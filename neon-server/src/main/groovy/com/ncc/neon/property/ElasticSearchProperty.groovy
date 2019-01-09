/*
 * Copyright 2016 Next Century Corporation
 * Licensed under the Apache License, Version 2.0 (the "License")
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
 *
 */

package com.ncc.neon.property

import com.ncc.neon.connect.ElasticSearchRestConnector
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value

import org.elasticsearch.action.admin.indices.get.GetIndexRequest
import org.elasticsearch.action.admin.indices.flush.FlushRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHits
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder

@Component("elasticsearch")
class ElasticSearchProperty implements PropertyInterface {

    @SuppressWarnings("GStringExpressionWithinString")
    @Value('\${propertiesDatabaseName}')
    String propertiesDatabaseName

    @SuppressWarnings("GStringExpressionWithinString")
    @Value('\${elasticSearchHost}')
    String elasticSearchHost

    @SuppressWarnings("GStringExpressionWithinString")
    @Value('\${elasticSearchPort}')
    String elasticSearchPort

    private final String propertiesTypeName = "properties"

    public Map getProperty(String key) {
        RestHighLevelClient client = getClient()
        Map toReturn = [key: key, value: null]

        if (doesDBExist(client)) {
            GetRequest request = new GetRequest(propertiesDatabaseName, propertiesTypeName, key)

            GetResponse resp = client.get(request, RequestOptions.DEFAULT)

            if (resp.isExists()) {
                String value = resp.getSourceAsMap().get("value")
                toReturn.put("value", value)
            }
        }

        return toReturn
    }

    public void setProperty(String key, String value) {
        RestHighLevelClient client = getClient()

        Map<String, String> json = [:]
        json.put("value", value)

        IndexRequest request = new IndexRequest(propertiesDatabaseName, propertiesTypeName, key)
        request.source(json)

        client.index(request, RequestOptions.DEFAULT)

        flush(client)
    }

    public void remove(String key) {
        RestHighLevelClient client = getClient()
        DeleteRequest request = new DeleteRequest(propertiesDatabaseName, propertiesTypeName, key)
        client.delete(request, RequestOptions.DEFAULT)

        flush(client)
    }

    public Set<String> propertyNames() {
        RestHighLevelClient client = getClient()
        Set<String> toReturn = [] as Set

        if (doesDBExist(client)) {
            SearchRequest request = getSearchAllRequest()

            SearchResponse resp = client.search(request, RequestOptions.DEFAULT)

            SearchHits hits = resp.getHits()
            for (SearchHit hit : hits.getHits()) {
                toReturn.add(hit.getId())
            }
        }

        return toReturn
    }

    public void removeAll() {
        RestHighLevelClient client = getClient()

        if (doesDBExist(client)) {
            SearchRequest request = getSearchAllRequest()

            SearchResponse resp = client.search(request, RequestOptions.DEFAULT)

            SearchHits hits = resp.getHits()
            for (SearchHit hit : hits.getHits()) {
                DeleteRequest deleteRequest = new DeleteRequest(propertiesDatabaseName, propertiesTypeName, hit.getId())

                client.delete(deleteRequest, RequestOptions.DEFAULT)
            }

            flush(client)
        }
    }

    private SearchRequest getSearchAllRequest() {
        SearchRequest request = new SearchRequest(propertiesDatabaseName)
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
        sourceBuilder.query(QueryBuilders.matchAllQuery())
        request.source(sourceBuilder)

        return request
    }

    private void flush(RestHighLevelClient client) {
        client.indices().flush(new FlushRequest(propertiesDatabaseName), RequestOptions.DEFAULT)
    }

    private boolean doesDBExist(RestHighLevelClient client) {
        GetIndexRequest request = new GetIndexRequest()
        request.indices((String[]) [propertiesDatabaseName])

        return client.indices().exists(request, RequestOptions.DEFAULT)
    }

    private RestHighLevelClient getClient() {
        def host = elasticSearchHost ?: "localhost"
        def port = elasticSearchPort ?: "9200"
        return ElasticSearchRestConnector.connectViaRest(host, Integer.parseInt(port))
    }
}
