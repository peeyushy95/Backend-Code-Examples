package com.stocks.web;

import com.stocks.dto.StockHistoryQuote;
import com.stocks.dto.StockQuote;
import com.stocks.model.MarketTrend;
import com.stocks.model.StockDetail;
import com.stocks.web.manager.IStockManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/stocks")
@Slf4j
public class StockService {

    @Autowired
    private IStockManager stockManager;

    @GetMapping("/details/{symbol}")
    public ResponseEntity<List<StockHistoryQuote>> getIndex(@PathVariable final String symbol) throws Exception {
        return new ResponseEntity<>(stockManager.getStockHistory(symbol), HttpStatus.OK);
    }

    @GetMapping("/daily/{symbol}")
    public ResponseEntity<StockQuote> getStockDailyData(@PathVariable final String symbol) throws Exception {
        return new ResponseEntity<>(stockManager.getQuote(symbol), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<HashMap<String, String>> getStockCodes() throws Exception {
        return new ResponseEntity<>(stockManager.getStockCodes(), HttpStatus.OK);
    }

//    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/bull")
    public ResponseEntity<List<StockDetail>> getBull() {
        return new ResponseEntity<>(stockManager.getDailyStockData(), HttpStatus.OK);
    }

    @GetMapping("/trend")
    public ResponseEntity<List<MarketTrend>> getTrend() {
        return new ResponseEntity<>(stockManager.getTrend(), HttpStatus.OK);
    }

    @PostMapping("/process")
    public void processRecords() throws Exception{
        stockManager.processDailyStockData();
    }
}
