package org.zb.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zb.demo.pojo.User;

import java.io.IOException;


public class EsClientDocumentTest {
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
    // 新增
    @Test
    void testCreate() throws IOException {
        User user = new User();
        user.setName("张三");
        user.setAge(30);
        user.setSex("男");
        IndexRequest request = new IndexRequest("user");
        request.id("1001").source(new ObjectMapper().writeValueAsString(user), XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());
    }
    // 更新
    @Test
    void testUpdate() throws IOException {
        UpdateRequest request = new UpdateRequest("user","1001");
        request.doc(XContentType.JSON,"sex","女");
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.getResult());
    }
    // 查找
    @Test
    void testGet() throws IOException {
        GetRequest request = new GetRequest("user", "1001");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
    }
    // 删除
    @Test
    void testDelete() throws IOException {
        DeleteRequest request = new DeleteRequest("user", "1001");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }
    // 批量插入
    @Test
    void testCreateBulk() throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest().index("user").id("1001").source(XContentType.JSON,"name","张三","age",30,"sex","男"));
        request.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON,"name","李四","age",45,"sex","男"));
        request.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON,"name","王五","age",12,"sex","女"));
        request.add(new IndexRequest().index("user").id("1004").source(XContentType.JSON,"name","费六","age",80,"sex","男"));
        request.add(new IndexRequest().index("user").id("1005").source(XContentType.JSON,"name","航七","age",70,"sex","女"));
        request.add(new IndexRequest().index("user").id("1006").source(XContentType.JSON,"name","速八","age",51,"sex","男"));
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.getTook());
        System.out.println(response.getItems());
    }
    // 批量删除
    @Test
    void testDeleteBulk() throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest().index("user").id("1001"));
        request.add(new DeleteRequest().index("user").id("1002"));
        request.add(new DeleteRequest().index("user").id("1003"));
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.getTook());
        System.out.println(response.getItems());
    }
    // 全量查找
    @Test
    void testQuery() throws IOException {
        SearchRequest request = new SearchRequest("user")
            .source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });

    }
    // 条件查询
    @Test
    void testQueryCondition() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(new SearchSourceBuilder().query(QueryBuilders.termQuery("sex","男")));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });

    }
    // 分页查询
    @Test
    void testQueryPage() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .query(QueryBuilders.matchAllQuery())
                                .from(0)
                                .size(2));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });

    }

    // 排序查询
    @Test
    void testQuerySort() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .query(QueryBuilders.matchAllQuery())
                                .sort("age", SortOrder.ASC));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }

    // 过滤字段查询
    @Test
    void testQueryIncludeExclude() throws IOException {
        String[] include = {};
        String[] exclude={"age"};
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .query(QueryBuilders.matchAllQuery())
                                .fetchSource(include,exclude));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }
    // 组合Must查询
    @Test
    void testQueryBoolMust() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .query(QueryBuilders.boolQuery().must(
                                        QueryBuilders.matchQuery("sex","男")
                                ).must(
                                        QueryBuilders.matchQuery("age",30)
                                )));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }
    // 组合Should查询
    @Test
    void testQueryBoolShould() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .query(QueryBuilders.boolQuery().should(
                                        QueryBuilders.matchQuery("sex","女")
                                ).should(
                                        QueryBuilders.matchQuery("age",30)
                                )));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }

    // 范围查询
    @Test
    void testQueryRange() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .query(QueryBuilders.rangeQuery("age")
                                .gte(40)
                                .lte(70)));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }

    // 模糊查询
    @Test
    void testQueryFuzzy() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .query(QueryBuilders
                                        .fuzzyQuery("name","张五")
                                        .fuzziness(Fuzziness.ONE)));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }


    // 高亮查询
    @Test
    void testQueryHighlight() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .query(QueryBuilders
                                .termQuery("name","张"))
                                .highlighter(new HighlightBuilder().preTags("<em>").postTags("</em>").field("name")));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(response);
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }

    // 聚合查询
    @Test
    void testQueryAgg() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                            .aggregation(AggregationBuilders.max("maxAge").field("age")));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(response);
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }

    // 聚合分组查询
    @Test
    void testQueryAggGroup() throws IOException {
        SearchRequest request = new SearchRequest("user")
                .source(
                        new SearchSourceBuilder()
                                .aggregation(AggregationBuilders.terms("ageGroup").field("age")));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(response);
        System.out.println(hits.getTotalHits());
        hits.forEach(h->{
            System.out.println(h.getSourceAsString());
        });
    }
}
