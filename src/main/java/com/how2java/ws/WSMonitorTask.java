package com.how2java.ws;

//import com.eps.domain.robot.RobotStatus;
//import com.eps.robot.utils.SpringTool;
//import com.eps.service.robot.RobotStatusService;
//import org.apache.log4j.Logger;
//import org.apache.log4j.spi.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 监控websocket心跳
 *
 * @author Tan Jingxuan
 * @date 2018/9/10
 */
public class WSMonitorTask implements Delayed {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //public static RobotStatusService robotStatusService = SpringContextHolder.getBean("robotStatusService");
//    public static RobotStatusService robotStatusService = (RobotStatusService) SpringTool.getBean("robotStatusService");

//    private static ApplicationContext applicationContext2;
//    private RobotStatusService robotStatusService2;
//    public static void setApplicationContext(ApplicationContext applicationContext) {
//        WSMonitorTask.applicationContext2 = applicationContext;
//    }


    /**
     * 执行时间
     */
    private long executeTime;

    /**
     * 推送任务信息
     */
    private String robotCode;

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    public String getRobotCode() {
        return robotCode;
    }

    public void setRobotCode(String robotCode) {
        this.robotCode = robotCode;
    }

    public WSMonitorTask(long executeTime, String robotCode) {
        this.executeTime = executeTime;
        this.robotCode = robotCode;
    }

    /**
     * 判断是否到期，到期返回-1
     *
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        WSMonitorTask that = (WSMonitorTask) o;
        if (this.executeTime > that.executeTime) {
            return 1;
        }
        if (this.executeTime < that.executeTime) {
            return -1;
        }
        return 0;
    }

    /**
     * 任务到了计划执行时间时执行
     */
    public void inform() {
        //更新连接状态
        logger.info("********* WSMonitorTask 到期(丢失心跳),开始改机器人 {} 为离线", robotCode);
        //关闭websoket
        WebSocket.close(robotCode);

//        RobotStatus robotStatus = new RobotStatus();
//        robotStatus.setWebSocketStatus(0);
//        robotStatus.setCurrentStatus(0);
//        robotStatus.setRobotCode(robotCode);
//        robotStatusService.updateWebSocketStatus(robotStatus);
        logger.info("********* WSMonitorTask ,改机器人 {} 为离线完成", robotCode);




    }

}
