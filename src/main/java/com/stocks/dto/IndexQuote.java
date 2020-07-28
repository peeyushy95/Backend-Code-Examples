package com.stocks.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class IndexQuote {
    private BigDecimal pChange;
    private BigDecimal lastPrice;
    private BigDecimal change;
    private String name;
}
