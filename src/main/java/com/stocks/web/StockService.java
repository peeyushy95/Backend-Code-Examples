package com.stocks.web;

import com.stocks.dto.StockQuote;
import com.stocks.web.manager.IStockManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/stocks")
@Slf4j
public class StockService {

    @Autowired
    private IStockManager stockManager;

    @GetMapping("/details/{symbol}")
    public ResponseEntity<StockQuote> getIndex(@PathVariable final String symbol) throws Exception {
        return new ResponseEntity<>(stockManager.getQuote(symbol), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<HashMap<String, String>> getStockCodes() throws Exception {
        return new ResponseEntity<>(stockManager.getStockCodes(), HttpStatus.OK);
    }
}
