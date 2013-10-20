package pl.d30.bitcoin.dash.service;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import pl.d30.bitcoin.D30;

public class CoinDashService extends DashClockExtension {

    protected SharedPreferences sp;

    protected String currency = D30.DEF_CURRENCY;
    protected int source = D30.SOURCE_MTGOX;
    protected float amount;

    protected int tries = 0;

    private String cachedVersionString = null;

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);

        sp = getSharedPreferences(D30.PREF_FILE_BTC, MODE_PRIVATE);

        getAppVersion();
    }

    @Override
    protected void onUpdateData(int reason) {

        source = Integer.parseInt(sp.getString(D30.IDX_SOURCE, "" + source));
        currency = sp.getString(D30.IDX_CURRENCY, currency);

        Ion.with(getApplicationContext(), getUrl())
            .setHeader("User-Agent", "DashClock Bitcoin Monitor " + getCachedVersion() + ", " + getDeviceInfo())
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject json) {
                if( e!=null) Log.w(D30.LOG, e.toString());
                if( json!=null) updateWidget( getLastValue(json) );
                }
            });
    }

    protected String getUrl() {
        switch( source ) {
            case D30.SOURCE_BTCE: return "https://btc-e.com/api/2/btc_" + currency + "/ticker";
            case D30.SOURCE_BITSTAMP: return "https://www.bitstamp.net/api/ticker/";
            default:
            case D30.SOURCE_MTGOX: return "https://data.mtgox.com/api/1/BTC" + currency + "/ticker";
        }
    }

    protected String getCachedVersion() {
        if( cachedVersionString==null ) cachedVersionString = getAppVersion();
        return cachedVersionString;
    }

    protected String getAppVersion() {
        PackageManager pm = getPackageManager();
        if( pm!=null ) {
            try {
                PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
                return pi.versionName + " (" + pi.versionCode + ")";

            } catch( PackageManager.NameNotFoundException ignored ) {}
        }
        return null;
    }

    protected String getLastValue(JsonObject j) {
        switch( source ) {
            case D30.SOURCE_MTGOX: return getFromMtgox(j);
            case D30.SOURCE_BITSTAMP: return getFromBitstamp(j);
            case D30.SOURCE_BTCE: return getFromBtce(j);
            default: return null;
        }
    }

    protected String getFormattedValue(String value) {
        // TODO: change to X.XX format
        return "";
    }
    protected String getFormattedValue(String value, String amount) {
        // TODO: multiply value by amount
        return getFormattedValue( value + amount );
    }

    protected String getPrintableValue(String v, boolean compact) {

        // Prefixed:
        if( currency.equals("USD") ) return "$" + v;
        else if( currency.equals("GBP") ) return "£" + v;
        else if( currency.equals("JPY") ) return "¥" + v;

        // Suffixed:
        else if( currency.equals("EUR") ) return v + "€";
        else if( currency.equals("PLN") ) return v + "zł";
        else if( currency.equals("NOK") ) return v + "kr";

        // Prefixed w/long form
        else if( currency.equals("AUD") ) return (compact ? "A$" : "AU$") + v;
        else if( currency.equals("CAD") ) return (compact ? "C$" : "CA$") + v;
        else if( currency.equals("SGD") ) return (compact ? "S$" : "SG$") + v;

        else return null;
    }

    protected void updateWidget(String newValue) {

        String a = sp.getString(D30.IDX_AMOUNT, "");
        if( !a.isEmpty() ) {
            try {
                amount = Float.parseFloat( a );

            } catch(NumberFormatException e) {
                fixAmount();
            }

        } else fixAmount();

        if( amount!=1.0f ) {

        }



    }

    protected void fixAmount() {
        sp.edit().putString(D30.IDX_AMOUNT, Float.toString(amount = 1f)).apply();
    }





    // STATIC
    protected static String getFromMtgox(JsonObject j) {
        j = D30.Json.getObject(j, "return");
        if( j!=null ) {
            j = D30.Json.getObject(j, "last");
            if( j!=null ) return D30.Json.getString(j, "value");
        }
        return null;
    }
    protected static String getFromBitstamp(JsonObject j) {
        return D30.Json.getString(j, "last");
    }
    protected static String getFromBtce(JsonObject j) {
        j = D30.Json.getObject(j, "ticker");
        return j!=null ? D30.Json.getString(j, "last") : null;
    }

    protected static String getSourceName(int source, boolean pretty) {
        switch( source ) {
            default:
            case D30.SOURCE_MTGOX: return pretty ? "Mt.Gox" : "mtgox";
            case D30.SOURCE_BTCE: return pretty ? "BTC-e" : "btce";
            case D30.SOURCE_BITSTAMP: return pretty? "Bitstamp" : "bitstamp";
        }
    }
    protected static String getSourceName(int source) { return getSourceName(source, false); }

    protected static String getDeviceInfo() {
        return Build.MANUFACTURER + " " + Build.MODEL + "[" + Build.DEVICE + "|" + Build.PRODUCT + "|" + Build.SERIAL + "], OS: " + Build.VERSION.RELEASE;
    }
}
