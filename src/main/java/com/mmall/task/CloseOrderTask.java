package com.mmall.task;

import com.mmall.common.Constants;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    IOrderService iOrderService;

    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1() {
        log.info("close order cronjob start");
        int timeout = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        iOrderService.closeOrders(timeout);
        log.info("close order cronjob end");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2() {
        log.info("close order cronjob start");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "5000"));
        Long setnxResult = RedisShardedPoolUtil.setnx(
            Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
            String.valueOf(System.currentTimeMillis()) + lockTimeout
        );
        if (setnxResult != null && setnxResult.intValue() == 1) {
            // acquired lock
            closeOrder(Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            log.info("did not get lock: {}", Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("close order cronjob end");
    }

    private void closeOrder(String lockName) {
        // set lock expire time 50 seconds to avoid dead lock
        RedisShardedPoolUtil.expire(lockName, 50);
        log.info(
            "acquired lock: {}, thread name: {}",
            Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
            Thread.currentThread().getName()
        );
        int timeout = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        iOrderService.closeOrders(timeout);
        RedisShardedPoolUtil.delete(Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info(
                "released lock: {}, thread name: {}",
                Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                Thread.currentThread().getName()
        );
        log.info("======================");
    }
}
