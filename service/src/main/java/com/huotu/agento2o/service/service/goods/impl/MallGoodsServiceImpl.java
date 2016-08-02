/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.goods.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallGoodsType;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentGoods;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.FreightTemplate;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.goods.MallGoodsTypeRepository;
import com.huotu.agento2o.service.repository.purchase.AgentGoodsRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.ShoppingCartRepository;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.goods.MallProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by helloztt on 2016/5/14.
 */
@Service
public class MallGoodsServiceImpl implements MallGoodsService {
    @Autowired
    private MallGoodsRepository goodsRepository;
    @Autowired
    private AgentProductRepository agentProductRepository;
    @Autowired
    private MallGoodsTypeRepository goodsTypeRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private MallProductService productService;
    @Autowired
    private AgentGoodsRepository agentGoodsRepository;


    /**
     * 根据 CustomerId 和 AgentId 查找指定门店商品，AgentId=0 表示平台方商品
     *
     * @param customerId 平台方ID
     * @param author     门店ID
     * @return
     */
    @Override
    public Page<MallGoods> findByCustomerIdAndAgentId(Integer customerId, Author author, GoodsSearcher goodsSearcher) {
        Specification<MallGoods> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("customerId").as(Integer.class), customerId));
            predicates.add(cb.equal(root.get("isAgent").as(Boolean.class), true));
            if (!StringUtil.isEmptyStr(goodsSearcher.getGoodsName())) {
                predicates.add(cb.like(root.get("name").as(String.class), "%" + goodsSearcher.getGoodsName() + "%"));
            }
            if (!StringUtil.isEmptyStr(goodsSearcher.getStandardTypeId())) {
                MallGoodsType type = goodsTypeRepository.findByStandardTypeIdAndDisabledFalseAndCustomerId(goodsSearcher.getStandardTypeId(), -1);
                predicates.add(cb.equal(root.get("typeId").as(Integer.class), type.getTypeId()));
            }
            if (goodsSearcher.getCustomerTypeId() != -1) {
                predicates.add(cb.equal(root.get("typeId").as(Integer.class), goodsSearcher.getCustomerTypeId()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<MallGoods> goodsPage = goodsRepository.findAll(specification, new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE, new Sort(Sort.Direction.DESC, "salesCount")));
        if (goodsPage.getContent() != null && goodsPage.getContent().size() > 0) {
            goodsPage.getContent().forEach(goods -> {
                goods.getProducts().forEach(product -> {
                    productService.setProductPrice(product, author);
                    AgentProduct agentProduct = getAgentProduct(author, product);
                    ShoppingCart shoppingCart = getShoppingCart(author, product);
                    if (agentProduct != null) {
                        product.setAuthorStore(Math.max(0, agentProduct.getStore() - agentProduct.getFreez()));
                    }
                    product.setUsableStore(Math.max(0, product.getStore() - product.getFreez()));
                    if (shoppingCart != null) {
                        product.setShoppingStore(Math.max(0, shoppingCart.getNum()));
                    }
                });
                setGoodsPurchasePrice(goods);
            });
        }
        return goodsPage;
    }

    public Map<Integer, Double> getLevelProductPrice(String priceInfo) {
        Map<Integer, Double> productPriceMap = new HashMap<>();
        String[] priceInfoList = priceInfo.split("\\|");
        for (int i = 0; i < priceInfoList.length; i++) {
            if (priceInfoList[i].indexOf(":") > -1) {
                productPriceMap.put(Integer.parseInt(priceInfoList[i].split(":")[0]), Double.parseDouble(priceInfoList[i].split(":")[1]));
            }
        }
        return productPriceMap;
    }

    public AgentProduct getAgentProduct(Author author, MallProduct product) {
        AgentProduct agentProduct = null;
        if (author != null && author.getType() == Agent.class) {
            agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getAuthorAgent(), product);
        } else if (author != null && author.getType() == Shop.class) {
            agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(author.getAuthorShop(), product);
        }
        return agentProduct;
    }

    public ShoppingCart getShoppingCart(Author author, MallProduct product) {
        ShoppingCart shoppingCart = null;
        if (author != null && author.getType() == Agent.class) {
            shoppingCart = shoppingCartRepository.findByAgentAndProduct(author.getAuthorAgent(), product);
        } else if (author != null && author.getType() == Shop.class) {
            shoppingCart = shoppingCartRepository.findByShopAndProduct(author.getAuthorShop(), product);
        }
        return shoppingCart;
    }

    /**
     * 根据 AgentId 查找上级代理商商品
     *
     * @param author 代理商/门店
     * @return
     */
    @Override
    public Page<MallGoods> findByAgentId(Author author, GoodsSearcher goodsSearcher) {
        Specification<MallGoods> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtil.isEmptyStr(goodsSearcher.getGoodsName())) {
                predicates.add(cb.like(root.get("name").as(String.class), "%" + goodsSearcher.getGoodsName() + "%"));
            }
            if (!StringUtil.isEmptyStr(goodsSearcher.getStandardTypeId())) {
                MallGoodsType type = goodsTypeRepository.findByStandardTypeIdAndDisabledFalseAndCustomerId(goodsSearcher.getStandardTypeId(), -1);
                predicates.add(cb.equal(root.get("typeId").as(Integer.class), type.getTypeId()));
            }
            if (goodsSearcher.getCustomerTypeId() != -1) {
                predicates.add(cb.equal(root.get("typeId").as(Integer.class), goodsSearcher.getCustomerTypeId()));
            }
            //子查询 goodsId in (select distinct goodsId from AgentProduct where author.id= ?1)
            Subquery subQuery = query.subquery(AgentProduct.class).distinct(true);
            Root agentProductRoot = subQuery.from(AgentProduct.class);
            subQuery.where(cb.equal(agentProductRoot.get("agent").get("id").as(Integer.class), author.getParentAgent().getId()));
            subQuery.select(agentProductRoot.get("goodsId"));
            predicates.add(root.get("goodsId").in(cb.any(subQuery)));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //设置可用库存和当前库存
        Page<MallGoods> goodsPage = goodsRepository.findAll(specification, new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE));
        if (goodsPage.getContent() != null && goodsPage.getContent().size() > 0) {
            goodsPage.getContent().forEach(goods -> {
                goods.getProducts().forEach(product -> {
                    productService.setProductPrice(product, author);
                    AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getParentAgent(), product);
                    AgentProduct agentProduct = getAgentProduct(author, product);
                    ShoppingCart shoppingCart = getShoppingCart(author, product);
                    if (agentProduct != null) {
                        product.setAuthorStore(Math.max(0, agentProduct.getStore() - agentProduct.getFreez()));
                    }
                    if (parentAgentProduct != null) {
                        product.setUsableStore(Math.max(0, parentAgentProduct.getStore() - parentAgentProduct.getFreez()));
                    }
                    if (shoppingCart != null) {
                        product.setShoppingStore(Math.max(0, shoppingCart.getNum()));
                    }
                });
                setGoodsPurchasePrice(goods);
            });
        }
        return goodsPage;
    }

    private void setGoodsPurchasePrice(MallGoods goods){
        double minPurchasePrice = goods.getProducts().stream().mapToDouble(p->p.getPurchasePrice()).min().getAsDouble();
        double maxPurchasePrice = goods.getProducts().stream().mapToDouble(p->p.getPurchasePrice()).max().getAsDouble();
        if (minPurchasePrice == maxPurchasePrice) {
            goods.setPurchasePrice(String.valueOf(minPurchasePrice));
        } else {
            goods.setPurchasePrice(minPurchasePrice + "-" + maxPurchasePrice);
        }
    }


    @Override
    public Page<MallGoods> findByAuthorId(Author author, GoodsSearcher goodsSearcher) {
        Specification<MallGoods> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtil.isEmptyStr(goodsSearcher.getGoodsName())) {
                predicates.add(cb.like(root.get("name").as(String.class), "%" + goodsSearcher.getGoodsName() + "%"));
            }
            if (!StringUtil.isEmptyStr(goodsSearcher.getStandardTypeId())) {
                MallGoodsType type = goodsTypeRepository.findByStandardTypeIdAndDisabledFalseAndCustomerId(goodsSearcher.getStandardTypeId(), -1);
                predicates.add(cb.equal(root.get("typeId").as(Integer.class), type.getTypeId()));
            }
            if (goodsSearcher.getCustomerTypeId() != -1) {
                predicates.add(cb.equal(root.get("typeId").as(Integer.class), goodsSearcher.getCustomerTypeId()));
            }
            //子查询 goodsId in (select distinct goodsId from AgentProduct where author.id= ?1)
            Subquery subQuery = query.subquery(AgentProduct.class).distinct(true);
            Root agentProductRoot = subQuery.from(AgentProduct.class);
            if (author != null && author.getType() == Agent.class) {
                subQuery.where(cb.equal(agentProductRoot.get("agent").get("id").as(Integer.class), author.getId()));
            } else if (author != null && author.getType() == Shop.class) {
                subQuery.where(cb.equal(agentProductRoot.get("shop").get("id").as(Integer.class), author.getId()));
            }
            subQuery.select(agentProductRoot.get("goodsId"));
            predicates.add(root.get("goodsId").in(cb.any(subQuery)));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //设置可用库存和预占库存
        Page<MallGoods> goodsPage = goodsRepository.findAll(specification, new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE));
        if (goodsPage.getContent() != null && goodsPage.getContent().size() > 0) {
            goodsPage.getContent().forEach(goods -> {
                goods.getProducts().forEach(product -> {
                    setStoreAndFreeze(product, author, null);
                });
            });
        }
        return goodsPage;
    }

    /**
     * 门店/代理商 获取商品时
     * 设置可用库存和预占库存总库存
     *
     * @param product
     * @param author
     */
    private void setStoreAndFreeze(MallProduct product, Author author, List<MallProduct> products) {
        AgentProduct agentProduct = null;
        if (author != null && author.getType() == Agent.class) {
            agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getAuthorAgent(), product);
        } else if (author != null && author.getType() == Shop.class) {
            agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(author.getAuthorShop(), product);
        }
        if (agentProduct != null) {
            product.setStore(agentProduct.getStore());
            product.setFreez(agentProduct.getFreez());
            product.setUsableStore(Math.max(0, agentProduct.getStore() - agentProduct.getFreez()));
            if (products != null) {
                products.add(product);
            }
        } else {
            product.setStore(0);
            product.setFreez(0);
            product.setUsableStore(0);
        }
    }

    @Override
    public MallGoods findByGoodsIdAndAuthor(Integer goodsId, Author author) {
        MallGoods mallGoods = findByGoodsId(goodsId);
        if (mallGoods == null) {
            return null;
        }
        //如果是门店需要获取商品的运费模板
        if (author != null && author.getType() == Shop.class) {
            AgentGoods agentGoods = agentGoodsRepository.findByShopAndGoodsIdAndDisabledFalse(author.getAuthorShop(), goodsId);
            if (agentGoods != null) {
                mallGoods.setFreightTemplate(agentGoods.getFreightTemplate());
            } else {
                mallGoods.setFreightTemplate(null);
            }
        }
        //设置可用库存和预占库存总库存和价格
        List<MallProduct> products = new ArrayList<>();
        mallGoods.getProducts().forEach(mallProduct -> {
            productService.setProductPrice(mallProduct, author);
            setStoreAndFreeze(mallProduct, author, products);
        });
        mallGoods.setProducts(products);
        return mallGoods;
    }

    @Override
    public ApiResult updateAgentGoods(AgentGoods requestAgentGoods, Author author, FreightTemplate freightTemplate) {
        if (requestAgentGoods == null || requestAgentGoods.getGoodsId() == null || author == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //只有门店可以修改商品信息
        if (author.getType() != Shop.class) {
            return new ApiResult("没有权限");
        }
        AgentGoods agentGoods = agentGoodsRepository.findByShopAndGoodsIdAndDisabledFalse(author.getAuthorShop(), requestAgentGoods.getGoodsId());
        if (agentGoods == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        agentGoods.setFreightTemplate(freightTemplate);
        //后续可以增加修改内容
        agentGoodsRepository.save(agentGoods);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public MallGoods findByGoodsId(Integer goodsId) {
        return goodsId == null ? null : goodsRepository.findOne(goodsId);
    }

}
