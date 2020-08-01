package com.stocks.web.manager;

import com.commons.webClient.BlockingRestClient;
import com.commons.webClient.RequestDetails;
import com.stocks.model.StockDetail;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.stocks.constants.NseUrls.stockHistory1;

@Slf4j
@Service
public class StockBrain {

    public Optional<StockDetail> processStockHistory(String symbol){
        log.info("Fetching Stock Data for {}", symbol);
        final String responseBody = new BlockingRestClient<String, String>().execute(
                RequestDetails.builder()
                        .url(stockHistory1.replace("#", symbol))
                        .requestType(HttpMethod.GET)
                        .build(), "", String.class);

        Boolean dataExist = false;
        String delivery = "0";
        String volume = "0";
        String price = "0";
        final NumberFormat format = NumberFormat.getInstance(Locale.US);
        final List<Double> prices = new ArrayList<>();
        for (final Element table : Jsoup.parse(responseBody).select("table")) {
            for (final Element row : table.select("tr")) {
                final Elements tds = row.select("td");
                if (!tds.isEmpty()) {
                    dataExist = true;
                    try {
                        volume = format.parse(tds.get(10).text()).toString();
                        delivery = format.parse(tds.get(14).text()).toString();
                        price = format.parse(tds.get(8).text()).toString();
                    } catch (ParseException e) {
//                        log.error(e.getMessage());
                    }
                    prices.add(Double.parseDouble(price));
                }
            }
        }

        if (dataExist) {
            Collections.reverse(prices);
            final DecimalFormat dec = new DecimalFormat("#0.00");
            final Double ema = calEma(prices.stream().limit(45).collect(Collectors.toList()));
            prices.remove(0);
            final Double emaYesterday = calEma(prices.stream().limit(45).collect(Collectors.toList()));

            final StockDetail stock = StockDetail.builder()
                    .symbol(symbol)
                    .price(price)
                    .volume(volume)
                    .delivery(delivery)
                    .emaToday(dec.format(ema))
                    .emaYesterday(dec.format(emaYesterday))
                    .updateDate(LocalDate.now())
                    .flag(ema > Double.parseDouble(price) ? 5 : 4)
                    .build();

            System.out.println(stock);
            return Optional.of(stock);
        }
        return Optional.empty();
    }

    private Double calEma(List<Double> price) {
        final double multiplier = 2.0 / (price.size() + 1);
        final AtomicReference<Double> ema = new AtomicReference<>((double) 0);
        price.forEach(p -> {
            ema.set(ema.get() + (p - ema.get()) * multiplier);
        });
        return ema.get();
    }

}
