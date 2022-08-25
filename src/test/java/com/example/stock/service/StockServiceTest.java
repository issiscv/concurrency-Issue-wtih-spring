package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    StockService stockService;

    @Autowired
    PessimisticLockStockService pessimisticLockStockService;

    @Autowired
    StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    void stock_decrease() {
        stockService.decrease(1L, 1L);

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(stock.getQuantity(), 99L);

    }

    @Test
    void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);//비동기로 실행하는 작업을 단순화하여 사용
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();;
                }
            });
        }

        latch.await();//다른 쓰레드에서 수행중인 작업이 완료될때까지 기다려줌

        Stock stock = stockRepository.findById(1L).orElseThrow();

        //race condition 이 발생함 동시에 변경하려고 할때 발생하는 문제
        //하나의 쓰레드의 작업이 완료되기 이전에 쓰레드가 공유 자원에 접근하였기 떄문에 값이 공유 자원의 값이 다르다.
        //해결법: 공유자원에 하나의 쓰레드만 접근하기를 허용

        //공유자원을 활용하는 decrease() 메서드에 synchronized 키워들 붙여도 실패
        //이유: @Transactional 어노테이션 때문에 -> aop

        assertEquals(0L, stock.getQuantity());

    }

    @Test
    void PessimisticLock_동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);//비동기로 실행하는 작업을 단순화하여 사용
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticLockStockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();;
                }
            });
        }

        latch.await();//다른 쓰레드에서 수행중인 작업이 완료될때까지 기다려줌

        Stock stock = stockRepository.findById(1L).orElseThrow();

        //race condition 이 발생함 동시에 변경하려고 할때 발생하는 문제
        //하나의 쓰레드의 작업이 완료되기 이전에 쓰레드가 공유 자원에 접근하였기 떄문에 값이 공유 자원의 값이 다르다.
        //해결법: 공유자원에 하나의 쓰레드만 접근하기를 허용

        //공유자원을 활용하는 decrease() 메서드에 synchronized 키워들 붙여도 실패
        //이유: @Transactional 어노테이션 때문에 -> aop

        assertEquals(0L, stock.getQuantity());

    }

}