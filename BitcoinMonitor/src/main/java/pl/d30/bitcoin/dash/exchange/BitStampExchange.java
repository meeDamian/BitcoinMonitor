package pl.d30.bitcoin.dash.exchange;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.cryptocoin.Coin;

public class BitStampExchange extends Exchange {

    private static final String URL = "https://www.bitstamp.net/api/";
    private static final String URL_TICKER = "ticker/";
    private static final String URL_ORDER_BOOK = "order_book/";

    public static final String NAME = "bitstamp";
    public static final String PRETTY_NAME = "Bitstamp";

    private BitStampExchange(Context context) {
        super(context);
    }

    protected void processTickerResponse(JsonObject json, int currency, int item, OnTickerDataAvailable cb) {

        String price = D30.Json.getString(json, getPriceTypeName(PRICE_LAST));
        lastValue = new LastValue(price, currency, item);

        lastValue.setSellValue( D30.Json.getString(json, getPriceTypeName(PRICE_SELL)) );
        lastValue.setBuyValue( D30.Json.getString(json, getPriceTypeName(PRICE_BUY)) );

        try {
            lastValue.setTimestamp( getTimestamp(json) );

        } catch(NumberFormatException ignored) {}

        if( cb!=null ) cb.onTicker(getId(), lastValue);
    }

    protected Float extractPrice(JsonElement e) {
        return e.isJsonArray() ? Float.parseFloat(e.getAsJsonArray().get(0).getAsString()) : null;
    }
    protected Float extractAmount(JsonElement e) {
        return e.isJsonArray() ? Float.parseFloat(e.getAsJsonArray().get(1).getAsString()) : null;
    }
    protected Long getTimestamp(JsonObject json) {
        return Long.parseLong(D30.Json.getString(json, "timestamp"));
    }

    public static String getPriceTypeName(int priceType) {
        switch( priceType ) {
            case PRICE_BUY: return "ask";
            case PRICE_SELL: return "bid";
        }
        return Exchange.getPriceTypeName(priceType);
    }

    public int getId() {
        return BITSTAMP;
    }
    public String getName() {
        return NAME;
    }
    public String getPrettyName() {
        return PRETTY_NAME;
    }
    protected String getBaseUrl(int currency, int item) {
        return URL;
    }
    protected String getTickerUrlSuffix() {
        return URL_TICKER;
    }
    protected String getOrderBookUrlSuffix() {
        return URL_ORDER_BOOK;
    }
    public boolean isCurrencySupported(int currency) {
        return currency==USD;
    }
    public boolean isItemSupported(int item) {
        return item==Coin.BTC;
    }


    // singleton magic
    private static BitStampExchange mInstance = null;
    public static BitStampExchange getInstance(Context context) {
        if( mInstance==null ) mInstance = new BitStampExchange(context);
        return mInstance;
    }
}
