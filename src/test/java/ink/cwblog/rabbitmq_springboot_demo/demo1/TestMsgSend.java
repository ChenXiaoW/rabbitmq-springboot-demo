package ink.cwblog.rabbitmq_springboot_demo.demo1;


import ink.cwblog.rabbitmq_springboot_demo.demo1.producter.ProducterSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMsgSend {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Resource(name = "ProducterSender1")
    ProducterSender producterSender;

	@Test
    public void sendMessage() throws Exception {
	    String msg = "this is from 2020/05/12";
        Map<String,Object> headers = new HashMap<>(2);
        headers.put("user","chenw");
        producterSender.senderMessage(msg,headers);
    }

	@Test
	public void sendMsg(){

		rabbitTemplate.setExchange("springboot.demo");
		rabbitTemplate.setRoutingKey("springboot.demo.key");
		rabbitTemplate.setMandatory(true);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentEncoding("UTF-8");
		messageProperties.setHeader("user","chenw");
		messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		String msg = "This is a springboot mq message";
		Message message = new Message(msg.getBytes(),messageProperties);
		/**
		 * 消息发送后如果routingKey路由不到就会触发Retrun回调
		 */
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			/*
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
				log.error("message：{}",new String(message.getBody()));
				log.error("replyCode：{}",replyCode);
				log.error("replyText：{}",replyText);
				log.error("exchange：{}",exchange);
				log.error("routingKey：{}",routingKey);
			}
		});


		rabbitTemplate.send(message);

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
}
