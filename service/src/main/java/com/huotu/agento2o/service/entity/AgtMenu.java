/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/10.
 */
@Entity
@Table(name = "Agt_Menu")
@Getter
@Setter
public class AgtMenu {
    @Id
    private String menuId;
    @ManyToOne
    @JoinColumn(referencedColumnName = "MENUID")
    private AgtMenu parent;
    private String menuName;
    private String linkUrl;
    private int sortNum;
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("sortNum asc ")
    private List<AgtMenu> children;
    private int length;
    @Column(length = 2000)
    private String author;
    /**
     * 是否启用,0表示启用,1表示不启用
     */
    private int isDisabled;

    public AgtMenu() {
    }

    public AgtMenu(int length, String linkUrl, String menuName, int sortNum, AgtMenu parent, String author, int isDisabled, String menuId) {
        this.menuId = menuId;
        this.parent = parent;
        this.menuName = menuName;
        this.linkUrl = linkUrl;
        this.sortNum = sortNum;
        this.length = length;
        this.author = author;
        this.isDisabled = isDisabled;
    }

    public String getSpace() {
        String space = "";
        for (int i = 0; i < length; i++) {
            space += "--";
        }
        return space;
    }

    public boolean isAuthor() {
        List<String> realAuthor = new ArrayList<>();
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(author -> {
            if (!realAuthor.contains(author.getAuthority().substring("ROLE_".length()))) {
                realAuthor.add(author.getAuthority().substring("ROLE_".length()));
            }
        });
        String[] authorArray = this.author.split(",");
        for (String author : authorArray) {
            if (realAuthor.contains(author)) {
                return true;
            }
        }
        return false;
    }
}
