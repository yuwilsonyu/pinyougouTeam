package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.*;

/**
 * 商品服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-06<p>
 */
@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SellerMapper sellerMapper;


    /** 添加商品(tb_goods、tb_goods_desc、tb_item) */
    @Override
    public void save(Goods goods) {
        try{
            /** 往tb_goods SPU标准商品表插入数据 */
            // 设置商品的审核状态（未审核）
            goods.setAuditStatus("0");
            goodsMapper.insertSelective(goods);

            /** 往tb_goods_desc 商品描述表插入数据 */
            // 设置主键id
            goods.getGoodsDesc().setGoodsId(goods.getId());
            goodsDescMapper.insertSelective(goods.getGoodsDesc());

            // 判断是否启用规格
            if ("1".equals(goods.getIsEnableSpec())) {
                /** 往tb_item SKU库存量单位表插入数据 */
                for (Item item : goods.getItems()) {
                    // {spec : {}, isDefault : '0', status : '0', price : 0, num : 9999}
                    // SKU商品的标题 = SPU标题 + 规格选项
                    StringBuilder title = new StringBuilder(goods.getGoodsName());
                    // 获取规格选项({"网络":"电信4G","机身内存":"64G"})
                    Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                    for (String optionName : specMap.values()) {
                        title.append(" " + optionName);
                    }
                    item.setTitle(title.toString());
                    setItemInfo(item, goods);
                    // 插入数据
                    itemMapper.insertSelective(item);
                }
            }else{
                // SPU就是SKU(往tb_item插入一条数据)
                // {spec : {}, isDefault : '0', status : '0', price : 0, num : 9999}
                /** 创建SKU具体商品对象 */
                Item item = new Item();
                /** 设置SKU商品的标题 */
                item.setTitle(goods.getGoodsName());
                /** 设置SKU商品的价格 */
                item.setPrice(goods.getPrice());
                /** 设置SKU商品库存数据 */
                item.setNum(9999);
                /** 设置SKU商品启用状态 */
                item.setStatus("1");
                /** 设置是否默认*/
                item.setIsDefault("1");
                /** 设置规格选项 */
                item.setSpec("{}");

                setItemInfo(item, goods);
                // 插入数据
                itemMapper.insertSelective(item);
            }

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 设置SKU其它信息 */
    private void setItemInfo(Item item, Goods goods) {
        // 商品图片[{"color":"金色","url":"http://image.pinyougou.com/jd/wKgMg1qtNj-Aaad6AAHgnq0MSN8172.jpg"}]
        List<Map> itemImages = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (itemImages != null && itemImages.size() > 0) {
            // SKU商品的图片
            item.setImage(itemImages.get(0).get("url").toString());
        }
        // SKU商品的三级分类id
        item.setCategoryid(goods.getCategory3Id());
        // SKU商品的创建时间
        item.setCreateTime(new Date());
        // SKU商品的修改时间
        item.setUpdateTime(item.getCreateTime());
        // SKU商品的关联的SPU的id
        item.setGoodsId(goods.getId());
        // SKU商品的商家id
        item.setSellerId(goods.getSellerId());
        // SKU商品的三级分类名称
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        item.setCategory(itemCat != null ? itemCat.getName() : "");
        // SKU商品的品牌名称
        Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(brand != null ? brand.getName() : "");
        // SKU商品的店铺名称
        Seller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(seller != null ? seller.getNickName() : "");
    }

    @Override
    public void update(Goods goods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    /** 批量删除(修改删除状态) */
    @Override
    public void deleteAll(Long[] ids) {
        try{
            // UPDATE tb_goods SET is_delete = 1 WHERE id IN (?,?,?)
            goodsMapper.updateStatus("is_delete", ids, "1");

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Goods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Goods> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Goods goods, int page, int rows) {
        try{
            // 开启分页
            PageInfo<Map<String,Object>> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    goodsMapper.findAll(goods);
                }
            });
            for (Map<String,Object> map : pageInfo.getList()){

                // 获取一级分类名称
                Long category1Id = (Long)map.get("category1Id");
                ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(category1Id);
                map.put("category1Name", itemCat1 != null ? itemCat1.getName() : "");

                // 获取二级分类名称
                Long category2Id = (Long)map.get("category2Id");
                ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(category2Id);
                map.put("category2Name", itemCat2 != null ? itemCat2.getName() : "");

                // 获取三级分类名称
                Long category3Id = (Long)map.get("category3Id");
                ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(category3Id);
                map.put("category3Name", itemCat3 != null ? itemCat3.getName() : "");
            }
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 商品审核(修改商品状态) */
    public void updateStatus(Long[] ids, String status){
        try{
            // UPDATE tb_goods SET audit_status = 1 WHERE id IN (?,?,?)
            goodsMapper.updateStatus("audit_status", ids, status);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**  修改商品上下架状态 */
    public void updateMarketable(Long[] ids, String status){
        try{
            // UPDATE tb_goods SET is_marketable = 1 WHERE id IN (?,?,?)
            goodsMapper.updateStatus("is_marketable", ids, status);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据SPU的id查询商品信息 */
    public Map<String,Object> getGoods(Long goodsId){
        try{

            Map<String,Object> dataModel = new HashMap<>();
            // 查询SPU表
            Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
            // 查询商品描述表
            GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);


            dataModel.put("goods",goods);
            dataModel.put("goodsDesc", goodsDesc);

            // 查询商品的三级分类名称
            if (goods.getCategory3Id() != null && goods.getCategory3Id() > 0){
                // 查询商品的一级分类名称
                ItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
                dataModel.put("itemCat1", itemCat1 != null ? itemCat1.getName() : "");

                // 查询商品的二级分类名称
                ItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
                dataModel.put("itemCat2", itemCat2 != null ? itemCat2.getName() : "");

                // 查询商品的三级分类名称
                ItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
                dataModel.put("itemCat3", itemCat3 != null ? itemCat3.getName() : "");
            }

            // 创建示范对象
            Example example = new Example(Item.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // goods_id = ?
            criteria.andEqualTo("goodsId", goodsId);
            // 状态码 1
            criteria.andEqualTo("status", 1);
            // 排序(把默认的SKU排在最前面)
            example.orderBy("isDefault").desc();
            // 查询SKU表
            List<Item> itemList = itemMapper.selectByExample(example);

            // ${itemList} 把集合转化成json字符串
            dataModel.put("itemList", JSON.toJSONString(itemList));
            return dataModel;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据goodsIds查询上架通过的SKU商品*/
    public List<Item> findItemByGoodsId(Long[] goodsIds){
        // SELECT * FROM `tb_item` WHERE goods_id IN(?,?,?)
        try{
            // 创建示范对象
            Example example = new Example(Item.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 状态码 : 1
            criteria.andEqualTo("status", 1);
            // 添加in条件
            criteria.andIn("goodsId", Arrays.asList(goodsIds));
            // 条件查询
            return itemMapper.selectByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}