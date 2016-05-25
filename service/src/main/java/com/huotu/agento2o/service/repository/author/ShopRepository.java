package com.huotu.agento2o.service.repository.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Repository(value = "shopRepository")
public interface ShopRepository extends JpaRepository<Shop, Integer>, JpaSpecificationExecutor {

    Shop findByUsername(String userName);

    @Query("update Shop shop set shop.status=?1 where shop.id=?2")
    @Modifying
    void updateStatus(AgentStatusEnum Status, int id);

    @Query("update Shop shop set shop.isDeleted = true where shop.id=?1")
    @Modifying
    void updateIsDeleted(int id);

    @Query("update Shop shop set shop.isDisabled= ?1 where shop.id=?2")
    @Modifying
    void updateIsDisabled(boolean isDisabled, int id);

    @Query("update Shop shop set shop.status=?1, shop.auditComment=?2 where shop.id=?3")
    @Modifying
    void updateStatusAndComment(AgentStatusEnum Status, String comment, int id);

    @Query("update Shop shop set shop.password=?1 where shop.id=?2")
    @Modifying
    void updatePassword(String password, int id);

    Shop findByUserBaseInfo_userId(Integer userId);
}
