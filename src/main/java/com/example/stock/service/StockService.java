package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    //자바의 synchronized 는 하나의 프로세스에서만 보장이 된다.
    //서버가 한 대 일때는 괜찮지만, 그 이상일 경우 데이터의 접근이 여러 프로세스에서 일어남
    //따라서 서버가 두대 이상일때는 synchronized 를 사용하지 않음
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
    }

}
