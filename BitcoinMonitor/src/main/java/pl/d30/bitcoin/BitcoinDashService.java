package pl.d30.bitcoin;

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
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BitcoinDashService extends DashClockExtension {
    protected static final int MTGOX = 0;
    protected static final int BITSTAMP = 1;
    protected static final int BTCE = 2;
    protected static final String DEF_CURRENCY = "USD";
    protected static final String DEF_AMOUNT = "1.0";

    private static final String LOG_TAG = "BTC_APP";

    private SharedPreferences sp;

    private String currency = DEF_CURRENCY;
    private int source = MTGOX;
    private boolean experimental = false;

    private int tries = 0;

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int arg0) {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        source = Integer.parseInt(sp.getString("source", ""+source));
        experimental = sp.getBoolean("experimental", experimental);

        if(!experimental) {
            if(source==MTGOX) {
                currency = sp.getString("currency", currency);
                new DownloadFilesTask().execute("https://data.mtgox.com/api/1/BTC" + currency + "/ticker");

            } else if(source==BTCE) new DownloadFilesTask().execute("https://btc-e.com/api/2/btc_usd/ticker");
            else if(source==BITSTAMP) new DownloadFilesTask().execute("https://www.bitstamp.net/api/ticker/");


        } else {
            String s = (source==BITSTAMP) ? "bitstamp" : "mtgox";
            currency = sp.getString("currency", currency);
            new DownloadFilesTask().execute("http://bitcoin.30d.pl/api/v3?s="+s+"&c="+currency);
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            String json = "", p = "0";
            try {
                String line;
                HttpGet request = new HttpGet(params[0]);
                request.getParams().setParameter(CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while((line = rd.readLine()) != null) json += line + System.getProperty("line.separator");

                if(!experimental) {
                    if(source==MTGOX) p = (new JSONObject(json)).getJSONObject("return").getJSONObject("last").getString("value");
                    else if(source==BITSTAMP) p = (new JSONObject(json)).getString("last");
                    else if(source==BTCE) p = (new JSONObject(json)).getJSONObject("ticker").getString("last");

                } else {
                    String[] data = json.split(",");
                    if( !currency.equals(data[3].trim()) ) Log.e("pl.d30.bitcoin", "Invalid currency! {requested:'" + currency + "' , received:'"+data[3].trim()+"'}");
                    p = (data.length>=3) ? data[1].trim() : "FAIL";
                }

            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
                p = "FAIL";

            } catch (IOException e2) {
                e2.printStackTrace();
                p = "FAIL";

            } catch (JSONException e) {
                e.printStackTrace();
                p = "FAIL";
            }
            return p;
        }

        protected void onProgressUpdate(Void... progress) {}

        @SuppressLint("DefaultLocale")
        protected void onPostExecute(String result) {

            if ( !result.equals("FAIL") && !result.trim().equals("") ) {
                String pre_e = null, pre = "", suf = "", src = "";

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

                if(source==MTGOX) src = "Mt.Gox";
                else if(source==BITSTAMP) src = "Bitstamp";
                else if(source==BTCE) src = "BTC-e";

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

                String status = pre+Math.round(Float.parseFloat(result))+suf;
                spe.putString("status", status);

                String expTitle = ((pre_e!=null)?pre_e:pre)+result+suf;
                spe.putString("expTitle", expTitle);

                spe.putLong("epoch", System.currentTimeMillis()/1000);

                spe.apply();

                publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.icon_small)
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
                if(source==MTGOX) src = " (Mt.Gox)";
                else if(source==BITSTAMP) src = " (Bitstamp)";
                else if(source==BTCE) src = " (BTC-e)";

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
}
