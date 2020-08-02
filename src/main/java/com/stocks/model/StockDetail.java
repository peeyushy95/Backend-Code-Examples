package com.stocks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity(name = "stock_detail")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockDetail {

    @Id
    private String symbol;

    private String price;

    @Column(name = "ema_today")
    private String emaToday;

    @Column(name = "ema_yesterday")
    private String emaYesterday;
    private String volume;
    private String delivery;
    private Integer flag;

    @Column(name = "updated_at")
    private String updateDate;

}