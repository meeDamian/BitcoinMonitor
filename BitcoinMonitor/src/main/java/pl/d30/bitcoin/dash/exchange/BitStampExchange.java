package pl.d30.bitcoin.dash.exchange;

import android.content.Context;

import com.google.gson.JsonObject;

import pl.d30.bitcoin.D30;

public class BitStampExchange extends Exchange {

    private static final String URL = "https://www.bitstamp.net/api/ticker/";

    public static final String NAME = "bitstamp";
    public static final String PRETTY_NAME = "Bitstamp";

    private BitStampExchange(Context context) {
        super(context);
    }

    @Override
    protected void processResponse(JsonObject json, int currency, int priceType, OnTickerDataAvailable cb) {
        String price = D30.Json.getString(json, getPriceTypeName(priceType));
        if( cb!=null ) cb.onTicker(lastValue = new LastValue(price, currency), json);
    }

    @Override
    protected String getUrl(int currency, int item) {
        return URL;
    }

    protected static String getPriceTypeName(int priceType) {
        switch( priceType ) {
            case PRICE_BUY: return "ask";
            case PRICE_SELL: return "bid";
        }
        return Exchange.getPriceTypeName(priceType);
    }

    public String getName() {
        return NAME;
    }

    public String getPrettyName() {
        return PRETTY_NAME;
    }

    // singleton magic
    private static BitStampExchange mInstance = null;
    public static BitStampExchange getInstance(Context context) {
        if( mInstance==null ) mInstance = new BitStampExchange(context);
        return mInstance;
    }
}
