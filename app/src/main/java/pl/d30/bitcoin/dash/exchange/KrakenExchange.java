package pl.d30.bitcoin.dash.exchange;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class KrakenExchange extends Exchange {

    private static final String URL = "https://api.kraken.com/0/public/";
    private static final String URL_TICKER = "Ticker?pair=%s";
    private static final String URL_ORDER_BOOK = "Depth?pair=%s";

    public static final String NAME = "kraken";
    public static final String PRETTY_NAME = "Kraken";

    private KrakenExchange(Context context) {
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

    public int getId() { return KRAKEN; }
    public String getName() { return NAME; }
    public String getPrettyName() { return PRETTY_NAME; }
    protected String getBaseUrl(int currency, int item) { return URL; }

    @Override
    protected String getTickerUrlSuffix() {
        return null;
    }

    @Override
    protected String getOrderBookUrlSuffix() {
        return null;
    }

    @Override
    public boolean isCurrencySupported(int currency) {
        return false;
    }

    @Override
    public boolean isItemSupported(int item) {
        return false;
    }

    private static KrakenExchange mInstance = null;
    public static KrakenExchange getInstance(Context context) {
        if(mInstance == null)
            mInstance = new KrakenExchange(context);

        return mInstance;
    }
}
