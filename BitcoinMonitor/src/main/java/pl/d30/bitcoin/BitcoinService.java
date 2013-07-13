package pl.d30.bitcoin;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BitcoinService extends WallpaperService {

    private static final String MTGOX = "0";
    private static final String BITSTAMP = "1";
    private static final int DELAY = 5000;

    private SharedPreferences prefs;
    private Set<String> sources;
    private boolean touchEnabled;
    private int refreshInterval;
    private int countdown = 0;


    private String mtgox_price = "$0";
    private String bitstamp_price = "$0";

    private AsyncTask mtgox_at;
    private AsyncTask bitstamp_at;

    @Override
    public Engine onCreateEngine() {
        return new BitcoinEngine();
    }

    private class BitcoinEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run(){
                draw();
            }
        };
        private Paint paint = new Paint();
        private boolean visible = true;

        public BitcoinEngine() {
            prefs = PreferenceManager.getDefaultSharedPreferences(BitcoinService.this);
            updatePrefs();
            updateData();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if(visible) {
                handler.post(drawRunner);
                updatePrefs();
            } else handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder,format,width,height);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if(touchEnabled && event.getAction()==MotionEvent.ACTION_UP) {
                updateData( true );
                SurfaceHolder holder = getSurfaceHolder();
                Canvas canvas = null;

                try {
                    canvas = holder.lockCanvas();
                    //if( canvas!=null ) canvas.drawColor(Color.BLACK);

                } finally {
                    if( canvas!=null ) holder.unlockCanvasAndPost(canvas);
                }

                handler.removeCallbacks(drawRunner);
                if( visible ) handler.postDelayed(drawRunner, DELAY);
            }
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            Log.d("a", "" + countdown);

            if( countdown>refreshInterval ) countdown = refreshInterval;
            if( (countdown-=DELAY/1000)<=0 ) {
                updateData();
                countdown = refreshInterval;
            }

            try {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.BLACK);

                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setTextSize(90);
                canvas.drawText(bitstamp_price, 190, 850, paint);
                canvas.drawText(mtgox_price, 190, 950, paint);

            } finally {
                holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            if( visible ) handler.postDelayed(drawRunner, 5000);
        }
    }

    private void updatePrefs() {
        touchEnabled = prefs.getBoolean("touch", false);
        sources = prefs.getStringSet("source", new HashSet<String>());
        refreshInterval = Integer.parseInt(prefs.getString("refresh", "600"));
    }

    private void updateData() {
        this.updateData(false);
    }
    private void updateData(boolean force) {

        boolean st = false;

        if( sources.contains(BITSTAMP) ) {
            if( bitstamp_at!=null ) st = (bitstamp_at.getStatus()==AsyncTask.Status.RUNNING || bitstamp_at.getStatus()==AsyncTask.Status.PENDING);
            if( st && force ) bitstamp_at.cancel(true);
            if( !st || force ) bitstamp_at = new DownloadFilesTask().execute("https://www.bitstamp.net/api/ticker/", BITSTAMP);
        }
        if( sources.contains(MTGOX) ) {
            if( mtgox_at!=null ) st = (mtgox_at.getStatus()==AsyncTask.Status.RUNNING || mtgox_at.getStatus()==AsyncTask.Status.PENDING);
            if( st && force ) mtgox_at.cancel(true);
            if( !st || force ) mtgox_at = new DownloadFilesTask().execute("http://data.mtgox.com/api/1/BTCUSD/ticker", MTGOX);
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            String json = "", p = "$0";
            try {
                String line = "";
                HttpGet request = new HttpGet(params[0]);
                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line = rd.readLine()) != null) json += line + System.getProperty("line.separator");

                if(params[1].equals(MTGOX)) mtgox_price = p = (new JSONObject(json)).getJSONObject("return").getJSONObject("last").getString("display_short");
                else if(params[1].equals(BITSTAMP)) bitstamp_price = p = "$"+(new JSONObject(json)).getString("last");

            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();

            } catch (IOException e2) {
                e2.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return p;
        }

        protected void onProgressUpdate(Void... progress) {}

        protected void onPostExecute(String result) {}
    }
}
