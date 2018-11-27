package com.pinyougou.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 消息监听器类(同步索引)
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-16<p>
 */
public class ItemMessageListener implements SessionAwareMessageListener<ObjectMessage>{

    @Reference(timeout = 10000)
    private GoodsService goodsService;
    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        System.out.println("=======ItemMessageListener========");
        // 1. 获取消息的内容
        Long[] goodsIds = (Long[])objectMessage.getObject();
        System.out.println("goodsIds: " + Arrays.toString(goodsIds));

        // 2. 根据goodsIds查询上架通过的SKU商品
        List<Item> items = goodsService.findItemByGoodsId(goodsIds);

        // 3. 把List<Item>转化成List<SolrItem>
        List<SolrItem> solrItems = new ArrayList<>();
        // 循环把List<Item> 转化成 List<SolrItem>
        for (Item item1 : items){
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
            // 添加集合
            solrItems.add(solrItem);
        }

        // 把商品数据同步到索引库
        itemSearchService.saveOrUpdate(solrItems);
    }
}
