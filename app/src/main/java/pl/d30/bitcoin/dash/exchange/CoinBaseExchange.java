package pl.d30.bitcoin.dash.exchange;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pl.d30.bitcoin.dash.cryptocoin.Coin;

public class CoinBaseExchange extends Exchange {

    private static final String URL = "https://coinbase.com/api/v1/prices/spot_rate?currency=%s";
    private static final String URL_TICKER = "";
    private static final String URL_ORDER_BOOK = "";

    public static final String NAME = "coinbase";
    public static final String PRETTY_NAME = "Coinbase";

    private CoinBaseExchange(Context context) {
        super(context);
    }

    @Override
    protected void processTickerResponse(JsonObject json, int currency, int item, OnTickerDataAvailable cb) {

    }

    @Override
    protected Float extractPrice(JsonElement e) {
        return null;
    }

    @Override
    protected Float extractAmount(JsonElement e) {
        return null;
    }

    @Override
    protected Long getTimestamp(JsonObject json) {
        return null;
    }
    public int getId() { return COINBASE; }
    public String getName() { return NAME; }
    public String getPrettyName() { return PRETTY_NAME; }
    protected String getBaseUrl(int currency, int item) {
        return String.format(URL, getCurrencyName(currency).toUpperCase());
    }
    protected String getTickerUrlSuffix() { return URL_TICKER; }
    protected String getOrderBookUrlSuffix() { return URL_ORDER_BOOK; }
    public boolean isCurrencySupported(int currency) {
        return true;
    }

    @Override
    public boolean isItemSupported(int item) {
        return item== Coin.BTC;
    }

    // singleton magic
    private static CoinBaseExchange mInstance = null;
    public static CoinBaseExchange getInstance(Context context) {
        if( mInstance==null ) mInstance = new CoinBaseExchange(context);
        return mInstance;
    }
}
