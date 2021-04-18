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

package io.github.dengliming.redismodule.redisearch.protocol;

/**
 * @author dengliming
 */
public enum Keywords {
    MAXTEXTFIELDS, TEMPORARY, NOOFFSETS, NOHL, FUZZY, WITHSCORES, WITHPAYLOADS, MAX, PAYLOAD, INCR, LANGUAGE, REPLACE,
    DD, FIELDS, IF, NOSAVE, KEEPDOCS, NOINDEX, SORTABLE, PHONETIC, NOSTEM, WEIGHT, SEPARATOR, ADD, SCHEMA, STOPWORDS, PREFIX, NOFREQS, NOFIELDS,
    NOCONTENT, VERBATIM, NOSTOPWORDS, WITHSORTKEYS, FILTER, GEOFILTER, INKEYS, INFIELDS, RETURN, SUMMARIZE, FRAGS, LEN,
    HIGHLIGHT, TAGS, SLOP, INORDER, EXPANDER, SCORER, EXPLAINSCORE, SORTBY, LIMIT, AVG, REDUCE, STDDEV, COUNT, COUNT_DISTINCT,
    COUNT_DISTINCTISH, SUM, MIN, QUANTILE, TOLIST, FIRST_VALUE, BY, RANDOM_SAMPLE, GROUPBY, LOAD, APPLY, AS, DISTANCE, INCLUDE, EXCLUDE, TERMS;
}
