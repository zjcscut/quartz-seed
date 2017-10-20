package org.throwable.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/20 16:50
 */
public class RedisDaemonThread {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisDaemonThread.class);
    private AtomicBoolean start = new AtomicBoolean(false);
    private String collectionKey;

    public void init(String collectionKey) {
        this.collectionKey = collectionKey;
        start.compareAndSet(false, true);
        Thread thread = new Thread(buildExecutionRunnable(), "redis-daemon-thread");
        thread.setDaemon(true);
        thread.start();
    }

    private Runnable buildExecutionRunnable() {
        final Jedis jedis = new Jedis("127.0.0.1", 6379);
        return () -> {
            while (start.get()) {
                long end = System.currentTimeMillis();  //结束的score为当前系统毫秒数
                long start = end - 30 * 60 * 1000;     //起始的score为结束score减半小时
                Set<String> zrange = jedis.zrangeByScore(collectionKey, start, end);
                if (null != zrange && !zrange.isEmpty()) {
                    for (String value : zrange) {
                        EXECUTOR.execute(() -> LOGGER.info("当前执行的数据为---> {}", value));
                    }
                }
                LOGGER.info("删除记录{}条!!", jedis.zremrangeByScore(collectionKey, start, end));
                sleep();
            }
        };
    }

    public void stop() {
        start.compareAndSet(true, false);
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            //ignore
        }
    }
}
