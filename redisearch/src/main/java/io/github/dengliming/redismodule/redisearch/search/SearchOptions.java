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

package io.github.dengliming.redismodule.redisearch.search;

import io.github.dengliming.redismodule.redisearch.index.RSLanguage;
import io.github.dengliming.redismodule.redisearch.protocol.Keywords;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dengliming
 */
public class SearchOptions {
    private boolean noContent;
    private boolean verbatim;
    private boolean noStopwords;
    private boolean withScores;
    private boolean withPayloads;
    private boolean withSortKeys;
    private String[] inKeys;
    private String[] inFields;
    private String[] returnFields;
    private String expander;
    private int slop;
    private boolean withInOrder;
    private List<Filter> filters;
    private RSLanguage language;
    private SummaryOptions summaryOptions;
    private HighlightOptions highlightOptions;
    private String scorer;
    private boolean explainScore;
    private String payload;
    private SortBy sortBy;
    private Page page;

    public SearchOptions() {
        this.filters = new LinkedList<>();
    }

    public SearchOptions noContent() {
        this.noContent = true;
        return this;
    }

    public SearchOptions verbatim() {
        this.verbatim = true;
        return this;
    }

    public SearchOptions noStopwords() {
        this.noStopwords = true;
        return this;
    }

    public SearchOptions withScores() {
        this.withScores = true;
        return this;
    }

    public SearchOptions withPayloads() {
        this.withPayloads = true;
        return this;
    }

    public SearchOptions withSortKeys() {
        this.withSortKeys = true;
        return this;
    }

    public SearchOptions inKeys(String... inKeys) {
        this.inKeys = inKeys;
        return this;
    }

    public SearchOptions inFields(String... inFields) {
        this.inFields = inFields;
        return this;
    }

    public SearchOptions returnFields(String... returnFields) {
        this.returnFields = returnFields;
        return this;
    }

    public SearchOptions expander(String expander) {
        this.expander = expander;
        return this;
    }

    public SearchOptions slop(int slop) {
        this.slop = slop;
        return this;
    }

    public SearchOptions withInOrder() {
        this.withInOrder = true;
        return this;
    }

    public SearchOptions filter(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    public SearchOptions language(RSLanguage language) {
        this.language = language;
        return this;
    }

    public SearchOptions summaryOptions(SummaryOptions summaryOptions) {
        this.summaryOptions = summaryOptions;
        return this;
    }

    public SearchOptions highlightOptions(HighlightOptions highlightOptions) {
        this.highlightOptions = highlightOptions;
        return this;
    }

    public SearchOptions page(Page page) {
        this.page = page;
        return this;
    }

    public SearchOptions scorer(String scorer) {
        this.scorer = scorer;
        return this;
    }

    public SearchOptions explainScore() {
        this.explainScore = true;
        return this;
    }

    public SearchOptions payload(String payload) {
        this.payload = payload;
        return this;
    }

    public SearchOptions sort(SortBy sortBy) {
        this.sortBy = sortBy;
        return this;
    }

	public boolean isWithScores() {
		return withScores;
	}

	public void build(List<Object> args) {
        if (noContent) {
            args.add(Keywords.NOCONTENT);
        }
        if (verbatim) {
            args.add(Keywords.VERBATIM);
        }
        if (noStopwords) {
            args.add(Keywords.NOSTOPWORDS);
        }
        if (withScores) {
            args.add(Keywords.WITHSCORES);
        }
        if (withPayloads) {
            args.add(Keywords.WITHPAYLOADS);
        }
        if (withSortKeys) {
            args.add(Keywords.WITHSORTKEYS);
        }
        if (filters != null) {
            filters.forEach(f -> f.build(args));
        }
        if (inKeys != null) {
            args.add(Keywords.INKEYS);
            args.add(inKeys.length);
            for (String inKey : inKeys) {
                args.add(inKey);
            }
        }
        if (inFields != null) {
            args.add(Keywords.INFIELDS);
            args.add(inFields.length);
            for (String inField : inFields) {
                args.add(inField);
            }
        }
        if (returnFields != null) {
            args.add(Keywords.RETURN);
            args.add(returnFields.length);
            for (String returnField : returnFields) {
                args.add(returnField);
            }
        }
        if (summaryOptions != null) {
            summaryOptions.build(args);
        }
        if (highlightOptions != null) {
            highlightOptions.build(args);
        }
        if (slop > 0) {
            args.add(Keywords.SLOP);
            args.add(slop);
        }
        if (withInOrder) {
            args.add(Keywords.INORDER);
        }
        if (language != null) {
            args.add(Keywords.LANGUAGE);
            args.add(language.name().toLowerCase());
        }
        if (expander != null) {
            args.add(Keywords.EXPANDER);
            args.add(expander);
        }
        if (scorer != null) {
            args.add(Keywords.SCORER);
            args.add(scorer);
        }
        if (explainScore) {
            args.add(Keywords.EXPLAINSCORE);
        }
        if (payload != null) {
            args.add(Keywords.PAYLOAD);
            args.add(payload);
        }
        if (sortBy != null) {
            sortBy.build(args);
        }
        if (page != null) {
            page.build(args);
        }
    }
}
