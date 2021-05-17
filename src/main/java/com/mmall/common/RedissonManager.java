package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.springframework.stereotype.Component;
import org.redisson.config.Config;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissonManager {
    private Config config = new Config();
    private Redisson redisson = null;

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    public Redisson getRedisson() {
        return redisson;
    }

    @PostConstruct
    private void init() {
        try {
            config.useSingleServer().setAddress(redis1Ip + ":" + redis1Port);
            redisson = (Redisson) Redisson.create(config);
            log.info("Redisson init finished");
        } catch (Exception e) {
            log.error("Redisson init failed error", e);
            e.printStackTrace();
        }
    }
}
