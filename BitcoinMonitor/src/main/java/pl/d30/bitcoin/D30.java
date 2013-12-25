package pl.d30.bitcoin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class D30 {

    public static final String LOG = "DashCoin";

    // configuration files
    public static final String PREF_FILE_BTC    = "btc";
    public static final String PREF_FILE_LTC    = "ltc";
    public static final String PREF_FILE_NOTIF  = "notif";


    //configuration indexes for monitor
    public static final String IDX_SOURCE       = "source";
    public static final String IDX_CURRENCY     = "currency";
    public static final String IDX_AMOUNT       = "amount";
    public static final String IDX_PRICE_BELOW  = "price_below";
    public static final String IDX_PRICE_ABOVE  = "price_above";
//    public static final String IDX_EXPERIMENTAL = "experimental";

    // configuration indexes for arbitrage
    public static final String IDX_BUY_SRC      = "buy_exchange";
    public static final String IDX_BUY_PRICE    = "buy_price";
    public static final String IDX_SELL_SRC     = "sell_exchange";
    public static final String IDX_SELL_PRICE   = "sell_price";
    public static final String IDX_PRIORITY     = "priority";
    public static final String IDX_ORDER_BOOK   = "order_book";
    public static final String IDX_ORDER_AMOUNT = "order_book_amount";
    public static final String IDX_DIFF_BELOW   = "diff_below";
    public static final String IDX_DIFF_ABOVE   = "diff_above";

    // configuration indexes for notifications
    public static final String IDX_MASTER_SWITCH= "master_enabled";

    // common configurations
    public static final String IDX_DONATE_BTC   = "donateBTC";
    public static final String IDX_DONATE_LTC   = "donateLTC";


    // Json helper class:
    public static class Json {

        public static JsonObject getObject(JsonObject j, String n) {
            JsonElement e = j.get(n);
            return e!=null && e.isJsonObject() ? e.getAsJsonObject() : null;
        }

        public static JsonArray getArray(JsonObject j, String n) {
            JsonElement e = j.get(n);
            return e!=null && e.isJsonArray() ? e.getAsJsonArray() : null;
        }

        public static String getString(JsonObject j, String n) {
            JsonElement e = j.get(n);
            return e!=null && e.isJsonPrimitive() ? e.getAsString() : null;
        }

        public static Float getFloat(JsonObject j, String n) {
            JsonElement e = j.get(n);
            return e!=null && e.isJsonPrimitive() ? e.getAsFloat() : null;
        }

        public static Long getLong(JsonObject j, String n) {
            JsonElement e = j.get(n);
            return e!=null && e.isJsonPrimitive() ? e.getAsLong() : null;
        }

    }


    // App/Device version extractors
    private static String appVersion = null;
    public static String getAppVersion(Context c) {
        if( appVersion!=null ) return appVersion;

        PackageManager pm = c.getPackageManager();
        if( pm!=null ) {
            try {
                PackageInfo pi = pm.getPackageInfo(c.getPackageName(), 0);
                return appVersion = pi.versionName + " (" + pi.versionCode + ")";

            } catch( PackageManager.NameNotFoundException ignored ) {}
        }
        return null;
    }
    public static String getDeviceInfo() {
        return Build.MANUFACTURER + " " + Build.MODEL + "[" + Build.DEVICE + "|" + Build.PRODUCT + "|" + Build.SERIAL + "], OS: " + Build.VERSION.RELEASE;
    }

}
