package com.stocks.repository;

import com.stocks.model.MarketTrend;
import com.stocks.model.StockDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketTrendRepository extends JpaRepository<MarketTrend, String> {

}
