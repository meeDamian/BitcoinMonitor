package pl.d30.bitcoin.dash.exchange;

import android.content.Context;

import com.google.gson.JsonObject;

import pl.d30.bitcoin.D30;

public class MtGoxExchange extends Exchange {

    private static final String URL = "http://data.mtgox.com/api/2/%s%s/money/ticker_fast";

    public static final String NAME = "mtgox";
    public static final String PRETTY_NAME = "Mt.Gox";

    private MtGoxExchange(Context context) {
        super(context);
    }

    protected void processResponse(JsonObject json, int currency, int item, int priceType, OnTickerDataAvailable cb) {
        JsonObject price = D30.Json.getObject(json, "data");
        if( price!=null ) {

            long ts;
            try {
                ts = Long.parseLong(D30.Json.getString(price, "now"));

            } catch(NumberFormatException ignored) {
                ts = 0l;
            }

            price = D30.Json.getObject(price, getPriceTypeName(priceType));
            if( price!=null ) {
                lastValue = new LastValue(extractValue(price), currency, item);
                if( ts>0 ) lastValue.setTimestamp(ts/1000000);
                if( cb!=null ) cb.onTicker(lastValue);
            }
        }
    }

    // MtGox specific functions:
    private String extractValue(JsonObject priceObject) {
        return D30.Json.getString(priceObject, "value");
    }

    protected String getUrl(int currency, int item) {
        return String.format(URL, getItemName(item), getCurrencyName(currency));
    }

    public String getName() {
        return NAME;
    }

    public String getPrettyName() {
        return PRETTY_NAME;
    }

    @Override
    public boolean isCurrencySupported(int currency) {
        return true;
    }

    @Override
    public boolean isItemSupported(int item) {
        return item==BTC;
    }

    // singleton magic
    private static MtGoxExchange mInstance = null;
    public static MtGoxExchange getInstance(Context context) {
        if( mInstance==null ) mInstance = new MtGoxExchange(context);
        return mInstance;
    }
}
