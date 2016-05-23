package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
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

    Agent findByUsernameAndStatus(String userName, AgentStatusEnum status);

    List<Agent> findByAgentLevel_levelIdAndIsDeletedFalse(Integer levelId);

    Agent findByUsernameAndIsDeletedFalse(String userName);

    @Modifying
    @Query("update Agent a set a.isDeleted = true where a.id = ?1")
    void deleteAgent(Integer id);

    @Modifying
    @Query("update Agent a set a.isDisabled = ?2 where a.id = ?1")
    void updateDisabledStatus(Integer id,boolean status);

    List<Agent> findByParentAuthor_id(Integer id);

    Agent findByUserBaseInfo_userId(Integer userId);

    @Modifying
    @Query("update Agent a set a.password = ?2 where a.id = ?1")
    void resetPassword(Integer id,String password);

}
