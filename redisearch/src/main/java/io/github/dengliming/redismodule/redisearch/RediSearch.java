/*
 * Copyright 2020 dengliming.
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
package io.github.dengliming.redismodule.redisearch;

import io.github.dengliming.redismodule.common.util.ArgsUtil;
import io.github.dengliming.redismodule.common.util.RAssert;
import io.github.dengliming.redismodule.redisearch.aggregate.AggregateOptions;
import io.github.dengliming.redismodule.redisearch.aggregate.AggregateResult;
import io.github.dengliming.redismodule.redisearch.index.*;
import io.github.dengliming.redismodule.redisearch.protocol.Keywords;
import io.github.dengliming.redismodule.redisearch.index.schema.Field;
import io.github.dengliming.redismodule.redisearch.index.schema.Schema;
import io.github.dengliming.redismodule.redisearch.index.schema.TagField;
import io.github.dengliming.redismodule.redisearch.index.schema.TextField;
import io.github.dengliming.redismodule.redisearch.search.MisspelledTerm;
import io.github.dengliming.redismodule.redisearch.search.SearchOptions;
import io.github.dengliming.redismodule.redisearch.search.SearchResult;
import io.github.dengliming.redismodule.redisearch.search.SpellCheckOptions;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.command.CommandAsyncExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.dengliming.redismodule.redisearch.protocol.RedisCommands.*;

/**
 * @author dengliming
 */
public class RediSearch extends RedissonObject {

    public RediSearch(Codec codec, CommandAsyncExecutor commandExecutor, String name) {
        super(codec, commandExecutor, name);
    }

    public RediSearch(CommandAsyncExecutor commandExecutor, String name) {
        this(commandExecutor.getConnectionManager().getCodec(), commandExecutor, name);
    }

    /**
     * Creates an index with the given spec
     *
     * @param schema
     * @return
     */
    public boolean createIndex(Schema schema) {
        return this.createIndex(schema, IndexOptions.defaultOptions());
    }

    public boolean createIndex(Schema schema, IndexOptions indexOptions) {
        return get(createIndexAsync(schema, indexOptions));
    }

    public RFuture<Boolean> createIndexAsync(Schema schema, IndexOptions indexOptions) {
        RAssert.notNull(indexOptions, "IndexOptions must be not null");
        RAssert.notNull(schema, "IndexSchema must be not null");

        List<Object> args = new ArrayList<>();
        args.add(getName());
        indexOptions.build(args);
        args.add(Keywords.SCHEMA.name());
        schema.getFields().forEach(field -> appendFieldArgs(args, field));
        return commandExecutor.writeAsync(getName(), codec, FT_CREATE, args.toArray());
    }

    public boolean alterIndex(Field... fields) {
        return get(alterIndexAsync(fields));
    }

    public RFuture<Boolean> alterIndexAsync(Field... fields) {
        RAssert.notEmpty(fields, "Fields must be not empty");

        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.add(Keywords.SCHEMA.name());
        args.add(Keywords.ADD.name());
        for (Field field : fields) {
            appendFieldArgs(args, field);
        }
        return commandExecutor.writeAsync(getName(), codec, FT_ALTER, args.toArray());
    }

    private void appendFieldArgs(List<Object> args, Field field) {
        args.add(field.getName());
        args.add(field.getFieldType().name());
        switch (field.getFieldType()) {
            case TAG:
                args.add(Keywords.SEPARATOR.name());
                args.add(((TagField) field).getSeparator());
                break;
            case TEXT:
                TextField textField = (TextField) field;
                args.add(Keywords.WEIGHT.name());
                args.add(textField.getWeight());
                if (textField.isNoStem()) {
                    args.add(Keywords.NOSTEM.name());
                }
                if (textField.getPhonetic() != null) {
                    args.add(Keywords.PHONETIC.name());
                    args.add(textField.getPhonetic().name());
                }
                break;
        }

        if (field.isSortable()) {
            args.add(Keywords.SORTABLE.name());
        }
        if (field.isNoIndex()) {
            args.add(Keywords.NOINDEX.name());
        }
    }

    /**
     * Deletes all the keys associated with the index.
     *
     * @return
     */
    public boolean dropIndex() {
        return dropIndex(false);
    }

    public boolean dropIndex(boolean keepDocs) {
        return get(dropIndexAsync(keepDocs));
    }

    public RFuture<Boolean> dropIndexAsync(boolean keepDocs) {
        if (keepDocs) {
            return commandExecutor.writeAsync(getName(), codec, FT_DROP, getName(), Keywords.KEEPDOCS.name());
        }
        return commandExecutor.writeAsync(getName(), codec, FT_DROP, getName());
    }

    public Map<String, Object> loadIndex() {
        return get(loadIndexAsync());
    }

