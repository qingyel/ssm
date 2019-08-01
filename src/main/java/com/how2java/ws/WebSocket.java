package com.how2java.ws;


import com.alibaba.fastjson.JSON;
import com.how2java.pojo.MessageVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ServerEndpoint("/apiWebSocket")
@Component
public class WebSocket {

    //private RobotStatusService robotStatusService = SpringContextHolder.getBean("robotStatusService");
    private static Logger logger = LoggerFactory.getLogger(WebSocket.class);
    private Session session;
    private String robotCode;
    //通知机器人需要做什么任务，上传日志 uploadAppLog, 更新机器人运行状态 updateRobotStatus
    //websocket = new WebSocket("ws://localhost:8088/api/webSocket/uploadAppLog?robotCode=testRobot");
    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<>();
    private static Map<String, WebSocket> robotClientMap = new ConcurrentHashMap<>();
    public static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void setRobotClientMap(Map<String, WebSocket> robotClientMap) {
        WebSocket.robotClientMap = robotClientMap;
    }

    public void startTest(){
        threadPool.execute(() ->{
            while (true){
                robotClientMap.forEach((k,v) ->{
                    String now = LocalTime.now().toString();
                    MessageVo messageVo  = new MessageVo();
                    messageVo.setMessage(now);
                    messageVo.setMessageType(0);
                    messageVo.setRobotCode(k);
                    sendMessage(messageVo);
                });
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    private static ApplicationContext applicationContext;
//    private RobotStatusService robotStatusService;

//    public static void setApplicationContext(ApplicationContext applicationContext) {
//        WebSocket.applicationContext = applicationContext;
//    }


    @OnOpen
    public void onOpen(Session session) {
        logger.info("**********开始onOpen");
        this.session = session;
        Map<String, List<String>> params = session.getRequestParameterMap();
        List<String> p = params.get("robotCode");
        if (p != null && p.size() > 0) {
            String robotCode = p.get(0);
            this.robotCode = robotCode;
            if (StringUtils.isNotBlank(robotCode)) {
                if (webSockets.add(this)) {
                    robotClientMap.put(robotCode, this);
                    logger.info("当前连接数:{}", webSockets.size());
                    try {
                        this.session.getBasicRemote().sendText("你已连接成功");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(webSockets.size() == 1){
                this.startTest();
                }


                logger.info("**********【新连接】 机器人 robotCode:{} 已建立连接,当前连接数:{}", this.robotCode, webSockets.size());
                //更新连接状态
//                RobotStatus robotStatus = new RobotStatus();
//                robotStatus.setWebSocketStatus(1);
//                robotStatus.setRobotCode(robotCode);
//                if (applicationContext != null) {
//                    robotStatusService = applicationContext.getBean(RobotStatusService.class);
//                }
//                if (robotStatusService != null) {
//                    robotStatusService.updateWebSocketStatus(robotStatus);
//                    logger.info("*********onOpen,更新了机器人 {} 的websocket状态", robotCode);
//                }
                //移出延迟队列里的该账号
                WSMonitorTaskConsumer.removeTask(robotCode);
                WSMonitorTaskConsumer.delayQueue.add(new WSMonitorTask(System.currentTimeMillis()+30000, robotCode));
            } else {
                notLogin(session);
            }
        } else {
            notLogin(session);
        }
    }

    private void notLogin(Session session) {
        //未登录发送未登录消息
        logger.info("OnOpen时，无法获取robotCode,不可连接");
        MessageVo msg = new MessageVo();
        msg.setMessageType(999);
        msg.setMessage("未登录");
        try {
            session.getBasicRemote().sendText(msg.toJson());
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose() {
        // logger.info("**********开始OnClose");
        webSockets.remove(this);
        if (StringUtils.isNotBlank(this.robotCode)) {
            robotClientMap.remove(this.robotCode);
        }
        //更新连接状态
//        RobotStatus robotStatus = new RobotStatus();
//        robotStatus.setWebSocketStatus(0);
//        robotStatus.setCurrentStatus(0);
//        robotStatus.setRobotCode(robotCode);
//        if (applicationContext != null) {
//            robotStatusService = applicationContext.getBean(RobotStatusService.class);
//        }
//        if (robotStatusService != null) {
//            robotStatusService.updateWebSocketStatus(robotStatus);
//            logger.info("********* onClose,更新了机器人 {} 的websocket状态", robotCode);
//        }
        //logger.info("【账号连接断开】 账号{}连接断开, 当前连接总数:{}", this.robotCode, webSockets.size());
        logger.info("【账号连接断开】 账号{}连接断开", this.robotCode);

        //移出延迟队列里的该账号
        WSMonitorTaskConsumer.removeTask(robotCode);
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("OnMessage:{}", message);
//        MessageVo messageVo = JacksonUtil.readValue(message, MessageVo.class);
        MessageVo messageVo =  JSON.parseObject(message,MessageVo.class);
        if (messageVo.getMessageType() == 0 && StringUtils.isNotBlank(messageVo.getRobotCode())) {
            //心跳 {"messageType":0, "robotCode":"tan_robot"}
            //this.sendMessage(messageVo.getRobotCode(), message);
            WebSocket s = robotClientMap.get(messageVo.getRobotCode());
            if (s != null && s.session.isOpen()) {
                try {
                    //TODO TEST
                    s.session.getBasicRemote().sendText(message);
                    logger.info("【websocket 心跳】 robotCode:" + robotCode + ",message:" + message);
                    //来个心跳就先从队列中移除此账号，刷新过期30s,再加入队列，到期了设置断开状态
                    WSMonitorTaskConsumer.removeTask(robotCode);
                    WSMonitorTaskConsumer.delayQueue.add(new WSMonitorTask(System.currentTimeMillis()+30000, robotCode));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logger.info("【websocket 心跳-断开】账号:{},找不到websocket对象，或连接已断开", messageVo.getRobotCode());
            }
        }

    }


    public static void sendMessage(MessageVo messageVo) {
        String message = messageVo.toJson();
        sendMessage(messageVo.getRobotCode(),message);
    }

    /**
     * 发送消息给指定机器人
     *
     * @param
     * @param message
     */
    public static void sendMessage(String robotCode, String message) {
        logger.info("【websocket准备发消息】 robotCode:" + robotCode + ",message:" + message);
        WebSocket s = robotClientMap.get(robotCode);
        if (s != null && s.session.isOpen()) {
            try {
                s.session.getBasicRemote().sendText(message);
                logger.info("【websocket发出消息】 robotCode:" + robotCode + ",message:" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("找不到websocket对象，或连接已断开， robotCode:" + robotCode);
        }
    }

    //
    public static void close(String robotCode){
        if(StringUtils.isNotBlank(robotCode)){
            WebSocket ws = robotClientMap.get(robotCode);
            webSockets.remove(ws);
            robotClientMap.remove(robotCode);
        }
    }
}
