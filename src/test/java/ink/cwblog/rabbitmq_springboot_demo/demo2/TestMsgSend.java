package ink.cwblog.rabbitmq_springboot_demo.demo2;

import ink.cwblog.rabbitmq_springboot_demo.demo2.producter.ProducterSender;
import ink.cwblog.rabbitmq_springboot_demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author chenw
 * @title: TestMsgSend
 * @description: TODO
 * @date 2020/5/12 15:40
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMsgSend {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void sendJavaMsg(){
        rabbitTemplate.setMandatory(true);
        //return 回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {


            /**
             * Returned message callback.
             *
             * @param message    the returned message.
             * @param replyCode  the reply code.
             * @param replyText  the reply text.
             * @param exchange   the exchange.
             * @param routingKey the routing key.
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.error("消息路由失败");
                log.error("message：{}",message);
                log.error("replyCode：{}",replyCode);
                log.error("replyText：{}",replyText);
                log.error("exchange：{}",exchange);
                log.error("routingKey：{}",routingKey);
            }
        });
        User user = new User();
        user.setUsername("chenw");
        user.setUserId("159");
        //convertAndSend ：将java对象转换成mq消息并发送到交换机
        String exchange = "springboot.demo3";
        String routingKey = "springboot.demo3.key";
        //全局唯一ID
        CorrelationData correlationData = new CorrelationData("12223");
        rabbitTemplate.convertAndSend(exchange,routingKey,user,correlationData);

        /**
         * 回调确认
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(ack){
                    log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
                }else{
                    //找不到Exchange就会失败
                    log.error("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
                }
            }
        });
    }

    @Resource(name = "ProducterSender2")
    ProducterSender producterSender;

    @Test
    public void sendMessageFromJAVA() throws Exception {
        User user = new User();
        user.setUsername("chenw");
        user.setUserId("243");
        producterSender.senderJAVAMessage(user);
    }
}
