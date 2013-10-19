package pl.d30.bitcoin.dash.service;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import pl.d30.bitcoin.R;

public class VirtualCoinDashService extends DashClockExtension {

    public static final int ITEM_BTC = 0;
    public static final int ITEM_LTC = 1;

    public static final int SOURCE_MTGOX = 0;
    public static final int SOURCE_BITSTAMP = 1;
    public static final int SOURCE_BTCE = 2;

    public static final String DEF_CURRENCY = "USD";
    public static final String DEF_AMOUNT = "1.0";
    public static final int DEF_ITEM = ITEM_BTC;
    public static final int DEF_SOURCE = SOURCE_MTGOX;

    public static final String LOG_TAG = "BTC_APP";
    private static final String FAIL = "FAIL";

    protected SharedPreferences sp;

    protected String currency = DEF_CURRENCY;
    protected int source = SOURCE_MTGOX;
    protected int item = DEF_ITEM;
    protected boolean experimental = false;

    private int tries = 0;

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int reason) {

        if( reason==UPDATE_REASON_SETTINGS_CHANGED || reason==UPDATE_REASON_INITIAL ) validateSettings();

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        source = Integer.parseInt(sp.getString("source", "" + source));
        experimental = sp.getBoolean("experimental", experimental);
        currency = sp.getString("currency", currency);

        // Children do stuff here

    }

    protected void setItem(int newItem) {
        item = newItem;
    }

    private boolean isLtc() {
        return item==ITEM_LTC;
    }

    protected String getSourceName(int source) {
        switch( source ) {
            case SOURCE_BITSTAMP: return "bitstamp";
            case SOURCE_BTCE: return "btce";
            default:
            case SOURCE_MTGOX: return "mtgox";
        }
    }

    protected String getSourcePrettyName(int source) {
        switch( source ) {
            default:
            case SOURCE_MTGOX: return "Mt.Gox";
            case SOURCE_BTCE: return "BTC-e";
            case SOURCE_BITSTAMP: return  "Bitstamp";
        }
    }

    protected class DownloadFilesTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 7000);
            HttpClient client = new DefaultHttpClient(httpParams);
            String json = "", p = "0";
            try {
                String line;
                HttpGet request = new HttpGet(params[0]);
                request.getParams().setParameter(CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));

                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while((line = rd.readLine()) != null) json += line + System.getProperty("line.separator");

                p = parseResponse(json);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                p = FAIL;

            } catch (IOException e) {
                e.printStackTrace();
                p = FAIL;

            } catch (JSONException e) {
                e.printStackTrace();
                p = FAIL;
            }
            return p;
        }

        protected void onProgressUpdate(Void... progress) {}

        @SuppressLint("DefaultLocale")
        protected void onPostExecute(String result) {

            if ( !result.equals(FAIL) && !result.trim().equals("") ) {
                String pre_e = null, pre = "", suf = "", src = "";

                if( sp==null ) return;

                String amount = sp.getString("amount", DEF_AMOUNT);
                float a;
                try {
                    a = Float.parseFloat(amount);
                } catch(NumberFormatException e) {
                    a = 1f;
                    sp.edit().putString("amount", Float.toString(a)).apply();
                }
                if( a!=1.0f ) {
                    result = String.format("%.6f", a * Float.parseFloat(result));

                    String tmp[] = amount.split("\\.");
                    amount = tmp[0].replaceFirst("^0+(?!$)", "");
                    if(amount.equals("")) amount = "0";
                    if(tmp.length>1) {
                        String decimals = tmp[1].replaceAll("[0]+$", "");
                        if(!decimals.equals("")) amount += "." + decimals;
                    }

                } else amount = "1";

                src = getSourcePrettyName(source);

                if(currency.equals("USD")) pre="$";
                else if(currency.equals("EUR")) suf="€";
                else if(currency.equals("CAD")) {pre="C$"; pre_e="CA$";}
                else if(currency.equals("AUD")) {pre="A$"; pre_e="AU$";}
                else if(currency.equals("GBP")) pre="£";
                else if(currency.equals("PLN")) suf="zł";
                else if(currency.equals("JPY")) pre="¥";
                else if(currency.equals("NOK")) suf="kr";
                else if(currency.equals("SGD")) {pre="S$"; pre_e="SG$";}

                SharedPreferences.Editor spe = sp.edit();

                float val = Float.parseFloat(result);
                String val_str;
                if( val<10 ) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    val_str = df.format(val);

                } else val_str = "" + Math.round(Float.parseFloat(result));

                String status = pre + val_str + suf;
                spe.putString("status", status);

                String expTitle = ((pre_e!=null)?pre_e:pre)+result+suf;
                spe.putString("expTitle", expTitle);

                spe.putLong("epoch", System.currentTimeMillis()/1000);

                spe.apply();

                publishUpdate(new ExtensionData()
                        .visible(true)
                        .icon( R.drawable.icon_small )
                        .status(status)
                        .expandedTitle(expTitle)
                        .expandedBody("Current value of "+amount+"BTC (" + src + ")")
                        .clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://preev.com/btc/" + currency.toLowerCase()))));

                tries = 0;
                Log.d(LOG_TAG, "full update.");

            } else if( ++tries<3 ) {
                Log.d(LOG_TAG, "retrying...");
                onUpdateData(0);

            } else {
                Log.d(LOG_TAG, "partial update.");

                String src = "";
                if(source==SOURCE_MTGOX) src = " (Mt.Gox)";
                else if(source==SOURCE_BITSTAMP) src = " (Bitstamp)";
                else if(source==SOURCE_BTCE) src = " (BTC-e)";

                String amount = sp.getString("amount", DEF_AMOUNT);
                if( amount!=DEF_AMOUNT ) {
                    String tmp[] = amount.split("\\.");
                    amount = tmp[0].replaceFirst("^0+(?!$)", "");
                    if(amount.equals("")) amount = "0";
                    if(tmp.length>1) {
                        String decimals = tmp[1].replaceAll("[0]+$", "");
                        if(!decimals.equals("")) amount += "." + decimals;
                    }

                } else amount = "1";

                long lastRead = sp.getLong("epoch", 0);
                String expandedBody = "";
                boolean visible = true;
                if( lastRead!=0 ) {
                    String ago;
                    long diff = System.currentTimeMillis()/1000 - lastRead;
                    if( diff<60 ) ago = diff + "s";
                    else if( diff<=60*60 ) ago = "~" + Math.round( diff/60 ) + "m";
                    else if( diff<=60*60*24 ) ago = "~" + Math.round( diff/(60*60) ) + "h";
                    else {
                        visible = false;
                        ago = "ages";
                    }

                    expandedBody =  ago + " ago " + amount + "BTC was worth" + src;
                }

                publishUpdate(new ExtensionData()
                        .visible( visible )
                        .icon( R.drawable.icon_small )
                        .status("---")
                        .expandedTitle(sp.getString("expTitle", "---"))
                        .expandedBody(expandedBody)
                        .clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://preev.com/btc/" + currency.toLowerCase()))));

                tries = 0;
            }
        }
    }

    private String parseResponse(String json) throws JSONException {
        if( !experimental ) {
            JSONObject j = new JSONObject(json);
            switch( source ) {
                default:
                case SOURCE_MTGOX: return j.getJSONObject("return").getJSONObject("last").getString("value");
                case SOURCE_BITSTAMP: return j.getString("last");
                case SOURCE_BTCE: return j.getJSONObject("ticker").getString("last");
            }

        } else {
            String[] data = json.split(",");
            if( !currency.equals(data[3].trim()) ) Log.e(LOG_TAG, "Invalid currency! {requested:'" + currency + "' , received:'" + data[3].trim() + "'}");
            return (data.length>2) ? data[1].trim() : FAIL;
        }
    }

    protected void validateSettings() {

    }

}
