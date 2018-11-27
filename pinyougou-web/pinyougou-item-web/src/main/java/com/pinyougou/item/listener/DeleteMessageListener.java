package com.pinyougou.item.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * 消息监听器(删除商品的静态页面)
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-11-16<p>
 */
public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    /** 注入静态页面存储路径 */
    @Value("${pageDir}")
    private String pageDir;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        System.out.println("========DeleteMessageListener=======");
        // 1. 获取消息的内容
        Long[] goodsIds = (Long[])objectMessage.getObject();

        // 2. 删除商品的静态页面
        for (Long goodsId : goodsIds){
            // 创建一个文件
            File file = new File(pageDir + goodsId + ".html");
            // 判断文件是否存在
            if (file.exists()){
                // 删除文件
                file.delete();
            }
        }
    }
}
