package com.jnxaread.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author 未央
 * @create 2020-05-02 16:27
 */
@SpringBootTest
public class EmailTest {

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    @Test
    public void testSend() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("inhive@sina.com");
        message.setTo("546993317@qq.com");
        message.setSubject("这是标题");
        message.setText("这是内容");
        javaMailSender.send(message);
    }

}
