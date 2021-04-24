package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.JedisPool;

public class RedisConnectionPool {
    private static JedisPool pool;

    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    private static Integer maxIdx = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));
    private static Integer minIdx = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "10"));

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));
}
