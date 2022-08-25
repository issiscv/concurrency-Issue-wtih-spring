package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, Long> {

    //read: Shared Lock은 다른 사용자가 동시에 데이터를 읽을 수는 있지만 Write는 할 수 없다.
    //Shared Lock을 얻고 데이터가 업데이트되거나 삭제되지 않도록합니다.

    //write: Exclusive Lock 으로 데이터를 변경하고자 할 때 사용되며,
    //트랜잭션이 완료될 때까지 유지되어 해당 Lock이 해제될 때까지 다른 트랜잭션은 해당 데이터에 읽기를 포함하여 접근을 할 수 없다.
    //Exclusive Lock을 획득하고 데이터를 읽거나, 업데이트하거나, 삭제하는 것을 방지합니다.
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.id = :id")
    Stock findByWithPessimisticLock(@Param("id") Long id);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from Stock s where s.id = :id")
    Stock findByWithOptimisticLock(@Param("id") Long id);

}
