package com.huotu.agento2o.service.repository.purchase;

import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
@Repository
public interface AgentReturnOrderRepository extends JpaRepository<AgentReturnedOrder,String>, JpaSpecificationExecutor<AgentReturnedOrder> {

    List<AgentReturnedOrder> findByAuthor_IdAndDisabledFalse(Integer agentId);

    AgentReturnedOrder findByAuthorAndROrderIdAndDisabledFalse(Author author,String rOrderId);
}
