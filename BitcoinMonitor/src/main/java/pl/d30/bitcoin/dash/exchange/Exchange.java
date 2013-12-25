package pl.d30.bitcoin.dash.exchange;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.DecimalFormat;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;

public abstract class Exchange {

    // exchanges:
    public static final int MTGOX = 0;
    public static final int BITSTAMP = 1;
    public static final int BTCE = 2;

    // urls:
    public static final int TICKER = 0;
    public static final int ORDER_BOOK = 1;

    // fiat currencies:
    public static final int USD = 0;
    public static final int GBP = 1;
    public static final int CAD = 2;
    public static final int AUD = 3;
    public static final int PLN = 4;
    public static final int JPY = 5;
    public static final int EUR = 6;
    public static final int NOK = 7;
    public static final int SGD = 8;

    // prices:
    public static final int PRICE_LAST = 0;
    public static final int PRICE_BUY = 1;
    public static final int PRICE_SELL = 2;

    // priorities:
    public static final boolean PERCENTAGE = true;
    public static final boolean CURRENCY = false;

    // other:
    private static final long FRESHNESS = 30; // seconds

    protected Context context;
    protected LastValue lastValue;

    public Exchange(Context context) {
        this.context = context;
    }

    public void getTicker(int currency, int item, float amount, OnTickerDataAvailable cb) {
        if(
            lastValue!=null
            &&
            lastValue.isFresh()
            &&
            lastValue.getCurrency()==currency
            &&
            lastValue.getItem()==item
            &&
            lastValue.isOrderBook()==(amount!=0f)

        ) {
            cb.onTicker(getId(), lastValue);

        } else downloadResponse( currency, item, amount, cb);
    }
    public void getTicker(int currency, int item, OnTickerDataAvailable cb) {
        getTicker(currency, item, 0f, cb);
    }

