package com.mmall.util;

import com.mmall.common.RedisConnectionPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisUtil {
    public static Long expire(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisConnectionPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key:{} error", key, e);
            RedisConnectionPool.returnBrokenResource(jedis);
            return result;
        }
        RedisConnectionPool.returnResource(jedis);
        return result;
    }

    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisConnectionPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error", key, value, e);
            RedisConnectionPool.returnBrokenResource(jedis);
            return result;
        }
        RedisConnectionPool.returnResource(jedis);
        return result;
    }

    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisConnectionPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisConnectionPool.returnBrokenResource(jedis);
            return result;
        }
        RedisConnectionPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisConnectionPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisConnectionPool.returnBrokenResource(jedis);
            return result;
        }
        RedisConnectionPool.returnResource(jedis);
        return result;
    }

    public static Long delete(String key) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisConnectionPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("delete key:{} error", key, e);
            RedisConnectionPool.returnBrokenResource(jedis);
            return result;
        }
        RedisConnectionPool.returnResource(jedis);
        return result;
    }
}
