package com.how2java.websocket.websocket;




import org.apache.log4j.spi.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Tan Jingxuan
 * @date 2018/9/10
 */
public class WSMonitorTaskConsumer {

//    private final static Logger logger = LoggerFactory.getLogger(WSMonitorTaskConsumer.class);

    public static DelayQueue<WSMonitorTask> delayQueue = new DelayQueue<WSMonitorTask>();

    public static ExecutorService threadPool = Executors.newCachedThreadPool();


    public static void startThread() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //取出已经到期的
                        WSMonitorTask wsMonitorTask = delayQueue.take();
                        if (wsMonitorTask != null) {
                            // logger.info("************WSMonitorTaskConsumer,任务数：{}", delayQueue.size());
                            threadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    delayQueue.remove(wsMonitorTask);
//                                    logger.info("************WSMonitorTaskConsumer,到了设定时间，已从队列移除,robotCode：{}");
                                    wsMonitorTask.inform();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 移出队列
     *
     * @param robotCode
     */
    public static void removeTask(String robotCode) {
        for (WSMonitorTask entity : delayQueue) {
            if (Objects.equals(robotCode, entity.getRobotCode())) {
                delayQueue.remove(entity);
               // logger.info("******************** WSMonitorTaskConsumer，已移出，robotCode:{}", robotCode);
            }
        }
    }


}
