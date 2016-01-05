package pl.d30.bitcoin.dash.exchange;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.cryptocoin.Coin;

public class BtceExchange extends Exchange {

    private static final String URL = "https://btc-e.com/api/2/%s_%s/";
    private static final String URL_TICKER = "ticker";
    private static final String URL_ORDER_BOOK = "depth";

    public static final String NAME = "btce";
    public static final String PRETTY_NAME = "BTC-e";

    public static final int a = 0;

    private BtceExchange(Context context) {
        super(context);
    }

    protected void processTickerResponse(JsonObject json, int currency, int item, OnTickerDataAvailable cb) {
        JsonObject ticker = D30.Json.getObject(json, "ticker");
        if(ticker != null) {

            float price = D30.Json.getFloat(ticker, getPriceTypeName(PRICE_LAST));
            if(
                lastValue==null
                ||
                lastValue.getItem()!=item
                ||
                lastValue.getCurrency()!=currency

            ) {
                lastValue = new LastValue(price, currency, item);

                lastValue.setSellValue(D30.Json.getFloat(ticker, getPriceTypeName(PRICE_SELL)));
                lastValue.setBuyValue(D30.Json.getFloat(ticker, getPriceTypeName(PRICE_BUY)));

            } else lastValue.setLastValue(price);

            long ts = D30.Json.getLong(ticker, "updated");

            if(ts > 0)
                lastValue.setTickerTimestamp(ts);

            if(cb != null)
                cb.onTicker(getId(), lastValue);
        }
    }

    protected Float extractPrice(JsonElement e) {
        return e.isJsonArray()
            ? e.getAsJsonArray().get(0).getAsFloat()
            : null;
    }
    protected Float extractAmount(JsonElement e) {
        return e.isJsonArray()
            ? e.getAsJsonArray().get(1).getAsFloat()
            : null;
    }
    protected Long getTimestamp(JsonObject json) {
        return System.currentTimeMillis()/1000;
    }

    public int getId() {
        return BTCE;
    }
    public String getName() {
        return NAME;
    }
    public String getPrettyName() {
        return PRETTY_NAME;
    }
    protected String getBaseUrl(int currency, int item) {
        return String.format(URL, Coin.getName(item).toLowerCase(), getCurrencyName(currency).toLowerCase());
    }
    protected String getTickerUrlSuffix() {
        return URL_TICKER;
    }
    protected String getOrderBookUrlSuffix() {
        return URL_ORDER_BOOK;
    }
    public boolean isCurrencySupported(int currency) {
        return currency == USD || currency == EUR;
    }
    public boolean isItemSupported(int item) {
        return item == Coin.BTC || item == Coin.LTC;
    }


    // singleton magic
    private static BtceExchange mInstance = null;
    public static BtceExchange getInstance(Context context) {
        if(mInstance == null)
            mInstance = new BtceExchange(context);

        return mInstance;
    }
}