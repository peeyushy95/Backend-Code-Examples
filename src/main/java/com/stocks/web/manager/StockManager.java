package com.stocks.web.manager;

import com.commons.webClient.BlockingRestClient;
import com.commons.webClient.RequestDetails;
import com.stocks.dto.StockHistoryQuote;
import com.stocks.dto.StockQuote;
import com.stocks.model.MarketTrend;
import com.stocks.model.StockDetail;
import com.stocks.repository.MarketTrendRepository;
import com.stocks.repository.StockDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.stocks.constants.NseUrls.*;

@Service
@Slf4j
public class StockManager implements IStockManager {

    @Autowired
    private StockDetailRepository stockDetailRepository;

    @Autowired
    private MarketTrendRepository marketTrendRepository;

    @Autowired
    private StockBrain stockBrain;

    private final HashMap<String, String> stockCodes = new HashMap<>();

    public HashMap<String, String> getStockCodes() throws Exception {
        final String responseBody = new BlockingRestClient<String, String>().execute(
                RequestDetails.builder()
                        .url(stocksCSVURL)
                        .requestType(HttpMethod.GET)
                        .build(), " ", String.class);

        final BufferedReader rd = new BufferedReader(new StringReader(responseBody));
        String line = rd.readLine();
        while ((line = rd.readLine()) != null) {
            stockCodes.put(line.split(",")[0], line.split(",")[1]);
        }
        return this.stockCodes;
    }

    @Override
    public List<StockHistoryQuote> getStockHistory(final String symbol){
        return stockBrain.getStockHistory(symbol);
    }

    @Override
    public void processDailyStockData() throws Exception {
        Integer totalCount = 0;
        AtomicReference<Integer> bullCount = new AtomicReference<>(0);
        final DecimalFormat dec = new DecimalFormat("#0.00");
        for (final Map.Entry<String, String> e : getStockCodes().entrySet()) {
            Optional<StockDetail> stock = stockBrain.processStockHistory(e.getKey());
            totalCount++;
            stock.ifPresent(stockDetail -> {
                stockDetailRepository.save(stockDetail);
                if(stockDetail.getFlag() == 5)
                bullCount.getAndSet(bullCount.get() + 1);
            });
            final MarketTrend trend = MarketTrend.builder()
                    .ratio(Double.parseDouble(dec.format(bullCount.get().doubleValue()/totalCount.doubleValue())))
                    .date(LocalDate.now().toString())
                    .build();

            marketTrendRepository.save(trend);
        }
    }

    @Override
    public List<MarketTrend> getTrend(){
        return marketTrendRepository.findAll();
    }

    @Override
    public List<StockDetail> getDailyStockData() {
        return stockDetailRepository.findAll();
    }

    @Override
    public StockQuote getQuote(String symbol) throws Exception {
        final String responseBody = new BlockingRestClient<String, String>().execute(
                RequestDetails.builder()
                        .url(buildURLForQuote(symbol.toUpperCase(), 0, 0, 0))
                        .requestType(HttpMethod.GET)
                        .build(), " ", String.class);
        final Element content = Jsoup.parse(responseBody).getElementById("responseDiv");
        final JSONObject jsonResponse = (JSONObject) new JSONParser().parse(content.text());
        final JSONArray dataArray = (JSONArray) jsonResponse.get("data");
        final JSONObject data = (JSONObject) dataArray.get(0);
        return prepareStockQuote(data);
    }

    private String buildURLForQuote(String quote, Integer illiquidValue, Integer smeFlag, Integer itpFlag) {
        return getQuoteURL + "symbol=" + URLEncoder.encode(quote) + "&illiquid=" + illiquidValue.toString() + "&smeFlag=" + smeFlag.toString() + "&itpFlag=" + itpFlag.toString();
    }

    private StockQuote prepareStockQuote(JSONObject data) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ParseException {
        final StockQuote stockQuote = new StockQuote();
        for (Object o : data.keySet()) {
            String key = (String) o;
            if (data.get(key).equals("-")) {
                this.setFieldInObject(stockQuote, key, null);
            } else if (key.equalsIgnoreCase("priceBand") && data.get(key).toString().equalsIgnoreCase("No Band")) {
                this.setFieldInObject(stockQuote, key, null);
            } else if (key.equalsIgnoreCase("secDate")) {
                this.setFieldInObject(stockQuote, key, new SimpleDateFormat("dd-MMM-yyyy").parse(data.get(key).toString()));
            } else if (key.equalsIgnoreCase("isExDateFlag")) {
                this.setFieldInObject(stockQuote, key, (boolean) data.get(key));
            } else if (key.toLowerCase().contains("date") || key.equalsIgnoreCase("cm_adj_high_dt") || key.equalsIgnoreCase("cm_adj_low_dt")) {
                this.setFieldInObject(stockQuote, key, new SimpleDateFormat("dd-MMM-yyyy").parse(data.get(key).toString()));
            } else if (key.toLowerCase().contains("price")
                    || key.toLowerCase().contains("quantity")
                    || key.toLowerCase().contains("value")
                    || key.toLowerCase().contains("margin")
                    || key.equalsIgnoreCase("varMargin")
                    || key.equalsIgnoreCase("securityVar")
                    || key.equalsIgnoreCase("open")
                    || key.equalsIgnoreCase("previousClose")
                    || key.equalsIgnoreCase("dayHigh")
                    || key.equalsIgnoreCase("dayLow")
                    || key.equalsIgnoreCase("high52")
                    || key.equalsIgnoreCase("low52")
                    || key.equalsIgnoreCase("change")
                    || key.equalsIgnoreCase("applicableMargin")
                    || key.equalsIgnoreCase("pChange")
                    || key.equalsIgnoreCase("cm_ffm")
                    || key.equalsIgnoreCase("totalTradedVolume")) {
                this.setFieldInObject(stockQuote, key, new BigDecimal(((String) data.get(key)).replaceAll(",", "")));
            } else {
                this.setFieldInObject(stockQuote, key, (String) data.get(key));
            }
        }
        return stockQuote;
    }

    private void setFieldInObject(Object object, String fieldName, Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        final Field f = object.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(object, value);
    }

}
