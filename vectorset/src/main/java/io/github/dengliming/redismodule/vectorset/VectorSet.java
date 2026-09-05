/*
 * Copyright 2024 dengliming.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.dengliming.redismodule.vectorset;

import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.vectorset.args.AddArgs;
import io.github.dengliming.redismodule.vectorset.args.SimilarArgs;
import io.github.dengliming.redismodule.vectorset.model.RawVector;
import io.github.dengliming.redismodule.vectorset.model.Similarity;
import io.github.dengliming.redismodule.vectorset.protocol.Keywords;
import io.github.dengliming.redismodule.vectorset.protocol.decoder.SimilarityDecoder;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.decoder.ListMultiDecoder2;
import org.redisson.command.CommandAsyncExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VADD;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VCARD;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VDIM;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VEMB;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VEMB_RAW;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VGETATTR;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VINFO;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VISMEMBER;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VLINKS;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VLINKS_WITHSCORES;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VRANDMEMBER;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VRANDMEMBER_COUNT;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VRANGE;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VREM;
import static io.github.dengliming.redismodule.vectorset.protocol.RedisCommands.VSETATTR;

/**
 * A Redis vector set (Redis 8.0+): a collection of named elements with a vector each, indexed in an HNSW graph
 * for similarity search. Elements may carry JSON attributes that VSIM can filter on.
 * <p>
 * Vectors are passed either as {@code double[]} (sent as VALUES) or as a little-endian FP32 blob.
 */
public class VectorSet extends RedissonObject {

    private static final String RANGE_MIN = "-";
    private static final String RANGE_MAX = "+";

    public VectorSet(CommandAsyncExecutor commandExecutor, String name) {
        super(commandExecutor, name);
    }

    public VectorSet(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    /**
     * Adds an element with the given vector (VADD ... VALUES), creating the set if needed.
     *
     * @return true if the element was added, false if it already existed and was updated
     */
    public boolean add(String element, double... vector) {
        return add(element, vector, null);
    }

    public boolean add(String element, double[] vector, AddArgs args) {
        return get(addAsync(element, vector, args));
    }

    public RFuture<Boolean> addAsync(String element, double... vector) {
        return addAsync(element, vector, null);
    }

    public RFuture<Boolean> addAsync(String element, double[] vector, AddArgs args) {
        RAssert.notEmpty(vector, "vector must not be empty");

        List<Object> vectorArgs = new ArrayList<>(vector.length + 2);
        vectorArgs.add(Keywords.VALUES);
        vectorArgs.add(vector.length);
        for (double component : vector) {
            vectorArgs.add(component);
        }
        return addAsync(element, vectorArgs, args);
    }

    /**
     * Adds an element with a vector given as a little-endian FP32 blob (VADD ... FP32).
     */
    public boolean add(String element, byte[] fp32Vector, AddArgs args) {
        return get(addAsync(element, fp32Vector, args));
    }

    public RFuture<Boolean> addAsync(String element, byte[] fp32Vector, AddArgs args) {
        RAssert.notNull(fp32Vector, "vector must not be null");
        RAssert.isTrue(fp32Vector.length > 0 && fp32Vector.length % 4 == 0, "FP32 vector must be a non-empty multiple of 4 bytes");

        List<Object> vectorArgs = new ArrayList<>(2);
        vectorArgs.add(Keywords.FP32);
        vectorArgs.add(fp32Vector);
        return addAsync(element, vectorArgs, args);
    }

    private RFuture<Boolean> addAsync(String element, List<Object> vectorArgs, AddArgs addArgs) {
        RAssert.notEmpty(element, "element must not be empty");

        List<Object> args = new ArrayList<>();
        args.add(getName());
        if (addArgs != null) {
            addArgs.buildBeforeVector(args);
        }
        args.addAll(vectorArgs);
        args.add(element);
        if (addArgs != null) {
            addArgs.buildAfterElement(args);
        }
        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, VADD, args.toArray());
    }