    protected void downloadResponse(final int currency, final int item, final float amount, final OnTickerDataAvailable cb) {
        Ion.with(context, getUrl(currency, item, (amount==0f) ? TICKER : ORDER_BOOK))
            .setHeader("User-Agent", "DashClock Bitcoin Monitor " + D30.getAppVersion(context) + ", " + D30.getDeviceInfo())
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject json) {
                if( e!=null ) Log.w(D30.LOG, e.toString());
                if( json!=null ) {
                    if( amount==0f ) processTickerResponse(json, currency, item, cb);
                    else processOrderBookResponse(json, currency, item, amount, cb);
                }
                }
            });
    }

    protected String getUrlSuffix(int requestedData) {
        switch( requestedData ) {
            case TICKER: return getTickerUrlSuffix();
            case ORDER_BOOK: return getOrderBookUrlSuffix();
        }
        return "";
    }
    protected String getUrl(int currency, int item, int requestedData) {
        return getBaseUrl(currency, item) + getUrlSuffix(requestedData);
    }
    protected abstract void processTickerResponse(JsonObject json, int currency, int item, OnTickerDataAvailable cb);
    protected void processOrderBookResponse(JsonObject json, int currency, int item, float amount, OnTickerDataAvailable cb) {
        json = preProcessOrderBookResponse(json);

        JsonArray bids = D30.Json.getArray(json, "bids");
        float buyPrice = bids!=null ? getPrice(bids, amount) : 0f;

        JsonArray asks = D30.Json.getArray(json, "asks");
        float sellPrice = asks!=null ? getPrice(asks, amount) : 0f;

        lastValue = new LastValue(buyPrice, sellPrice, currency, item);
        lastValue.setTimestamp( getTimestamp(json) );
        if( cb!=null ) cb.onTicker( getId(), lastValue );
    }

    // this is used in case when our precious data is nested in some retarded way (Looking at you MtGox...)
    protected JsonObject preProcessOrderBookResponse(JsonObject json) {
        return json;
    }

    private float getPrice(JsonArray prices, float amount) {
        float currentAmount = 0f, sum = 0f;
        for(JsonElement priceItem : prices) {
            float tmpAmount = extractAmount(priceItem);
            if( currentAmount+tmpAmount<amount ) {
                sum += tmpAmount * extractPrice(priceItem);
                currentAmount += tmpAmount;

            } else {
                sum += (amount-currentAmount) * extractPrice(priceItem);
                break;

            }
        }
        return sum / amount;
    }

    protected abstract Float extractPrice(JsonElement e);
    protected abstract Float extractAmount(JsonElement e);
    protected abstract Long getTimestamp(JsonObject json);

    public static String getPriceTypeName(int priceType) {
        switch( priceType ) {
            case PRICE_LAST: return "last";
            case PRICE_BUY:  return "buy";
            case PRICE_SELL: return "sell";
        }
        return null;
    }
    public static String getCurrencyName(int currency) {
        switch( currency ) {
            case USD: return "USD";
            case GBP: return "GBP";
            case JPY: return "JPY";
            case EUR: return "EUR";
            case PLN: return "PLN";
            case NOK: return "NOK";
            case AUD: return "AUD";
            case CAD: return "CAD";
            case SGD: return "SGD";
        }
        return null;
    }
    public static Exchange getExchange(int exchange, Context context) {
        switch( exchange ) {
            case Exchange.MTGOX:    return MtGoxExchange.getInstance(context);
            case Exchange.BITSTAMP: return BitStampExchange.getInstance(context);
            case Exchange.BTCE:     return BtceExchange.getInstance(context);
        }
        return null;
    }
    public static Integer getIcon(int item) {
        switch(item) {
            case Exchange.MTGOX:    return R.drawable.ic_mtgox_blue;
            case Exchange.BITSTAMP: return R.drawable.ic_bitstamp_blue;
            case Exchange.BTCE:     return R.drawable.ic_btce_blue;
        }
        return null;
    }

    public abstract int getId();
    public abstract String getName();
    public abstract String getPrettyName();
    protected abstract String getBaseUrl(int currency, int item);
    protected abstract String getTickerUrlSuffix();
    protected abstract String getOrderBookUrlSuffix();
    public abstract boolean isCurrencySupported(int currency);
    public abstract boolean isItemSupported(int item);


    public interface OnTickerDataAvailable {
        public void onTicker(int source, LastValue lastValue);
    }

    public class LastValue {

        private float lastValue;
        private float buyValue;
        private float sellValue;

        private int currency;
        private int item;
        private boolean orderBook;

        private long ts = 0;
        private float amount = 1;
        private String prettyAmount = "1";

        public LastValue(float buyPrice, float sellPrice, int currency, int item) {
            constructorsCallMe(
                buyPrice + sellPrice/2, // NOTE: lastPrice here is *kinda* fake ;)
                currency,
                item,
                true
            );
            setBuyValue(buyPrice);
            setSellValue(sellPrice);
        }

        public LastValue(float lastValue, int currency, int item) {
            constructorsCallMe(lastValue, currency, item, false);
        }
        public LastValue(String lastValue, int currency, int item) {
            constructorsCallMe(convertToFloat(lastValue), currency, item, false);
        }
        private void constructorsCallMe(float lastValue, int currency, int item, boolean orderBook) {
            this.lastValue = lastValue;
            this.currency = currency;
            this.item = item;
            this.orderBook = orderBook;
        }

        // handle amount
        public void setAmount(String amount) throws Exception {
            this.amount = Float.parseFloat(amount);
            if( this.amount<=0 ) throw new Exception("amount cannot be set to zero or less");

            String[] tmp = amount.split("\\.");
            prettyAmount = tmp[0].replaceFirst("^0+(?!$)", "");
            if( prettyAmount.equals("") ) prettyAmount = "0";
            if( tmp.length>1 ) {
                String decimals = tmp[1].replaceAll("[0]+$", "");
                if(!decimals.equals("")) prettyAmount += "." + decimals;
            }
        }
        public String getPrettyAmount() {
            return prettyAmount;
        }


        public void setTimestamp(long timestamp) {
            this.ts = timestamp;
        }
        public boolean isFresh() {
            return ts!=0 && ts + FRESHNESS > System.currentTimeMillis() / 100;
        }


        public int getCurrency() { return currency; }
        public int getItem() { return item; }
        public boolean isOrderBook() { return orderBook; }

        public void setBuyValue(String buyValue) {
            setBuyValue(convertToFloat(buyValue));
        }
        public void setBuyValue(float buyValue) {
            this.buyValue = buyValue;
        }
        public void setSellValue(String sellValue) {
            setSellValue(convertToFloat(sellValue));
        }
        public void setSellValue(float sellValue) {
            this.sellValue = sellValue;
        }

        // different getters for a value
        public float getFloat(int priceType) {
            float tmp;
            switch( priceType ) {
                case Exchange.PRICE_LAST: tmp = lastValue; break;
                case Exchange.PRICE_SELL: tmp = sellValue; break;
                case Exchange.PRICE_BUY: tmp = buyValue; break;
                default: tmp = 0;
            }
            return amount * tmp;
        }
        public String getCompact(int priceType, int decimalPlaces) {
            float tmp = getFloat(priceType);

            // TODO: this is ugly, and should be prettified
            return getFormattedValue(decimalPlaces==-1
                ? (tmp<10 ? new DecimalFormat("#.##").format(tmp) : "" + Math.round(tmp))
                : new DecimalFormat("#." + new String(new char[decimalPlaces]).replace('\0', '#')).format(tmp),
                true
            );
        }
        public String getCompact(int priceType) { return getCompact(priceType, -1); }
        public String getString(int priceType) {
            return getFormattedValue("" + getFloat(priceType), false);
        }


        // methods used internally
        private float convertToFloat(String strValue) {
            return Float.parseFloat(strValue);
        }
        private String getFormattedValue(String value, boolean compact) {
            switch( currency ) {
                // Prefixed:
                case USD: return "$" + value;
                case GBP: return "£" + value;
                case JPY: return "¥" + value;

                // Suffixed:
                case EUR: return value + "€";
                case PLN: return value + "zł";
                case NOK: return value + "kr";

                // Prefixed w/long form
                case AUD: return (compact ? "A$" : "AU$") + value;
                case CAD: return (compact ? "C$" : "CA$") + value;
                case SGD: return (compact ? "S$" : "SG$") + value;

                default: return null;
            }
        }
    }
}
