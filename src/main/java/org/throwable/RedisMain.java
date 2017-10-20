package org.throwable;

import org.throwable.support.RedisDaemonThread;
import redis.clients.jedis.Jedis;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/20 17:06
 */
public class RedisMain {

    public static void main(String[] args) throws Exception {
        String collectionKey = "doge";
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        long start = System.currentTimeMillis();
        jedis.zadd(collectionKey, start, "doge-init");
        jedis.zadd(collectionKey, start + 100, "doge100");
        jedis.zadd(collectionKey, start + 1000, "doge1000");
        jedis.zadd(collectionKey, start + 5000, "doge5000");
        jedis.zadd(collectionKey, start + 10000, "doge10000");
        RedisDaemonThread daemonThread = new RedisDaemonThread();
        daemonThread.init(collectionKey);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
