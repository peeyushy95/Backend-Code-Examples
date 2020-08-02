package com.stocks.web.manager;

import com.commons.webClient.BlockingRestClient;
import com.commons.webClient.RequestDetails;
import com.stocks.dto.StockHistoryQuote;
import com.stocks.model.StockDetail;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.stocks.constants.NseUrls.stockHistory;

@Slf4j
@Service
public class StockBrain {

    public List<StockHistoryQuote> getStockHistory(final String symbol){
        final String responseBody = new BlockingRestClient<String, String>().execute(
                RequestDetails.builder()
                        .url(stockHistory.replace("#", symbol).replace("$", "12month"))
                        .requestType(HttpMethod.GET)
                        .build(), "", String.class);

        final String [] content = Jsoup.parse(responseBody).getElementById("csvContentDiv").text().replace("\"","").split(":");
        final List<StockHistoryQuote> stock = new ArrayList<>();
        final List<Double> prices = new ArrayList<>();
        final NumberFormat format = NumberFormat.getInstance(Locale.US);
        final DecimalFormat dec = new DecimalFormat("#0.00");

        for(int ind = 1; ind< content.length; ind++){
            final String [] row = content[ind].split(",");
                try {
                    final int volume = Integer.parseInt(row[13].replace("\"","").trim());
                    final double price = format.parse(row[7].trim()).doubleValue();
                    final String date = row[2].trim();
                    prices.add(price);
                    if(prices.size() > 45) prices.remove(0);
                    final Double ema = calEma(prices);

                    stock.add(StockHistoryQuote.builder()
                            .close(price)
                            .sellPrice(price)
                            .date(date)
                            .ema45(Double.parseDouble(dec.format(ema)))
                            .volume(volume)
                            .build());

                } catch (ParseException e) {
                        log.error(e.getMessage());
                }
        }
        return stock;
    }

    public Optional<StockDetail> processStockHistory(String symbol){
        log.info("Fetching Stock Data for {}", symbol);
        final String responseBody = new BlockingRestClient<String, String>().execute(
                RequestDetails.builder()
                        .url(stockHistory.replace("#", symbol).replace("$", "3month"))
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
            List<Double> p= prices.stream().limit(45).collect(Collectors.toList());
            Collections.reverse(p);
            final Double ema = calEma(p);
            prices.remove(0);
            p= prices.stream().limit(45).collect(Collectors.toList());
            Collections.reverse(p);
            final Double emaYesterday = calEma(p);

            final StockDetail stock = StockDetail.builder()
                    .symbol(symbol)
                    .price(price)
                    .volume(volume)
                    .delivery(delivery)
                    .emaToday(dec.format(ema))
                    .emaYesterday(dec.format(emaYesterday))
                    .updateDate(LocalDate.now().toString())
                    .flag(ema > Double.parseDouble(price) ? 5 : 4)
                    .build();

            System.out.println(stock);
            return Optional.of(stock);
        }
        return Optional.empty();
    }

    private Double calEma(List<Double> price) {
        final double multiplier = 2.0 / (price.size() + 1);
        double ema = price.get(0);
       for(int ind = 1; ind < price.size(); ind++){
           ema = ema + (price.get(ind) - ema) * multiplier;
        }
        return ema;
    }

}
