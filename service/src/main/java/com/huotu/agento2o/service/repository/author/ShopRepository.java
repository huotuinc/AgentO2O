package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "shopRepository")
public interface ShopRepository extends JpaRepository<Shop,Integer>,JpaSpecificationExecutor {
    Shop findByUsernameAndStatus(String userName, AgentStatusEnum status);
}
