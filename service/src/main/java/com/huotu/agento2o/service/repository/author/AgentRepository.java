package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "agentRepository")
public interface AgentRepository extends JpaRepository<Agent, Integer>, JpaSpecificationExecutor {

    Agent findByUsernameAndStatus(String userName, AgentStatusEnum status);

    List<Agent> findByAgentLevel_levelId(Integer levelId);

    Agent findByUsernameAndIsDeletedFalse(String userName);
}
