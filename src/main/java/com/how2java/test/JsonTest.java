package com.how2java.test;

import com.alibaba.fastjson.JSON;
import com.how2java.pojo.MessageVo;

public class JsonTest {
    public static void main(String[] args) {
        MessageVo vo = new MessageVo();
        vo.setRobotCode("ss");
        vo.setMessageType(1);
        vo.setMessage("hh");
        String jsonStr = JSON.toJSONString(vo);

        MessageVo messageVo1 =  JSON.parseObject(jsonStr,MessageVo.class);
        MessageVo messageVo =  JSON.parseObject("{\"message\":\"heart\",\"messageType\":0,\"robotCode\":\"test\"}",MessageVo.class);
        System.out.println("ss");
    }
}
