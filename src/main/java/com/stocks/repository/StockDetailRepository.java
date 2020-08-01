package com.stocks.repository;

import com.stocks.model.StockDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDetailRepository extends JpaRepository<StockDetail, String> {

}
