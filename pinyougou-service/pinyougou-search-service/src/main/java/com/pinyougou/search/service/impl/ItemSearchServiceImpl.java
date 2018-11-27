package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品搜索服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-12<p>
 */
@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /** 数据同步到索引库 */
    public void saveOrUpdate(List<SolrItem> solrItems){
        try{
            UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
            if (updateResponse.getStatus() == 0){
                solrTemplate.commit();
            }else{
                solrTemplate.rollback();
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 删除商品的索引 */
    public void delete(Long[] goodsIds){
        try{
            // 创建查询对象
            SimpleQuery query = new SimpleQuery();
            // 创建条件对象
            Criteria criteria = new Criteria("goodsId").in(Arrays.asList(goodsIds));
            // 添加条件
            query.addCriteria(criteria);
            // 删除
            UpdateResponse updateResponse = solrTemplate.delete(query);
            if (updateResponse.getStatus() == 0){
                solrTemplate.commit();
            }else{
                solrTemplate.rollback();
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 商品搜索方法 */
    public Map<String,Object> search(Map<String, Object> params){
        try{
            // 定义Map集合封装响应数据
            Map<String,Object> data = new HashMap<>();

            // 获取当前页码
            Integer page = (Integer) params.get("page");
            // 获取页大小
            Integer rows = (Integer) params.get("rows");
            // 设置默认的页码
            if (page == null || page < 1) {
                page = 1;
            }
            // 设置默认的页大小
            if (rows == null || rows < 1) {
                rows = 20;
            }

            // 获取关键字
            String keywords = (String)params.get("keywords");
            // 判断关键字
            if (!StringUtils.isEmpty(keywords)){ // 高亮查询

                // 创建高亮查询对象
                HighlightQuery query = new SimpleHighlightQuery();
                // 创建条件对象
                Criteria criteria = new Criteria("keywords").is(keywords);
                // 添加条件对象
                query.addCriteria(criteria);
                // 创建高亮选项对象(封装高亮参数)
                HighlightOptions highlightOptions = new HighlightOptions();
                // 设置高亮格式器前缀
                highlightOptions.setSimplePrefix("<font color='red'>");
                // 设置高亮的Field
                highlightOptions.addField("title");
                // 设置高亮格式器后缀
                highlightOptions.setSimplePostfix("</font>");

                // 设置高亮选项对象
                query.setHighlightOptions(highlightOptions);


                // {keywords: "", category: "手机", brand: "三星",
                // spec: {网络: "移动3G", 机身内存: "32G"}, price: "3000-*"}

                // 按商品分类过滤
                if (!"".equals(params.get("category"))){
                    // 创建条件对象
                    Criteria criteria1 = new Criteria("category").is(params.get("category"));
                    // 添加过滤查询
                    query.addFilterQuery(new SimpleFilterQuery(criteria1));
                }

                // 按品牌过滤
                if (!"".equals(params.get("brand"))){
                    // 创建条件对象
                    Criteria criteria1 = new Criteria("brand").is(params.get("brand"));
                    // 添加过滤查询
                    query.addFilterQuery(new SimpleFilterQuery(criteria1));
                }

                // 按规格选项过滤
                // "spec_网络": "联通4G",
                // "spec_机身内存": "64G",
                Map<String,String> specMap = (Map<String,String>)params.get("spec");
                //  {网络: "移动3G", 机身内存: "32G"}
                if (specMap != null && specMap.size() > 0){
                    // 迭代key
                    for (String key : specMap.keySet()){
                        // 创建条件对象
                        Criteria criteria1 = new Criteria("spec_" + key).is(specMap.get(key));
                        // 添加过滤查询
                        query.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                }
                // 按价格区间过滤
                if (!"".equals(params.get("price"))){
                    // 0-500, 1500-2000, 3000-*
                    String[] price = params.get("price").toString().split("-");
                    // 价格区间数组第一个元素不是零
                    if (!"0".equals(price[0])){
                        // 创建条件对象  price >= ?
                        Criteria criteria1 = new Criteria("price").greaterThanEqual(price[0]);
                        // 添加过滤查询
                        query.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                    // 价格区间数组第二个元素不是星号
                    if (!"*".equals(price[1])){
                        // 创建条件对象 price <= ?
                        Criteria criteria1 = new Criteria("price").lessThanEqual(price[1]);
                        // 添加过滤查询
                        query.addFilterQuery(new SimpleFilterQuery(criteria1));
                    }
                }
                // 添加排序(价格、新品)
                String sortField = (String)params.get("sortField");
                String sortValue = (String)params.get("sort");
                if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortValue)){
                    // 为高亮查询对象添加排序
                    // 第一个参数：升序｜降序
                    query.addSort(new Sort("ASC".equals(sortValue)
                            ? Sort.Direction.ASC : Sort.Direction.DESC, sortField));
                }


                // 设置分页起始记录数
                query.setOffset((page - 1) * rows);
                // 设置页大小
                query.setRows(rows);

                // 高亮分页查询
                HighlightPage<SolrItem> highlightPage = solrTemplate
                            .queryForHighlightPage(query, SolrItem.class);

                System.out.println("命中的记录数：" + highlightPage.getTotalElements());
                // 获取高亮项的集合
                List<HighlightEntry<SolrItem>> highlighted = highlightPage.getHighlighted();
                // 迭代高亮项的集合
                for (HighlightEntry<SolrItem> highlightEntry : highlighted){
                    // 获取原文档封装实体对象
                    SolrItem solrItem = highlightEntry.getEntity();
                    // 获取高亮内容集合
                    List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
                    // 判断高亮内容集合的大小
                    if (highlights != null && highlights.size() > 0){
                        // 获取第一个Field的高亮内容(标题)
                        String title = highlights.get(0).getSnipplets().get(0);
                        System.out.println("title高亮内容：" + title);
                        // 设置标题高亮内容到实体中
                        solrItem.setTitle(title);
                    }
                }
                // 获取搜索到得文档集合
                data.put("rows", highlightPage.getContent());
                // 总记录数
                data.put("total", highlightPage.getTotalElements());
                // 总记页码
                data.put("totalPages", highlightPage.getTotalPages());

            }else{ // 简单查询

                // 创建简单查询对象
                Query query = new SimpleQuery("*:*");

                // 设置分页起始记录数
                query.setOffset((page - 1) * rows);
                // 设置页大小
                query.setRows(rows);

                // 分页查询
                ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
                System.out.println("命中的记录数：" + scoredPage.getTotalElements());
                // 获取搜索到得文档集合
                data.put("rows", scoredPage.getContent());
                // 总记录数
                data.put("total", scoredPage.getTotalElements());
                // 总记页码
                data.put("totalPages", scoredPage.getTotalPages());

            }
            return data;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}