package pl.d30.bitcoin.dash.exchange;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.DecimalFormat;

import pl.d30.bitcoin.D30;

public abstract class Exchange {

    // virtual currencies:
    public static final int BTC = 0;
    public static final int LTC = 1;

    // exchanges:
    public static final int MTGOX = 0;
    public static final int BITSTAMP = 1;
    public static final int BTCE = 2;

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


    public static final int PRICE_LAST = 0;
    public static final int PRICE_BUY = 1;
    public static final int PRICE_SELL = 2;


    protected Context context;
    protected LastValue lastValue;

    public Exchange(Context context) {
        this.context = context;
    }

    public void getTicker(int currency, int priceType, int item, OnTickerDataAvailable cb) {
        downloadResponse( currency, priceType, item,  cb);
    }

    protected void downloadResponse(final int currency, final int priceType, int item, final OnTickerDataAvailable cb) {
        Ion.with(context, getUrl(currency, item))
            .setHeader("User-Agent", "DashClock Bitcoin Monitor " + D30.getAppVersion(context) + ", " + D30.getDeviceInfo())
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject json) {
                    if( e!=null ) Log.w(D30.LOG, e.toString());
                    if( json!=null ) processResponse(json, currency, priceType, cb);
                }
            });
    }

    protected abstract void processResponse(JsonObject json, int currency, int priceType, OnTickerDataAvailable cb);
    protected abstract String getUrl(int currency, int item);

    public LastValue getLastValue() {
        return lastValue;
    }

    protected static String getPriceTypeName(int priceType) {
        switch( priceType ) {
            case PRICE_LAST: return "last";
            case PRICE_BUY: return "buy";
            case PRICE_SELL: return "sell";
        }
        return null;
    }
    public static String getItemName(int item) {
        switch( item ) {
            case BTC: return "BTC";
            case LTC: return "LTC";
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
    public abstract String getName();
    public abstract String getPrettyName();
    public abstract boolean isCurrencySupported(int currency);
    public abstract boolean isItemSupported(int item);


    public interface OnTickerDataAvailable {
        public void onTicker(LastValue lastValue, JsonObject rawResponse);
    }

    public class LastValue {

        private float numericValue;

        private int currency;

        private long ts;
        private float amount = 1;
        private String prettyAmount = "1";

        public LastValue(float value, int currency) {
            this.currency = currency;
            numericValue = value;
        }
        public LastValue(String value, int currency) {
            this.currency = currency;
            numericValue = convertToFloat(value);
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
        public float getAmount() {
            return amount;
        }
        public String getPrettyAmount() {
            return prettyAmount;
        }


        public void setTimestamp(long timestamp) {
            this.ts = timestamp;
        }
        public long getTimestamp() { return ts; }

        // different getters for a value
        public float getFloat() {
            return amount * numericValue;
        }
        public String getCompact() {
            float tmp = getFloat();

            return getFormattedValue(
                tmp<10 ? new DecimalFormat("#.##").format(tmp) : "" + Math.round(tmp),
                true
            );
        }
        public String getString() {
            return getFormattedValue("" + getFloat(), false);
        }


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
