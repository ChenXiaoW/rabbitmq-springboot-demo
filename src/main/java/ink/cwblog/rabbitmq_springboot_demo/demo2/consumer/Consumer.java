package ink.cwblog.rabbitmq_springboot_demo.demo2.consumer;

import com.rabbitmq.client.Channel;
import ink.cwblog.rabbitmq_springboot_demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author chenw
 * @title: Consumer
 * @description: JAVA对象消息消费者 - 使用java对象消费
 * @date 2020/5/12 15:36
 */
@Slf4j
@Component("Consumer2")
public class Consumer {

    /**
     * 监听队列
     *
     * @param user 用户对象
     * @param headers 消息头
     * @param channel
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "springboot.demo3.queue",durable = "true",autoDelete="false"),
            exchange = @Exchange(value = "springboot.demo3",durable = "true",type = "direct",ignoreDeclarationExceptions = "false"),key = "springboot.demo3.key"))
    @RabbitHandler
    public void onJavaMessage(@Payload User user, @Headers Map<String,Object> headers, Channel channel)throws Exception{
        log.info("Consumer2消费端：");
        Object delivery = headers.get(AmqpHeaders.DELIVERY_TAG);
        log.info("delivery：{}",(long)delivery);
        log.info("消费消息：{}",user);
        log.info("user：{}",user);
        //手工ack
        channel.basicAck((long)delivery,false);
    }
}
