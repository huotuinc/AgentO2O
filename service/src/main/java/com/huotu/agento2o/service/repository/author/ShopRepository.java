package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "shopRepository")
public interface ShopRepository extends JpaRepository<ShopAuthor, Integer>, JpaSpecificationExecutor {

    ShopAuthor findByUsername(String userName);

    ShopAuthor findByUserBaseInfo_userId(Integer userId);

    ShopAuthor findByIdAndAgent(Integer id, Agent agent);

    List<ShopAuthor> findByAgent_Id(Integer agentId);

    ShopAuthor findByIdAndCustomer(Integer id, CustomerAuthor customer);

    List<ShopAuthor> findByIsDeletedFalseAndIsDeletedFalseAndStatus(AgentStatusEnum status);

    List<ShopAuthor> findByIsDeletedFalseAndIsDeletedFalseAndStatusAndCustomer_CustomerId(AgentStatusEnum status, Integer customerId);
}
