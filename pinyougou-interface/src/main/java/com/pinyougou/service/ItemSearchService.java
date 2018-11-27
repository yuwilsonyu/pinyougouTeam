package com.pinyougou.service;

import com.pinyougou.solr.SolrItem;

import java.util.List;
import java.util.Map; /**
 * 商品搜索服务接口
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-12<p>
 */
public interface ItemSearchService {
    /** 商品搜索方法 */
    Map<String,Object> search(Map<String, Object> params);

    /** 数据同步到索引库 */
    void saveOrUpdate(List<SolrItem> solrItems);

    /** 删除商品的索引 */
    void delete(Long[] goodsIds);
}
