package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "authorRepository")
public interface AuthorRepository extends JpaRepository<Author, Integer>, JpaSpecificationExecutor {
    List<Author> findByCustomer_CustomerId(Integer customerId);

    List<Author> findByParentAuthor(Agent agent);

    Author findByUsername(String userName);
}