    public RFuture<Map<String, Object>> loadIndexAsync() {
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, FT_INFO, getName());
    }

    /**
     * Adds a document to the index.
     *
     * @param document
     * @return
     */
    public boolean addDocument(Document document) {
        return addDocument(document, new DocumentOptions());
    }

    public boolean addDocument(Document document, DocumentOptions options) {
        return get(addDocumentAsync(document, options));
    }

    public RFuture<Boolean> addDocumentAsync(Document document, DocumentOptions options) {
        RAssert.notNull(document, "Document must be not null");
        RAssert.notNull(options, "DocumentOptions must be not null");

        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.add(document.getId());
        args.add(document.getScore());
        if (document.getPayload() != null) {
            args.add(Keywords.PAYLOAD.name());
            args.add(document.getPayload());
        }
        options.build(args);
        args.add(Keywords.FIELDS.name());
        document.getFields().forEach((k, v) -> {
            args.add(k);
            args.add(v);
        });
        return commandExecutor.writeAsync(getName(), codec, FT_ADD, args.toArray());
    }

    /**
     * Returns the full contents of a document.
     *
     * @param docId
     * @return
     */
    public Map<String, Object> getDocument(String docId) {
        return get(getDocumentAsync(docId));
    }

    public RFuture<Map<String, Object>> getDocumentAsync(String docId) {
        RAssert.notNull(docId, "docId must be not null");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, FT_GET, getName(), docId);
    }

    /**
     * Returns the full contents of multiple documents.
     *
     * @param docIds
     * @return
     */
    public List<Map<String, Object>> getDocuments(String... docIds) {
        return get(getDocumentsAsync(docIds));
    }

    public RFuture<List<Map<String, Object>>> getDocumentsAsync(String... docIds) {
        RAssert.notEmpty(docIds, "docIds must be not empty");

        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, FT_MGET, ArgsUtil.append(getName(), docIds));
    }

    /**
     * Deletes a document from the index.
     *
     * @param docId
     * @return
     */
    public boolean deleteDocument(String docId) {
        return this.deleteDocument(docId, false);
    }

    public boolean deleteDocument(String docId, boolean deleteDocument) {
        return get(deleteDocumentAsync(docId, deleteDocument));
    }

    public RFuture<Boolean> deleteDocumentAsync(String docId, boolean deleteDocument) {
        RAssert.notNull(docId, "docId must be not null");

        if (deleteDocument) {
            return commandExecutor.writeAsync(getName(), codec, FT_DEL, getName(), docId, Keywords.DD.name());
        }
        return commandExecutor.writeAsync(getName(), codec, FT_DEL, getName(), docId);
    }

    /**
     * Adds a document to the index from an existing HASH key in Redis.
     *
     * @param docId
     * @param score
     * @return
     */
    public boolean addHash(String docId, double score) {
        return this.addHash(docId, score, null);
    }

    public boolean addHash(String docId, double score, RSLanguage language) {
        return this.addHash(docId, score, language, false);
    }

    public boolean addHash(String docId, double score, RSLanguage language, boolean replace) {
        return get(addHashAsync(docId, score, language, replace));
    }

    public RFuture<Boolean> addHashAsync(String docId, double score, RSLanguage language, boolean replace) {
        RAssert.notNull(docId, "docId must be not null");

        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.add(docId);
        args.add(score);
        if (language != null) {
            args.add(Keywords.LANGUAGE.name());
            args.add(language.name().toLowerCase());
        }
        if (replace) {
            args.add(Keywords.REPLACE.name());
        }
        return commandExecutor.writeAsync(getName(), codec, FT_ADDHASH, args.toArray());
    }

    public boolean addAlias(String alias) {
        return get(addAliasAsync(alias));
    }

    public RFuture<Boolean> addAliasAsync(String alias) {
        RAssert.notNull(alias, "alias must be not null");

        return commandExecutor.writeAsync(getName(), codec, FT_ALIASADD, alias, getName());
    }

    public boolean updateAlias(String alias) {
        return get(updateAliasAsync(alias));
    }

    public RFuture<Boolean> updateAliasAsync(String alias) {
        RAssert.notNull(alias, "alias must be not null");

        return commandExecutor.writeAsync(getName(), codec, FT_ALIASUPDATE, alias, getName());
    }

    public boolean deleteAlias(String alias) {
        return get(deleteAliasAsync(alias));
    }

    public RFuture<Boolean> deleteAliasAsync(String alias) {
        RAssert.notNull(alias, "alias must be not null");

        return commandExecutor.writeAsync(getName(), codec, FT_ALIASDEL, alias);
    }

    public String explain(String query) {
        return get(explainAsync(query));
    }

    public RFuture<String> explainAsync(String query) {
        RAssert.notNull(query, "query must be not null");

        return commandExecutor.readAsync(getName(), codec, FT_EXPLAIN, getName(), query);
    }

    public boolean setConfig(ConfigOption option, String value) {
        return get(setConfigAsync(option, value));
    }

    public RFuture<Boolean> setConfigAsync(ConfigOption option, String value) {
        RAssert.notNull(option, "ConfigOption must be not null");
        RAssert.notNull(value, "value must be not null");

        return commandExecutor.writeAsync(getName(), codec, FT_CONFIG_SET, option.getKeyword(), value);
    }

    public Map<String, String> getConfig(ConfigOption option) {
        return get(getConfigAsync(option));
    }

    public RFuture<Map<String, String>> getConfigAsync(ConfigOption option) {
        RAssert.notNull(option, "ConfigOption must be not null");

        return commandExecutor.readAsync(getName(), codec, FT_CONFIG_GET, option.getKeyword());
    }

    public Map<String, String> getHelp(ConfigOption option) {
        return get(getHelpAsync(option));
    }

    public RFuture<Map<String, String>> getHelpAsync(ConfigOption option) {
        RAssert.notNull(option, "ConfigOption must be not null");

        return commandExecutor.readAsync(getName(), codec, FT_CONFIG_HELP, option.getKeyword());
    }

    /**
     * Adds terms to a dictionary.
     *
     * @param dictName
     * @param terms
     * @return
     */
    public int addDict(String dictName, String... terms) {
        return get(addDictAsync(dictName, terms));
    }

    public RFuture<Integer> addDictAsync(String dictName, String... terms) {
        RAssert.notNull(dictName, "dictName must be not null");
        RAssert.notEmpty(terms, "terms must be not empty");

        return commandExecutor.writeAsync(getName(), codec, FT_DICTADD, ArgsUtil.append(dictName, terms));
    }

    /**
     * Deletes terms from a dictionary.
     *
     * @param dictName
     * @param terms
     * @return
     */
    public int deleteDict(String dictName, String... terms) {
        return get(deleteDictAsync(dictName, terms));
    }

    public RFuture<Integer> deleteDictAsync(String dictName, String... terms) {
        RAssert.notNull(dictName, "dictName must be not null");
        RAssert.notEmpty(terms, "terms must be not empty");

        return commandExecutor.writeAsync(getName(), codec, FT_DICTDEL, ArgsUtil.append(dictName, terms));
    }

    /**
     * Dumps all terms in the given dictionary.
     *
     * @param dictName
     * @return
     */
    public List<String> dumpDict(String dictName) {
        return get(dumpDictAsync(dictName));
    }

    public RFuture<List<String>> dumpDictAsync(String dictName) {
        RAssert.notNull(dictName, "dictName must be not null");

        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, FT_DICTDUMP, dictName);
    }

    /**
     * Adds a synonym group.
     *
     * @param terms
     * @return
     */
    public long addSynonym(String... terms) {
        return get(addSynonymAsync(terms));
    }

    public RFuture<Long> addSynonymAsync(String... terms) {
        RAssert.notEmpty(terms, "terms must be not empty");

        return commandExecutor.writeAsync(getName(), codec, FT_SYNADD, ArgsUtil.append(getName(), terms));
    }

    /**
     * Updates a synonym group.
     *
     * @param synonymGroupId
     * @param terms
     * @return
     */
    public boolean updateSynonym(long synonymGroupId, String... terms) {
        return get(updateSynonymAsync(synonymGroupId, terms));
    }

    public RFuture<Boolean> updateSynonymAsync(long synonymGroupId, String... terms) {
        RAssert.notEmpty(terms, "terms must be not empty");

        List<Object> args = new ArrayList<>(terms.length + 2);
        args.add(getName());
        args.add(synonymGroupId);
        Arrays.stream(terms).forEach(args::add);
        return commandExecutor.writeAsync(getName(), codec, FT_SYNUPDATE, args.toArray());
    }

    /**
     * Dumps the contents of a synonym group.
     *
     * @return
     */
    public Map<String, List<Long>> dumpSynonyms() {
        return get(dumpSynonymsAsync());
    }

    public RFuture<Map<String, List<Long>>> dumpSynonymsAsync() {
        return commandExecutor.writeAsync(getName(), StringCodec.INSTANCE, FT_SYNDUMP, getName());
    }

    /**
     * Returns the distinct tags indexed in a Tag field.
     *
     * @param fieldName
     * @return
     */
    public List<String> getTagVals(String fieldName) {
        return get(getTagValsAsync(fieldName));
    }

    public RFuture<List<String>> getTagValsAsync(String fieldName) {
        RAssert.notNull(fieldName, "fieldName must be not null");

        return commandExecutor.readAsync(getName(), codec, FT_TAGVALS, getName(), fieldName);
    }

    /**
     * Adds a suggestion string to an auto-complete suggestion dictionary.
     *
     * @param suggestion
     * @return
     */
    public int addSuggestion(Suggestion suggestion) {
        return addSuggestion(suggestion, false);
    }

    public int addSuggestion(Suggestion suggestion, boolean increment) {
        return get(addSuggestionAsync(suggestion, increment));
    }

    public RFuture<Integer> addSuggestionAsync(Suggestion suggestion, boolean increment) {
        RAssert.notNull(suggestion, "Suggestion must be not null");

        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.add(suggestion.getTerm());
        args.add(suggestion.getScore());
        if (increment) {
            args.add(Keywords.INCR.name());
        }
        if (suggestion.getPayload() != null) {
            args.add(Keywords.PAYLOAD.name());
            args.add(suggestion.getPayload());
        }
        return commandExecutor.writeAsync(getName(), codec, FT_SUGADD, args.toArray());
    }

    /**
     * Gets the size of an auto-complete suggestion dictionary
     *
     * @return
     */
    public int getSuggestionLength() {
        return get(getSuggestionLengthAsync());
    }

    public RFuture<Integer> getSuggestionLengthAsync() {
        return commandExecutor.readAsync(getName(), codec, FT_SUGLEN, getName());
    }

    /**
     * Deletes a string from a suggestion index.
     *
     * @param term
     * @return
     */
    public boolean deleteSuggestion(String term) {
        return get(deleteSuggestionAsync(term));
    }

    public RFuture<Boolean> deleteSuggestionAsync(String term) {
        RAssert.notNull(term, "term must be not null");

        return commandExecutor.readAsync(getName(), codec, FT_SUGDEL, getName(), term);
    }

    /**
     * Gets completion suggestions for a prefix.
     *
     * @param prefix
     * @param options
     * @return
     */
    public List<Suggestion> getSuggestion(String prefix, SuggestionOptions options) {
        return get(getSuggestionAsync(prefix, options));
    }

    public RFuture<List<Suggestion>> getSuggestionAsync(String prefix, SuggestionOptions options) {
        RAssert.notNull(prefix, "prefix must be not null");
        RAssert.notNull(options, "SuggestionOptions must be not null");

        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.add(prefix);
        options.build(args);
        return commandExecutor.readAsync(getName(), codec, FT_SUGGET, args.toArray());
    }

    /**
     * Searches the index with a textual query, returning either documents or just ids.
     *
     * @param query
     * @param searchOptions
     * @return
     */
    public SearchResult search(String query, SearchOptions searchOptions) {
        return get(searchAsync(query, searchOptions));
    }

    public RFuture<SearchResult> searchAsync(String query, SearchOptions searchOptions) {
        checkQueryArgument(query);
        RAssert.notNull(searchOptions, "SearchOptions must be not null");

		List<Object> args = new ArrayList<>();
		args.add(getName());
		args.add(query);
		searchOptions.build(args);
		return commandExecutor.readAsync(
				getName(),
				StringCodec.INSTANCE,
				searchOptions.isWithScores() ? FT_SEARCH_WITH_SCORES : FT_SEARCH,
				args.toArray()
		);
	}

    /**
     * Runs a search query on an index, and performs aggregate transformations on the results.
     *
     * @param query
     * @param aggregateOptions
     * @return
     */
    public AggregateResult aggregate(String query, AggregateOptions aggregateOptions) {
        return get(aggregateAsync(query, aggregateOptions));
    }

    public RFuture<AggregateResult> aggregateAsync(String query, AggregateOptions aggregateOptions) {
        checkQueryArgument(query);
        RAssert.notNull(aggregateOptions, "AggregateOptions must be not null");

        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.add(query);
        aggregateOptions.build(args);
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, FT_AGGREGATE, args.toArray());
    }

    /**
     * Performs spelling correction on a query, returning suggestions for misspelled terms.
     *
     * @param query
     * @param options
     * @return
     */
    public List<MisspelledTerm> spellCheck(String query, SpellCheckOptions options) {
        return get(spellCheckAsync(query, options));
    }

    public RFuture<List<MisspelledTerm>> spellCheckAsync(String query, SpellCheckOptions options) {
        checkQueryArgument(query);

        List<Object> args = new ArrayList<>();
        args.add(getName());
        args.add(query);
        if (options != null) {
            options.build(args);
        }
        return commandExecutor.readAsync(getName(), StringCodec.INSTANCE, FT_SPELLCHECK, args.toArray());
    }

    private void checkQueryArgument(String query) {
        RAssert.notNull(query, "query must be not null");
    }
}
