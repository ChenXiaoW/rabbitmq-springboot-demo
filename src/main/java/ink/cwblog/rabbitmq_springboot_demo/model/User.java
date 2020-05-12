package ink.cwblog.rabbitmq_springboot_demo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author  chenw
 * @date  2020/5/12 11:08
 * 用户对象
 */
@Data
public class User implements Serializable {

    private String username;

    private String userId;
}
