/*
 * Copyright 2015 Next Century Corporation
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
 *
 */

package com.ncc.neon.data

import org.apache.http.HttpHost
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.support.master.AcknowledgedResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.ElasticsearchException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ElasticSearchDataDeleter extends DefaultTask{
    // default value. build will override this
    String host = "elastic:10000"
    String databaseName = "neonintegrationtest"

    @TaskAction
    void run(){
        String[] connectionUrl = host.split(':', 2)
        String hostName = connectionUrl[0]
        int port = connectionUrl.length == 2 ? Integer.parseInt(connectionUrl[1]) : 9200

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port)))

        deleteIndex(client, databaseName)
    }

    /**
     * Delete the index.  Code is from:
     * http://programcreek.com/java-api-examples/index.php?api=org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
     */
    def deleteIndex(RestHighLevelClient client, indexName) {
        try {
            final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(databaseName)
            final AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT)

            if (!deleteIndexResponse.isAcknowledged()) {
                println("Index " + indexName + " not deleted")
            } else {
                println("Index " + indexName + " deleted")
            }
        }
        catch (ElasticsearchException e) {
            // Ignore, meh
        }
    }
}
