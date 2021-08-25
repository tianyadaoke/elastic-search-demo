package org.zb.demo;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.zb.demo.dao.ProductRepository;
import org.zb.demo.pojo.Product;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SpringDataEsTest {
    @Autowired
    ElasticsearchRestTemplate template;
    @Autowired
    ProductRepository repository;

    //创建索引
    @Test
    void createIndex() {
    }
    @Test
    void save(){
        Product product = new Product(2L,"华为手机","手机",2888.0,"http://asas.jpg");
        repository.save(product);
    }
    @Test
    void update(){
        Product product = new Product(2L,"华为手机","手机",2999.0,"http://asas.jpg");
        repository.save(product);
    }
    @Test
    void find(){
        System.out.println(repository.findById(2L).get());
    }
    @Test
    void findAll(){
        System.out.println(repository.findAll());
    }
    @Test
    void delete(){
        Product product = new Product();
        product.setId(2L);
        repository.delete(product);
    }
    @Test
    void findPage(){
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        int page=0;
        int size=5;
        Page<Product> products = repository.findAll(PageRequest.of(page, size));
        products.forEach(p-> System.out.println(p));
    }
    @Test
    void saveAll(){
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product product = new Product(Long.valueOf(i+1),"xiaomi mobile","mobile",1999.0+i,"httpjpa"+i);
            products.add(product);
        }
        repository.saveAll(products);
    }
    @Test
    void termQuery(){
        repository.findByTitle("xiaomi",PageRequest.of(0,7)).forEach(p-> System.out.println(p));
    }
}
