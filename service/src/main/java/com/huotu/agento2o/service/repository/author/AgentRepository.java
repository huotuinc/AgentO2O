package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.entity.author.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "agentRepository")
public interface AgentRepository extends JpaRepository<Agent, Integer>, JpaSpecificationExecutor {

    Agent findByIdAndCustomer_customerId(Integer agentId, Integer customerId);

    List<Agent> findByAgentLevel_levelIdAndIsDeletedFalse(Integer levelId);

    @Modifying(clearAutomatically = true)
    @Query("update Agent a set a.isDeleted = true where a.id = ?1")
    int deleteAgent(Integer agentId);

    @Modifying(clearAutomatically = true)
    @Query("update Agent a set a.isDisabled = ?2 where a.id = ?1")
    int updateDisabledStatus(Integer agentId, boolean status);

    /**
     * 查找下级代理商列表
     * @param agentId
     * @return
     */
    @Query("select new com.huotu.agento2o.service.entity.author.Agent(a.id,a.name) from Agent a where a.parentAgent.id = ?1 and a.isDeleted = false")
    List<Agent> findByParentAgent_IdAndIsDeletedFalse(Integer agentId);

    /**
     * 用于界面上的select，只需要id 和 name
     * @param customerId
     * @return
     */
    @Query("select new com.huotu.agento2o.service.entity.author.Agent(a.id,a.name) from Agent a where a.customer.customerId = ?1 and a.isDeleted = false")
    List<Agent> findByCustomer_CustomerIdAndIsDeletedFalse(Integer customerId);

    Agent findByUserBaseInfo_userIdAndIsDeletedFalse(Integer userId);
}
