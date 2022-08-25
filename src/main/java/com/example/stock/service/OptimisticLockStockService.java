package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long id, Long quantity) {
        //다른 트랜잭션인 특정 row 의 lock 을 얻는 것을 방지합니다.
        //하나의 트랜잭션이 stock 에 해당하는 row 에 lock 을 건다.
        //일반 select 는 가능
        //99개의 다른 쓰레드는 첫번째 쓰레드에 pessimistic lock 걸려있기 때문에 대기 -> 성능 저하
        Stock stock = stockRepository.findByWithOptimisticLock(id);

        stock.decrease(quantity);
    }

}
