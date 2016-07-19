package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "shopRepository")
public interface ShopRepository extends JpaRepository<Shop, Integer>, JpaSpecificationExecutor {

    Shop findByUsername(String userName);

    Shop findByUserBaseInfo_userIdAndIsDeletedFalse(Integer userId);

    Shop findByIdAndAgent(Integer id, Agent agent);

    List<Shop> findByAgent_IdAndIsDeletedFalse(Integer agentId);

    Shop findByIdAndCustomer(Integer id, MallCustomer customer);

    List<Shop> findByIsDeletedFalseAndIsDisabledFalseAndStatus(AgentStatusEnum status);

    List<Shop> findByIsDeletedFalseAndIsDisabledFalseAndStatusAndCustomer_CustomerId(AgentStatusEnum status, Integer customerId);
}
