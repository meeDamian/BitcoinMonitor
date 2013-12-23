package pl.d30.bitcoin.dash.exchange;

import android.content.Context;

import com.google.gson.JsonObject;

import pl.d30.bitcoin.D30;

public class BtceExchange extends Exchange {

    private static final String URL = "https://btc-e.com/api/2/%s_%s/ticker";;

    public static final String NAME = "btce";
    public static final String PRETTY_NAME = "BTC-e";


    private BtceExchange(Context context) {
        super(context);
    }

    @Override
    protected void processResponse(JsonObject json, int currency, int item, OnTickerDataAvailable cb) {
        JsonObject ticker = D30.Json.getObject(json, "ticker");
        if( ticker!=null ) {
            float price = D30.Json.getFloat(ticker, getPriceTypeName(PRICE_LAST));
            lastValue = new LastValue(price, currency, item);

            lastValue.setSellValue( D30.Json.getFloat(ticker, getPriceTypeName(PRICE_SELL)) );
            lastValue.setBuyValue(D30.Json.getFloat(ticker, getPriceTypeName(PRICE_BUY)));
             long ts = D30.Json.getLong(ticker, "updated");
            if( ts>0 ) lastValue.setTimestamp(ts);

            if( cb!=null ) cb.onTicker(getId(), lastValue);
        }
    }

    @Override
    protected String getUrl(int currency, int item) {
        return String.format(URL, getItemName(item).toLowerCase(), getCurrencyName(currency).toLowerCase());
    }

    @Override
    public int getId() {
        return BTCE;
    }

    public String getName() {
        return NAME;
    }

    public String getPrettyName() {
        return PRETTY_NAME;
    }

    @Override
    public boolean isCurrencySupported(int currency) {
        return currency==USD || currency==EUR;
    }

    @Override
    public boolean isItemSupported(int item) {
        return item==BTC || item==LTC;
    }

    // singleton magic
    private static BtceExchange mInstance = null;
    public static BtceExchange getInstance(Context context) {
        if( mInstance==null ) mInstance = new BtceExchange(context);
        return mInstance;
    }
}