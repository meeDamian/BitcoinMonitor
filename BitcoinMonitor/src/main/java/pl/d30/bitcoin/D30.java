package pl.d30.bitcoin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class D30 {

    public static final String LOG = "BtcManager";

    // configuration files
    public static final String PREF_FILE_BTC = "btc";
    public static final String PREF_FILE_LTC = "ltc";


    //configuration indexes
    public static final String IDX_SOURCE = "source";
    public static final String IDX_CURRENCY = "currency";
    public static final String IDX_AMOUNT= "amount";
    public static final String IDX_EXPERIMENTAL = "experimental";


    //values
    public static final int BTC = 0;
    public static final int LTC = 1;

    public static final int MTGOX = 0;
    public static final int BITSTAMP = 1;
    public static final int BTCE = 2;

    public static final String USD = "USD";
    public static final String EUR = "EUR";


    // DEFAULTS
    public static final String DEF_CURRENCY = USD;

    public static class Json {

        public static JsonObject getObject(JsonObject j, String n) {
            JsonElement e = j.get(n);
            return e!=null && e.isJsonObject() ? e.getAsJsonObject() : null;
        }

        public static String getString(JsonObject j, String n) {
            JsonElement e = j.get(n);
            return e!=null && e.isJsonPrimitive() ? e.getAsString() : null;
        }
    }


}
