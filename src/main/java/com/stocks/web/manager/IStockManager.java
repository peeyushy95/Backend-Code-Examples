package com.stocks.web.manager;

import com.stocks.dto.StockQuote;

import java.util.HashMap;


public interface IStockManager {
    HashMap<String,String> getStockCodes() throws Exception;
    StockQuote getQuote(String symbol) throws Exception;
}
