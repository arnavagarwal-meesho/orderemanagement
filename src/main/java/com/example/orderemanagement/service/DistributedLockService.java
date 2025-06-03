package com.example.orderemanagement.service;

import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {
    private final RedisLockRegistry redisLockRegistry;
    private static final long DEFAULT_TIMEOUT_SECONDS = 10;

    public <T> T executeWithLock(String lockKey, Supplier<T> task) {
        Lock lock = redisLockRegistry.obtain(lockKey);
        try {
            boolean acquired = lock.tryLock(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("Could not acquire lock for key: " + lockKey);
            }
            log.info("Acquired lock for key: {}", lockKey);
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for lock", e);
        } finally {
            try {
                lock.unlock();
                log.info("Released lock for key: {}", lockKey);
            } catch (Exception e) {
                log.error("Error releasing lock for key: {}", lockKey, e);
            }
        }
    }
} 