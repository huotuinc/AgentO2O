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

    List<Agent> findByParentAgent_IdAndIsDeletedFalse(Integer agentId);

    Agent findByUserBaseInfo_userIdAndIsDeletedFalse(Integer userId);
}
