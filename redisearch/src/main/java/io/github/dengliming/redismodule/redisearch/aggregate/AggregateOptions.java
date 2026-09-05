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

package io.github.dengliming.redismodule.redisearch.aggregate;

import io.github.dengliming.redismodule.redisearch.protocol.Keywords;
import io.github.dengliming.redismodule.redisearch.search.Page;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 */
public class AggregateOptions {

    private boolean verbatim;
    private String[] loadFields;
    private Group[] groups;
    private SortBy sortBy;
    private Apply[] applies;
    private Page[] limits;
    private Filter[] filters;
    private Map<String, Object> params;
    private Integer dialect;
    private Long timeout;

    public AggregateOptions groups(Group... groups) {
        this.groups = groups;
        return this;
    }

    public AggregateOptions verbatim() {
        this.verbatim = true;
        return this;
    }

    public AggregateOptions loads(String... loadFields) {
        this.loadFields = loadFields;
        return this;
    }

    public AggregateOptions sortBy(SortBy sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public AggregateOptions applys(Apply... applies) {
        this.applies = applies;
        return this;
    }

    public AggregateOptions filters(Filter... filters) {
        this.filters = filters;
        return this;
    }

    public AggregateOptions limits(Page... pages) {
        this.limits = pages;
        return this;
    }


    /**
     * Binds a query parameter referenced as {@code $name} in the query (PARAMS). Vector blobs are passed as
     * {@code byte[]}, see {@link io.github.dengliming.redismodule.redisearch.search.Vectors}. Requires dialect 2+.
     */
    public AggregateOptions param(String name, Object value) {
        if (params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(name, value);
        return this;
    }

    public AggregateOptions params(Map<String, Object> params) {
        params.forEach(this::param);
        return this;
    }

    /**
     * Query dialect (DIALECT); vector queries and PARAMS need 2 or higher.
     */
    public AggregateOptions dialect(int dialect) {
        this.dialect = dialect;
        return this;
    }

    /**
     * Maximum processing time in milliseconds (TIMEOUT).
     */
    public AggregateOptions timeout(long timeoutMillis) {
        this.timeout = timeoutMillis;
        return this;
    }

    public void build(List<Object> args) {
        if (verbatim) {
            args.add(Keywords.VERBATIM);
        }
        if (loadFields != null) {
            args.add(Keywords.LOAD);
            args.add(loadFields.length);
            for (String loadField : loadFields) {
                args.add(loadField);
            }
        }

        if (groups != null) {
            for (Group group : groups) {
                group.build(args);
            }
        }

        if (sortBy != null) {
            sortBy.build(args);
        }

        if (applies != null) {
            for (Apply apply : applies) {
                apply.build(args);
            }
        }

        if (limits != null) {
            for (Page limit : limits) {
                limit.build(args);
            }
        }

        if (filters != null) {
            for (Filter filter : filters) {
                filter.build(args);
            }
        }
        if (timeout != null) {
            args.add(Keywords.TIMEOUT);
            args.add(timeout);
        }
        if (params != null && !params.isEmpty()) {
            args.add(Keywords.PARAMS);
            args.add(params.size() * 2);
            params.forEach((name, value) -> {
                args.add(name);
                args.add(value);
            });
        }
        if (dialect != null) {
            args.add(Keywords.DIALECT);
            args.add(dialect);
        }
    }
}
