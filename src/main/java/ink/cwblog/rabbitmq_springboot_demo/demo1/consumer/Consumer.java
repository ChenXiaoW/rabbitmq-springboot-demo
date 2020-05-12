package ink.cwblog.rabbitmq_springboot_demo.demo1.consumer;

import com.rabbitmq.client.Channel;
import ink.cwblog.rabbitmq_springboot_demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 *
 *
 * @description: 普通消息消费者 - 默认消费字符串
 * @author: ChenWei
 * @create: 2020/5/11 - 23:22
 **/
@Slf4j
@Component("Consumer1")
public class Consumer {
	@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "springboot.demo.queue",durable = "true",autoDelete="false"),
			exchange = @Exchange(value = "springboot.demo",durable = "true",type = "direct",ignoreDeclarationExceptions = "false"),key = "springboot.demo.key"))
	@RabbitHandler
	public void onMessage(Message message, Channel channel,String msg)throws Exception{
		log.info("Consumer1消费端：");
		Object delivery = message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
		Object user = message.getHeaders().get("user");
		log.info("delivery：{}",(long)delivery);
		log.info("消费消息：{}",msg);
		log.info("user：{}",user);
		//手工ack
		channel.basicAck((long)delivery,false);
	}

}
