package com.tensquare.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.tensquare.sms.utils.SmsUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "queue-sms")
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;
    @Value("${aliyun.sms.template_code}")
    private String templateCode;
    @Value("${aliyun.sms.sign_name}")
    private String signName;


    @RabbitHandler
    public void sendSms(Map<String,String> message) {
        //接收到mq发送的消息
        System.out.println(message);
        //发送短信
        //参数1：mobile，手机号
        String mobile = message.get("mobile");
        String code = message.get("code");
        //参数2：模板编号
        //参数3：签名
        //参数4：阿里云通信要求的参数信息
        String param = "{\"code\":\"" + code + "\"}";
        try {
            smsUtil.sendSms(mobile, templateCode, signName, param);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
