package com.stocks.web.manager;

import com.commons.webClient.BlockingRestClient;
import com.commons.webClient.RequestDetails;
import com.stocks.dto.StockQuote;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.stocks.constants.NseUrls.getQuoteURL;
import static com.stocks.constants.NseUrls.stocksCSVURL;

@Service
public class StockManager implements IStockManager {
    private HashMap<String, String> stockCodes = null;
    private List<String> indexList = null;

    public HashMap<String, String> getStockCodes() throws Exception {
        if (this.stockCodes != null) {
            return this.stockCodes;
        } else {
            String responseBody = new BlockingRestClient<String, String>().execute(
                    RequestDetails.builder()
                            .url(stocksCSVURL)
                            .requestType(HttpMethod.GET)
                            .build(), " ", String.class);

            this.stockCodes = new HashMap<String, String>();
            BufferedReader rd = new BufferedReader(new StringReader(responseBody));
            String line = rd.readLine();
            while ((line = rd.readLine()) != null) {
                this.stockCodes.put(line.split(",")[0], line.split(",")[1]);
            }
            return this.stockCodes;
        }
    }

    @Override
    public StockQuote getQuote(String symbol) throws Exception {

        String responseBody = new BlockingRestClient<String, String>().execute(
                RequestDetails.builder()
                        .url(buildURLForQuote(symbol.toUpperCase(), 0, 0, 0))
                        .requestType(HttpMethod.GET)
                        .build(), " ", String.class);
            Element content = Jsoup.parse(responseBody).getElementById("responseDiv");
            JSONObject jsonResponse = (JSONObject) new JSONParser().parse(content.text());
            JSONArray dataArray = (JSONArray) jsonResponse.get("data");
            JSONObject data = (JSONObject) dataArray.get(0);
            StockQuote s = this.prepareStockQuote(data);
            return s;
    }

    private String buildURLForQuote(String quote, Integer illiquidValue, Integer smeFlag, Integer itpFlag) {
        return getQuoteURL + "symbol=" + URLEncoder.encode(quote) + "&illiquid=" + illiquidValue.toString() + "&smeFlag=" + smeFlag.toString() + "&itpFlag=" + itpFlag.toString();
    }

    private StockQuote prepareStockQuote(JSONObject data) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ParseException {
        StockQuote stockQuote = new StockQuote();
        for (Iterator iterator = data.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
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

    private void setFieldInObject(Object object, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field f = object.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(object, value);
    }

}
