package org.zb.demo;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;


public class EsClientIndexTest {
    RestHighLevelClient client;

    @BeforeEach
    void test1() throws IOException {
        client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200))
        );
    }

    @AfterEach
    void test2() throws IOException {
        client.close();
    }

    @Test
    void testCreate() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("user");

        CreateIndexResponse resp = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("索引操作："+resp.isAcknowledged());
    }

    @Test
    void testSearch() throws IOException {
        GetIndexRequest request = new GetIndexRequest("user");
        GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
        System.out.println("response.getAliases() = " + response.getAliases());
        System.out.println("response.getMappings() = " + response.getMappings());
        System.out.println("response.getSettings() = " + response.getSettings());
    }
    @Test
    void testDelete() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("user");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
    }
}
