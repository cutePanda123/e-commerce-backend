package com.mmall.task;

import com.mmall.common.Constants;
import com.mmall.common.RedisShardedPool;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    IOrderService iOrderService;

    @Autowired
    RedissonManager redissonManager;

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

//    @PreDestroy
//    public void releaseV2Lock() {
//        RedisShardedPoolUtil.delete(Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV3() {
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
            String lockTimestamp = RedisShardedPoolUtil.get(Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if (lockTimestamp != null && System.currentTimeMillis() > Long.parseLong(lockTimestamp)) {
                String getSetResult = RedisShardedPoolUtil.getSet(
                        Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                        String.valueOf(System.currentTimeMillis()) + lockTimeout
                );

                if (getSetResult == null || StringUtils.equals(lockTimestamp, getSetResult)) {
                    // acquired the lock
                    closeOrder(Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                } else {
                    log.info("did not get lock: {}", Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            } else {
                log.info("did not get lock: {}", Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }
        log.info("close order cronjob end");
    }


    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV4() {
        RLock lock = redissonManager.getRedisson().getLock(Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean acquiredLock = false;
        try {
            if (acquiredLock = lock.tryLock(0, 50, TimeUnit.SECONDS)) {
                log.info("Redisson lock acquired: {}, ThreadName: {}", Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
                int timeout = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
                iOrderService.closeOrders(timeout);
            } else {
                log.info("Redisson lock not acquired: {}, ThreadName: {}", Constants.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.error("Redisson lock acquired failed: ", e);
            e.printStackTrace();
        } finally {
            if (!acquiredLock) {
                return;
            }
            lock.unlock();
            log.info("Redisson lock released");
        }
    }

    private void closeOrder(String lockName) {
        // set lock expire time 50 seconds to avoid dead lock
        RedisShardedPoolUtil.expire(
            lockName,
            Integer.parseInt(PropertiesUtil.getProperty("lock.timeout", "5000")) / 1000
        );
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
