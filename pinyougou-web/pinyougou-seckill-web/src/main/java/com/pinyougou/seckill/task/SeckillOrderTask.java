package com.pinyougou.seckill.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 秒杀订单任务调度类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-27<p>
 */
@Component
public class SeckillOrderTask {

    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * 定义关闭订单方法
     * 每隔3秒调度该方法
     * cron: 秒 分  小时 日  月  周
     * */
    @Scheduled(cron = "0/3 * * * * ?")
    public void closeOrder(){
        System.out.println("============" + new Date());
        /** 当用户下单后5分钟尚未付款应该释放订单，增加库存。*/
        // 1. 查询所有超时未支付的订单
        List<SeckillOrder> seckillOrderList = seckillOrderService.findOrderByTimeout();

        System.out.println("需要关闭的订单：【" + seckillOrderList.size() + "】个。");

        // 2. 调用微信支付系统"关闭订单" 接口
        for (SeckillOrder seckillOrder : seckillOrderList){
            // 关闭订单
            Map<String,String> data = weixinPayService
                    .closePayTimeout(seckillOrder.getId().toString());

            // 3. 如果关单成功，删除秒杀订单，增加库存
            if ("SUCCESS".equals(data.get("return_code"))){
                System.out.println("===超时，删除订单===");
                // 删除Redis数据中的秒杀订单，增加库存
                seckillOrderService.deleteOrderFromRedis(seckillOrder);
            }
        }
    }
}
