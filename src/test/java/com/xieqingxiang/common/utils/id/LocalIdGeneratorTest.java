package com.xieqingxiang.common.utils.id;


import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalIdGeneratorTest {

    @Test
    public void testGetLongId() throws InterruptedException {
        Set<Long> idSet = new ConcurrentSkipListSet<>();
        LocalIdGenerator idGenerator = new LocalIdGenerator();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for(int i = 0;i < 100; i ++) {
            executorService.execute(() -> {
                for(int j = 0; j < 100000;j ++) {
                    idSet.add(idGenerator.getLongId());
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        Assert.assertEquals("duplicate IDs",10000000, idSet.size());
    }
    @Test
    public void testGetIntegerId() throws InterruptedException {
        Set<Integer> idSet = new ConcurrentSkipListSet<>();
        LocalIdGenerator idGenerator = new LocalIdGenerator();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for(int i = 0;i < 100; i ++) {
            executorService.execute(() -> {
                for(int j = 0; j < 100000;j ++) {
                    idSet.add(idGenerator.getIntegerId());
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        Assert.assertEquals("duplicate IDs",10000000, idSet.size());
    }
}