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

import io.github.dengliming.redismodule.vectorset.args.AddArgs;
import io.github.dengliming.redismodule.vectorset.args.SimilarArgs;
import io.github.dengliming.redismodule.vectorset.model.Quantization;
import io.github.dengliming.redismodule.vectorset.model.RawVector;
import io.github.dengliming.redismodule.vectorset.model.Similarity;
import org.junit.jupiter.api.Test;
import org.redisson.api.BatchResult;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class VectorSetTest extends AbstractTest {

    private VectorSet populated(String name) {
        VectorSet vectorSet = getVectorSetClient().getVectorSet(name);
        assertThat(vectorSet.add("a", 1.0, 0.0, 0.0)).isTrue();
        assertThat(vectorSet.add("b", new double[]{0.0, 1.0, 0.0}, new AddArgs().attributes("{\"color\":\"red\"}"))).isTrue();
        assertThat(vectorSet.add("c", 0.9, 0.1, 0.0)).isTrue();
        return vectorSet;
    }

    @Test
    public void testAddAndInspect() {
        VectorSet vectorSet = populated("vs:basic");
        assertThat(vectorSet.cardinality()).isEqualTo(3);
        assertThat(vectorSet.dimension()).isEqualTo(3);
        assertThat(vectorSet.contains("a")).isTrue();
        assertThat(vectorSet.contains("nope")).isFalse();

        // updating an existing element reports false
        assertThat(vectorSet.add("a", 1.0, 0.0, 0.0)).isFalse();

        List<Double> vector = vectorSet.getVector("a");
        assertThat(vector).hasSize(3);
        assertThat(vector.get(0)).isCloseTo(1.0, within(0.05));

        RawVector raw = vectorSet.getRawVector("a");
        assertThat(raw.getQuantization()).isEqualTo("int8");
        assertThat(raw.getBlob()).hasSize(3);
        assertThat(raw.getNorm()).isCloseTo(1.0, within(0.01));
        assertThat(raw.getRange()).isNotNull();

        Map<String, Object> info = vectorSet.info();
        assertThat(info).containsEntry("vector-dim", 3L).containsEntry("size", 3L).containsKey("quant-type");

        assertThat(vectorSet.getVector("nope")).isNull();
    }

    @Test
    public void testSimilarity() {
        VectorSet vectorSet = populated("vs:sim");

        List<Similarity> byVector = vectorSet.similar(new double[]{1.0, 0.0, 0.0}, new SimilarArgs().withScores().count(2));
        assertThat(byVector).hasSize(2);
        assertThat(byVector.get(0).getElement()).isEqualTo("a");
        assertThat(byVector.get(0).getScore()).isGreaterThan(0.95);
        assertThat(byVector.get(1).getElement()).isEqualTo("c");

        List<Similarity> byElement = vectorSet.similarTo("b", new SimilarArgs().withScores().withAttribs());
        assertThat(byElement.get(0).getElement()).isEqualTo("b");
        assertThat(byElement.get(0).getAttributes()).contains("red");
        assertThat(byElement).extracting(Similarity::getElement).contains("a", "c");

        List<Similarity> filtered = vectorSet.similar(new double[]{1.0, 0.0, 0.0},
                new SimilarArgs().filter(".color == \"red\"").truth());
        assertThat(filtered).extracting(Similarity::getElement).containsExactly("b");
        assertThat(filtered.get(0).getScore()).isNull();

        List<Similarity> plain = vectorSet.similar(0.0, 1.0, 0.0);
        assertThat(plain.get(0).getElement()).isEqualTo("b");
    }

    @Test
    public void testAttributesLinksAndMembers() {
        VectorSet vectorSet = populated("vs:attr");

        assertThat(vectorSet.getAttributes("b")).contains("red");
        assertThat(vectorSet.getAttributes("a")).isNull();
        assertThat(vectorSet.setAttributes("a", "{\"k\":1}")).isTrue();
        assertThat(vectorSet.getAttributes("a")).contains("\"k\"");
        assertThat(vectorSet.setAttributes("nope", "{}")).isFalse();

        List<List<String>> links = vectorSet.links("a");
        assertThat(links).isNotEmpty();
        assertThat(links.get(0)).contains("c");
        List<List<Similarity>> scoredLinks = vectorSet.linksWithScores("a");
        assertThat(scoredLinks.get(0)).extracting(Similarity::getElement).contains("c");
        assertThat(scoredLinks.get(0).get(0).getScore()).isNotNull();
        assertThat(vectorSet.links("nope")).isNull();

        assertThat(vectorSet.randomMember()).isIn("a", "b", "c");
        assertThat(vectorSet.randomMembers(2)).hasSize(2);

        assertThat(vectorSet.members()).containsExactly("a", "b", "c");
        assertThat(vectorSet.range("[b", "+", 10)).containsExactly("b", "c");
        assertThat(vectorSet.range("-", "(b", 10)).containsExactly("a");

        assertThat(vectorSet.remove("c")).isTrue();
        assertThat(vectorSet.remove("c")).isFalse();
        assertThat(vectorSet.cardinality()).isEqualTo(2);
        assertThat(vectorSet.delete()).isTrue();
        assertThat(vectorSet.cardinality()).isEqualTo(0);
    }

    @Test
    public void testFp32AndQuantization() {
        VectorSet vectorSet = getVectorSetClient().getVectorSet("vs:fp32");
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putFloat(0.5f).putFloat(0.5f);
        assertThat(vectorSet.add("x", buffer.array(), new AddArgs().quantization(Quantization.NOQUANT))).isTrue();
        assertThat(vectorSet.dimension()).isEqualTo(2);
        // the server reports "f32" for unquantized vectors (the docs call it fp32)
        assertThat(vectorSet.getRawVector("x").getQuantization()).isEqualTo("f32");

        List<Similarity> similar = vectorSet.similar(buffer.array(), new SimilarArgs().withScores());
        assertThat(similar.get(0).getElement()).isEqualTo("x");
        assertThat(similar.get(0).getScore()).isCloseTo(1.0, within(0.01));
    }

    @Test
    public void testPipelining() {
        VectorSetBatch batch = getVectorSetClient().createVectorSetBatch();
        VectorSet vectorSet = batch.getVectorSet("vs:batch");
        vectorSet.addAsync("a", 1.0, 0.0);
        vectorSet.addAsync("b", 0.0, 1.0);
        vectorSet.cardinalityAsync();
        BatchResult<?> result = batch.execute();
        assertThat(result.getResponses()).containsExactly(true, true, 2L);
    }
}
