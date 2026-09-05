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

package io.github.dengliming.redismodule.examples.vectorset;

import io.github.dengliming.redismodule.vectorset.VectorSet;
import io.github.dengliming.redismodule.vectorset.args.AddArgs;
import io.github.dengliming.redismodule.vectorset.args.SimilarArgs;
import io.github.dengliming.redismodule.vectorset.client.VectorSetClient;
import io.github.dengliming.redismodule.vectorset.model.Similarity;
import org.redisson.config.Config;

public class Examples {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        VectorSetClient client = new VectorSetClient(config);

        VectorSet movies = client.getVectorSet("movies");
        movies.add("matrix", new double[]{0.9, 0.1, 0.0}, new AddArgs().attributes("{\"year\":1999,\"genre\":\"sci-fi\"}"));
        movies.add("inception", new double[]{0.8, 0.2, 0.1}, new AddArgs().attributes("{\"year\":2010,\"genre\":\"sci-fi\"}"));
        movies.add("amelie", new double[]{0.0, 0.2, 0.9}, new AddArgs().attributes("{\"year\":2001,\"genre\":\"romance\"}"));

        // nearest neighbours of a query vector, with scores and attributes
        for (Similarity hit : movies.similar(new double[]{1.0, 0.0, 0.0}, new SimilarArgs().withScores().withAttribs().count(2))) {
            System.out.println(hit.getElement() + " " + hit.getScore() + " " + hit.getAttributes());
        }

        // neighbours of an existing element, filtered on attributes
        for (Similarity hit : movies.similarTo("matrix", new SimilarArgs().filter(".year > 2000"))) {
            System.out.println("after 2000: " + hit.getElement());
        }

        System.out.println(movies.cardinality() + " vectors of " + movies.dimension() + " dimensions");
        System.out.println(movies.members());

        movies.delete();
        client.shutdown();
    }
}
