package org.zb.demo.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.zb.demo.pojo.Product;

@Repository
public interface ProductRepository extends ElasticsearchRepository<Product,Long> {
    Page findByTitle(String title, Pageable pageable);
}
