package com.stocks.web.manager;

import com.stocks.dto.StockHistoryQuote;
import com.stocks.dto.StockQuote;
import com.stocks.model.MarketTrend;
import com.stocks.model.StockDetail;

import java.util.HashMap;
import java.util.List;


public interface IStockManager {
    HashMap<String,String> getStockCodes() throws Exception;
    StockQuote getQuote(final String symbol) throws Exception;
    List<StockDetail> getDailyStockData();
    List<MarketTrend> getTrend();
    void processDailyStockData() throws Exception;
    List<StockHistoryQuote> getStockHistory(final String symbol);
}
