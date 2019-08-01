package com.how2java.pojo;

import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.objects.annotations.Getter;

public class MessageVo {
    Integer messageType;
    String message;
    String robotCode;

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public Integer getMessageType() {
        return messageType;
    }
    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toJson(){
        return JSON.toJSONString(this);
    }
}
