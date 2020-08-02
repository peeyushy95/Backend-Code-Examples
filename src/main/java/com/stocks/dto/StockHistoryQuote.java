package com.stocks.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class StockHistoryQuote {
    private String date;
    private Double ema45;
    private Integer volume;
    private Double close;
    private Double sellPrice;
}
