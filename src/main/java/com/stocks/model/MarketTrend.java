package com.stocks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "market_trend")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketTrend {
    @Id
    private String date;
    private Double ratio;
}
