package ink.cwblog.rabbitmq_springboot_demo.demo1.producter;

import ink.cwblog.rabbitmq_springboot_demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author  chenw
 * @date  2020/5/12 15:06
 */
@Slf4j
@Component("ProducterSender1")
public class ProducterSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if(ack){
                log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }else{
                //找不到Exchange就会失败
                log.error("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }
        }
    };

    final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            log.error("消息路由失败");
            log.error("message：{}",message);
            log.error("replyCode：{}",replyCode);
            log.error("replyText：{}",replyText);
            log.error("exchange：{}",exchange);
            log.error("routingKey：{}",routingKey);
        }
    };

    /**
     * 方式一
     *
     * @param message
     * @param properties
     * @throws Exception
     */
    public void senderMessage(Object message, Map<String,Object> properties) throws Exception{
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        org.springframework.messaging.Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);
        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.convertAndSend("springboot.demo","springboot.demo.key",msg);
    }

}
