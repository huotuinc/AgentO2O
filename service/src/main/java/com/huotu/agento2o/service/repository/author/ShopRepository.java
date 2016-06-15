package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "shopRepository")
public interface ShopRepository extends JpaRepository<Shop, Integer>, JpaSpecificationExecutor {

    Shop findByUsername(String userName);

    Shop findByUserBaseInfo_userId(Integer userId);

    Shop findByIdAndParentAuthor(Integer id, Agent agent);

    Shop findByIdAndCustomer(Integer id, MallCustomer customer);

    List<Shop> findByIsDeletedFalseAndIsDeletedFalseAndStatus(AgentStatusEnum status);

    List<Shop> findByIsDeletedFalseAndIsDeletedFalseAndStatusAndCustomer_CustomerId(AgentStatusEnum status,Integer customerId);
}
