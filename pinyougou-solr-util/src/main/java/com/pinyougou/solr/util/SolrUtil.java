package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SolrUtil(把tb_item表中数据导入Solr服务器的索引库中)
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-12<p>
 */
@Component
public class SolrUtil {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /** 导入数据到Solr服务器 */
    public void importDataToSolr(){

        // 创建Item对象封装查询条件
        Item item = new Item();
        // 状态码：1
        item.setStatus("1");

        // 查询SKU商品数据
        List<Item> items = itemMapper.select(item);

        System.out.println("======开始========");
        List<SolrItem> solrItems = new ArrayList<>();

        // 循环把List<Item> 转化成 List<SolrItem>
        for (Item item1 : items){
            System.out.println(item1.getId() + "\t" + item1.getTitle());
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item1.getId());
            solrItem.setTitle(item1.getTitle());
            solrItem.setPrice(item1.getPrice());
            solrItem.setImage(item1.getImage());
            solrItem.setGoodsId(item1.getGoodsId());
            solrItem.setCategory(item1.getCategory());
            solrItem.setBrand(item1.getBrand());
            solrItem.setSeller(item1.getSeller());
            solrItem.setUpdateTime(item1.getUpdateTime());
            // 获取规格选项 {"网络":"联通4G","机身内存":"64G"}
            Map<String,String> specMap = JSON.parseObject(item1.getSpec(), Map.class);
            // 动态域
            solrItem.setSpecMap(specMap);

            solrItems.add(solrItem);
        }

        // 添加或修改Solr服务器中的索引库
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0){
            solrTemplate.commit();;
        }else{
            solrTemplate.rollback();
        }
        System.out.println("======结束========");
    }

    public static void main(String[] args){
        // 创建Spring容器
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        SolrUtil solrUtil = ac.getBean(SolrUtil.class);

        solrUtil.importDataToSolr();
    }
}
