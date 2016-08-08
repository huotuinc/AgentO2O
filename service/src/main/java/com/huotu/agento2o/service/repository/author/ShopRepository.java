package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "shopRepository")
public interface ShopRepository extends JpaRepository<Shop, Integer>, JpaSpecificationExecutor {

//    Shop findByUsername(String userName);

    Shop findByUserBaseInfo_userIdAndIsDeletedFalse(Integer userId);

    // TODO: 2016/7/28  
    Shop findByIdAndAgent(Integer id, Agent agent);

    /**
     * 用于界面上的 select , 只需要 id 和 name
     * @param agentId
     * @return
     */
    @Query("select new com.huotu.agento2o.service.entity.author.Shop(a.id,a.name) from Shop a where a.agent.id = ?1 and a.isDisabled = false")
    List<Shop> findByAgent_IdAndIsDeletedFalse(Integer agentId);

    // TODO: 2016/7/28  
    Shop findByIdAndCustomer(Integer id, MallCustomer customer);

    List<Shop> findByIsDeletedFalseAndIsDisabledFalseAndStatus(AgentStatusEnum status);

    List<Shop> findByIsDeletedFalseAndIsDisabledFalseAndStatusAndCustomer_CustomerId(AgentStatusEnum status, Integer customerId);
}
