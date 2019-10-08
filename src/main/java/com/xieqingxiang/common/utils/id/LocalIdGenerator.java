package com.xieqingxiang.common.utils.id;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: common-utils
 * @description:
 * @author: 谢庆香
 * @create: 2019-10-08 09:24
 **/
public class LocalIdGenerator implements IdGenerator {
    private static final long twepoch = System.currentTimeMillis();
    private static final long workerIdBits = 5L;
    private static final long datacenterIdBits = 5L;
    private static final long sequenceBits = 12L;
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static final long datacenterIdShift = workerIdBits + sequenceBits;
    private static final long workerIdShift = sequenceBits;
    private static long lastTimestamp = -1L;
    private long sequence = 0L;
    private long workerId = 1L;
    private long processId = 1L;

    public LocalIdGenerator() {
    }

    public LocalIdGenerator(long sequence, long workerId, long processId) {
        this.sequence = sequence;
        this.workerId = workerId;
        this.processId = processId;
    }
    public synchronized long getLongId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new IdGeneratorException("Clock moved backwards.  Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        } else {
            if (lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1L & 4095L;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }
        }
        lastTimestamp = timestamp;
        long nextId = ((timestamp - twepoch) << timestampLeftShift)
                | this.processId << datacenterIdShift
                | this.workerId << workerIdShift
                | this.sequence;
        return nextId;
    }
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public int getIntegerId() {
        long longId = getLongId();
        if(longId <= Integer.MAX_VALUE) {
            return (int)longId;
        }else {
            String longIdStr = longId + "";
            long newLongId = Long.valueOf(longIdStr.substring(longIdStr.length() - 10));
            if(newLongId <= Integer.MAX_VALUE) {
                return (int)newLongId;
            }else {
                return Integer.valueOf(longIdStr.substring(longIdStr.length() - 9));
            }
        }
    }
}