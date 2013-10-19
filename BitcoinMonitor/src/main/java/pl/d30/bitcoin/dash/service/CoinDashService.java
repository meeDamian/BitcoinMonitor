package pl.d30.bitcoin.dash.service;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    protected int tries = 0;

    private String versionName = null;
    private int versionCode = -1;

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

        // TODO: get url
        String url = "https://btc-e.com/api/2/ltc_eur/ticker";

        Ion.with(getApplicationContext(), url)
            .setHeader("User-Agent", "DashClock Bitcoin Monitor " + getAppVersion())
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject json) {
                    if( e!=null) Log.d(D30.LOG, e.toString());
                    if( json!=null) Log.d(D30.LOG, json.toString());
                }
            });


    }

    protected String getBtcUrl() {
        switch( source ) {
            case D30.SOURCE_BTCE: return "https://btc-e.com/api/2/btc_" + currency + "/ticker";
            case D30.SOURCE_BITSTAMP: return "https://www.bitstamp.net/api/ticker/";
            default:
            case D30.SOURCE_MTGOX: return "https://data.mtgox.com/api/1/BTC" + currency + "/ticker";
        }
    }

    protected String getSourceName(int source, boolean pretty) {
        switch( source ) {
            default:
            case D30.SOURCE_MTGOX: return pretty ? "Mt.Gox" : "mtgox";
            case D30.SOURCE_BTCE: return pretty ? "BTC-e" : "btce";
            case D30.SOURCE_BITSTAMP: return pretty? "Bitstamp" : "bitstamp";
        }
    }
    protected String getSourceName(int source) { return getSourceName(source, false); }

    protected String getAppVersion() {
        if( versionName==null || versionCode==-1 ) {
            PackageManager pm = getPackageManager();
            if( pm!=null ) {
                try {
                    PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
                    versionName = pi.versionName;
                    versionCode = pi.versionCode;

                } catch(PackageManager.NameNotFoundException e) {
                    versionName = "unknown";
                    versionCode = -1;
                }
            }
        }
        return versionName + " (" + versionCode + ")";
    }
}
