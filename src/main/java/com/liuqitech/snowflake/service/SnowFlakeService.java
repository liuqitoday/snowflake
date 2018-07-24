package com.liuqitech.snowflake.service;

public class SnowFlakeService {

    private static final long TIMESTAMP_BIT_NUM = 41L;
    private static final long SEQUENCE_BIT_NUM = 12L;
    private static final long MACHINE_BIT_NUM = 5L;

    private static final long SEQUENCE_MAX_VALUE = -1L ^ (-1L << SEQUENCE_BIT_NUM);

    private long BEGIN_TIMESTAMP = 1262275200000L;

    private long lastTimeStamp = -1L;
    private long sequence = 0L;

    public Long getSnowFlake(Long machineId) {
        long currentTimeStamp = System.currentTimeMillis();
        if (currentTimeStamp < lastTimeStamp) {
            throw new RuntimeException("服务器时间异常！");
        }
        if (currentTimeStamp == lastTimeStamp) {
            sequence = (sequence + 1) & SEQUENCE_MAX_VALUE;
            if (sequence == 0) {
                currentTimeStamp = getNextTimeMillis();
            }
        } else {
            lastTimeStamp = currentTimeStamp;
            sequence = 0;
        }
        return (currentTimeStamp - BEGIN_TIMESTAMP) << (MACHINE_BIT_NUM + SEQUENCE_BIT_NUM)
                | machineId << SEQUENCE_BIT_NUM
                | sequence;
    }

    private long getNextTimeMillis() {
        long currentTimeMillis = 0L;
        do {
            currentTimeMillis = System.currentTimeMillis();
        } while (currentTimeMillis <= lastTimeStamp);
        return currentTimeMillis;
    }

    public static void main(String[] args) {
        long beginTime = System.currentTimeMillis();
        SnowFlakeService snowFlakeService = new SnowFlakeService();
        int count = 0;
        while ((System.currentTimeMillis() - beginTime) <= 1000) {
            Long snowFlake = snowFlakeService.getSnowFlake(1L);
            System.out.println(snowFlake);
            count++;
        }
        System.out.println("产生个数" + count);
    }
}
