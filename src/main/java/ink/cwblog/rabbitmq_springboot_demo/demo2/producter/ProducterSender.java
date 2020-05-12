package ink.cwblog.rabbitmq_springboot_demo.demo2.producter;

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
@Component(value = "ProducterSender2")
public class ProducterSender {

    @Autowired
    RabbitTemplate rabbitTemplate;
    /**
     * confirm 确认，
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if(ack){
                log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }else{
                //找不到Exchange就会失败
                log.error("消息发送到Exchange失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }
        }
    };
    /**
     * return 回调
     */
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

    public void senderJAVAMessage(User user) throws Exception{
        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //全局唯一ID
        CorrelationData correlationData = new CorrelationData("13213");
        rabbitTemplate.convertAndSend("springboot.demo3","springboot.demo3.key",user,correlationData);
    }

}
