package com.huotu.agento2o.service.repository.purchase;

import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
@Repository
public interface AgentReturnOrderRepository extends JpaRepository<AgentReturnedOrder,Integer> {
}
