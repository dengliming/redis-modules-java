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

import io.github.dengliming.redismodule.redisearch.protocol.Keywords;
import java.util.List;

/**
 * @author dengliming
 */
public class GeoFilter extends Filter {

    private final double lon;
    private final double lat;
    private final double radius;
    private final Unit unit;

    public GeoFilter(String field, double lon, double lat, double radius, Unit unit) {
        super(field);
        this.lon = lon;
        this.lat = lat;
        this.radius = radius;
        this.unit = unit;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getRadius() {
        return radius;
    }

    public Unit getUnit() {
        return unit;
    }

    public enum Unit {
        KILOMETERS("km"),
        METERS("m"),
        FEET("ft"),
        MILES("mi");

        private String code;

        Unit(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public void build(List<Object> args) {
        args.add(Keywords.GEOFILTER);
        args.add(this.getField());
        args.add(this.getLon());
        args.add(this.getLat());
        args.add(this.getRadius());
        args.add(this.getUnit().code);
    }
}