    /**
     * Removes an element (VREM).
     *
     * @return true if the element existed
     */
    public boolean remove(String element) {
        return get(removeAsync(element));
    }

    public RFuture<Boolean> removeAsync(String element) {
        RAssert.notEmpty(element, "element must not be empty");

        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, VREM, getName(), element);
    }

    /**
     * Number of elements (VCARD); 0 when the key does not exist.
     */
    public long cardinality() {
        return get(cardinalityAsync());
    }

    public RFuture<Long> cardinalityAsync() {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VCARD, getName());
    }

    /**
     * Number of dimensions of the stored vectors (VDIM). Fails if the key does not exist.
     */
    public long dimension() {
        return get(dimensionAsync());
    }

    public RFuture<Long> dimensionAsync() {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VDIM, getName());
    }

    /**
     * Approximate vector of an element (VEMB), de-normalized and de-quantized; null if absent.
     */
    public List<Double> getVector(String element) {
        return get(getVectorAsync(element));
    }

    public RFuture<List<Double>> getVectorAsync(String element) {
        RAssert.notEmpty(element, "element must not be empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VEMB, getName(), element);
    }

    /**
     * Internal representation of an element's vector (VEMB ... RAW); null if absent.
     */
    public RawVector getRawVector(String element) {
        return get(getRawVectorAsync(element));
    }

    public RFuture<RawVector> getRawVectorAsync(String element) {
        RAssert.notEmpty(element, "element must not be empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VEMB_RAW, getName(), element, Keywords.RAW);
    }

    /**
     * JSON attributes of an element (VGETATTR); null when the element has none or does not exist.
     */
    public String getAttributes(String element) {
        return get(getAttributesAsync(element));
    }

    public RFuture<String> getAttributesAsync(String element) {
        RAssert.notEmpty(element, "element must not be empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VGETATTR, getName(), element);
    }

    /**
     * Sets (replaces) the JSON attributes of an element (VSETATTR).
     *
     * @param attributesJson a JSON object, or an empty string to remove the attributes
     * @return true if the element exists
     */
    public boolean setAttributes(String element, String attributesJson) {
        return get(setAttributesAsync(element, attributesJson));
    }

    public RFuture<Boolean> setAttributesAsync(String element, String attributesJson) {
        RAssert.notEmpty(element, "element must not be empty");
        RAssert.notNull(attributesJson, "attributes must not be null");

        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, VSETATTR, getName(), element, attributesJson);
    }

    /**
     * Metadata of the set (VINFO): quant-type, vector-dim, size, max-level, ...; null if the key does not exist.
     */
    public Map<String, Object> info() {
        return get(infoAsync());
    }

    public RFuture<Map<String, Object>> infoAsync() {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VINFO, getName());
    }

    /**
     * HNSW neighbours of an element, one list per layer (VLINKS); null if the element does not exist.
     */
    public List<List<String>> links(String element) {
        return get(linksAsync(element));
    }

    public RFuture<List<List<String>>> linksAsync(String element) {
        RAssert.notEmpty(element, "element must not be empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VLINKS, getName(), element);
    }

    /**
     * HNSW neighbours of an element with their similarity scores, one list per layer (VLINKS ... WITHSCORES).
     */
    public List<List<Similarity>> linksWithScores(String element) {
        return get(linksWithScoresAsync(element));
    }

    public RFuture<List<List<Similarity>>> linksWithScoresAsync(String element) {
        RAssert.notEmpty(element, "element must not be empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VLINKS_WITHSCORES, getName(), element, Keywords.WITHSCORES);
    }

    /**
     * Whether the element is part of the set (VISMEMBER).
     */
    public boolean contains(String element) {
        return get(containsAsync(element));
    }

    public RFuture<Boolean> containsAsync(String element) {
        RAssert.notEmpty(element, "element must not be empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VISMEMBER, getName(), element);
    }

    /**
     * One random element (VRANDMEMBER); null if the key does not exist.
     */
    public String randomMember() {
        return get(randomMemberAsync());
    }

    public RFuture<String> randomMemberAsync() {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VRANDMEMBER, getName());
    }

    /**
     * Random elements (VRANDMEMBER count). A positive count returns distinct elements, a negative count may repeat.
     */
    public List<String> randomMembers(int count) {
        return get(randomMembersAsync(count));
    }

    public RFuture<List<String>> randomMembersAsync(int count) {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VRANDMEMBER_COUNT, getName(), count);
    }

    /**
     * Elements in lexicographical order (VRANGE, Redis 8.4+). Bounds use the ZRANGEBYLEX syntax:
     * {@code [x} inclusive, {@code (x} exclusive, {@code -} minimum, {@code +} maximum.
     *
     * @param count maximum number of elements; negative returns everything in range
     */
    public List<String> range(String start, String end, int count) {
        return get(rangeAsync(start, end, count));
    }

    public RFuture<List<String>> rangeAsync(String start, String end, int count) {
        RAssert.notEmpty(start, "start must not be empty");
        RAssert.notEmpty(end, "end must not be empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, VRANGE, getName(), start, end, count);
    }

    /**
     * All elements in lexicographical order (VRANGE - + -1). Use {@link #range(String, String, int)} to page.
     */
    public List<String> members() {
        return range(RANGE_MIN, RANGE_MAX, -1);
    }

    /**
     * Elements most similar to a query vector (VSIM ... VALUES).
     */
    public List<Similarity> similar(double... vector) {
        return similar(vector, new SimilarArgs());
    }

    public List<Similarity> similar(double[] vector, SimilarArgs args) {
        return get(similarAsync(vector, args));
    }

    public RFuture<List<Similarity>> similarAsync(double[] vector, SimilarArgs args) {
        RAssert.notEmpty(vector, "vector must not be empty");

        List<Object> queryArgs = new ArrayList<>(vector.length + 2);
        queryArgs.add(Keywords.VALUES);
        queryArgs.add(vector.length);
        for (double component : vector) {
            queryArgs.add(component);
        }
        return similarAsync(queryArgs, args);
    }

    /**
     * Elements most similar to a query vector given as a little-endian FP32 blob (VSIM ... FP32).
     */
    public List<Similarity> similar(byte[] fp32Vector, SimilarArgs args) {
        return get(similarAsync(fp32Vector, args));
    }

    public RFuture<List<Similarity>> similarAsync(byte[] fp32Vector, SimilarArgs args) {
        RAssert.notNull(fp32Vector, "vector must not be null");

        List<Object> queryArgs = new ArrayList<>(2);
        queryArgs.add(Keywords.FP32);
        queryArgs.add(fp32Vector);
        return similarAsync(queryArgs, args);
    }

    /**
     * Elements most similar to an existing element (VSIM ... ELE). The element itself is part of the result.
     */
    public List<Similarity> similarTo(String element) {
        return similarTo(element, new SimilarArgs());
    }

    public List<Similarity> similarTo(String element, SimilarArgs args) {
        return get(similarToAsync(element, args));
    }

    public RFuture<List<Similarity>> similarToAsync(String element, SimilarArgs args) {
        RAssert.notEmpty(element, "element must not be empty");

        List<Object> queryArgs = new ArrayList<>(2);
        queryArgs.add(Keywords.ELE);
        queryArgs.add(element);
        return similarAsync(queryArgs, args);
    }

    private RFuture<List<Similarity>> similarAsync(List<Object> queryArgs, SimilarArgs similarArgs) {
        SimilarArgs options = similarArgs == null ? new SimilarArgs() : similarArgs;
        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.addAll(queryArgs);
        options.build(args);
        RedisCommand<List<Similarity>> command = new RedisCommand<>("VSIM",
                new ListMultiDecoder2(new SimilarityDecoder(options.isWithScores(), options.isWithAttribs())));
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, command, args.toArray());
    }
}
