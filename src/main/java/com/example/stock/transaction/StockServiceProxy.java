package com.example.stock.transaction;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//스프링 aop 의 프록시 예시
public class StockServiceProxy {

    private StockService stockService;

    public void decrease(Long id, Long quantity) {
        startTransaction();//트랜잭션 부가 기능 예시

        stockService.decrease(id, quantity);
        //트랜잭션 종료 시점에 데이터베이스에 update 되는데 여기서 문제 발생
        //decrease 메서드가 완료가 되었고 실제 데이터베이스에 업데이트 되기전에 다른 쓰레드가 decrease 메서드를 호출할 수 있어서

        //여기서 다른 쓰레드가 접근이 가능하다. 위의 decrease 메서드의 쓰레드 접근이 끝났기 때문에 데이터베이스 업데이트 이전에 다른 쓰레드가 공유 자원에접근함

        endTransaction();//트랜잭션 부가 기능 예시
    }

    private void startTransaction() {
    }

    private void endTransaction() {
    }


}
