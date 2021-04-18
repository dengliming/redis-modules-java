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
package io.github.dengliming.redismodule.redisearch.protocol.decoder;

import io.github.dengliming.redismodule.redisearch.index.Document;
import io.github.dengliming.redismodule.redisearch.search.SearchResult;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.decoder.MultiDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 */
public class SearchResultDecoder implements MultiDecoder<SearchResult> {

	private final boolean withScores;

	public SearchResultDecoder() {
		withScores = false;
	}

	public SearchResultDecoder(boolean b) {
		withScores = b;
	}

	@Override
	public Decoder<Object> getDecoder(int paramNum, State state) {
		return null;
	}

	@Override
	public SearchResult decode(List<Object> parts, State state) {

		Long total = (Long) parts.get(0);
		int documentSize = withScores ? 3 : 2;
		boolean noContent = total == parts.size() + 1;

		List<Document> documents = new ArrayList<>(total.intValue());

		// Checks the document size. DocumentSize equals to 2 means only key and parts. DocumentSize equals to 3 means
		// key, score and parts. Created separated IFs to avoid checking this logic each  document. Also  changed  the
		// step size to reduce numbers of interactions
		if (documentSize == 2) {

			//Only key and parts
			for (int i = 1; i < parts.size(); i += documentSize) {
				if (noContent) {
					documents.add(new Document((String) parts.get(i), 1.0d, null));
				} else {
					documents.add(new Document((String) parts.get(i), 1.0d, (Map<String, Object>) parts.get(i + 1)));
				}
			}

		} else {

			//Key, score and parts
			for (int i = 1; i < parts.size(); i += documentSize) {
				if (noContent) {
					documents.add(new Document((String) parts.get(i), (Double) parts.get(i + 1), null));
				} else {
					documents.add(new Document((String) parts.get(i), Double.parseDouble((String) parts.get(i + 1)), (Map<String, Object>) parts.get(i + 2)));
				}
			}

		}

		return new SearchResult(total, documents);

	}

}
