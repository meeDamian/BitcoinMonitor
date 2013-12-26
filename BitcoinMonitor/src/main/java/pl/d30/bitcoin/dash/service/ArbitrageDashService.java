package pl.d30.bitcoin.dash.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.text.NumberFormat;
import java.util.Locale;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.exchange.Exchange;

public abstract class ArbitrageDashService extends DashClockExtension {

    protected SharedPreferences sp;

    protected int currency = Exchange.USD;

    protected int buyPrice = Exchange.PRICE_BUY;
    protected int sellPrice = Exchange.PRICE_SELL;

    protected boolean displayPriority = Exchange.PERCENTAGE;

    protected Exchange buyExchange;
    protected Exchange sellExchange;

    protected Exchange.LastValue firstDownload = null;
    Exchange.OnTickerDataAvailable callback = new Exchange.OnTickerDataAvailable() {
        @Override
        public void onTicker(int source, Exchange.LastValue lastValue) {
        if( firstDownload==null ) firstDownload = lastValue;
        else if( buyExchange.getId()==source ) updateWidget( lastValue, firstDownload );
        else updateWidget( firstDownload, lastValue );
        }
    };

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);

        EasyTracker.getInstance().setContext(this);

        sp = getSharedPreferences(getConfFile(), MODE_PRIVATE);
    }

    @Override
    protected void onUpdateData(int reason) {

        int buySource = Integer.parseInt(sp.getString(D30.IDX_BUY_SRC, "" + Exchange.BITSTAMP));
        buyExchange = Exchange.getExchange(buySource, this);

        int sellSource = Integer.parseInt(sp.getString(D30.IDX_SELL_SRC, "" + Exchange.MTGOX));
        sellExchange = Exchange.getExchange(sellSource, this);

        buyPrice = sp.getBoolean(D30.IDX_BUY_PRICE, true) ? Exchange.PRICE_BUY : Exchange.PRICE_LAST;
        sellPrice = sp.getBoolean(D30.IDX_SELL_PRICE, true) ? Exchange.PRICE_SELL : Exchange.PRICE_LAST;

        displayPriority = sp.getBoolean(D30.IDX_PRIORITY, displayPriority);

        float orderBookAmount = 0f;
        boolean useOrderBook = sp.getBoolean(D30.IDX_ORDER_BOOK, false);
        if( useOrderBook ) {
            String amount = sp.getString(D30.IDX_ORDER_AMOUNT, "");
            if( !amount.isEmpty() ) {
                try {
                    orderBookAmount = Float.parseFloat(amount);

                } catch(NumberFormatException e) {
                    orderBookAmount = fixAmount( 1f );

                }

            } else orderBookAmount = fixAmount( 1f );
        }

        buyExchange.getTicker(getCurrency(), getItem(), orderBookAmount, callback);
        sellExchange.getTicker(getCurrency(), getItem(), orderBookAmount, callback);
    }

    protected void updateWidget(Exchange.LastValue buyValue, Exchange.LastValue sellValue) {
        firstDownload = null;

        String percentageDiff = getPercentageDiff(buyValue.getFloat(buyPrice), sellValue.getFloat(sellPrice));
        String currencyDiff = getCurrencyDiff(buyValue.getFloat(buyPrice), sellValue.getFloat(sellPrice));

        String expBody = getString(
            R.string.expanded_body_arbitrage,
            buyExchange.getPrettyName(), Exchange.getPriceTypeName(buyPrice), buyValue.getCompact(buyPrice, 3),
            sellExchange.getPrettyName(), Exchange.getPriceTypeName(sellPrice), sellValue.getCompact(sellPrice, 3)
        );

        publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_arbitrage)
            .status(getStatus(percentageDiff, currencyDiff))
            .expandedTitle(getTitle(percentageDiff, currencyDiff))
            .expandedBody(expBody)
            .clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getIntentAddress()))));
    }

    protected String getStatus(String percentageDiff, String currencyDiff) {
        return displayPriority ? percentageDiff : currencyDiff;
    }
    protected String getTitle(String percentageDiff, String currencyDiff) {
        return getStatus(percentageDiff, currencyDiff) + " = " + getStatus(currencyDiff, percentageDiff);
    }

    protected String getPercentageDiff(float v1, float v2) {
        float value = 100 - (v1 *100f) / v2;
//        if( value>6 ) {
//            Notification.BigPictureStyle textStyle = new Notification.BigPictureStyle();
//            textStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.ic_arbitrage))
//                    .setSummaryText("lolololololo")
//                    .setBigContentTitle("Dsadasdsa");
//
//            Notification.Builder notification = new Notification.Builder(this)
//                .setContentTitle("aaaaaaa")
//                .setContentText("bbbbbbbb")
//                .setSubText("cccccccc")
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_arbitrage))
//                .setSmallIcon(R.drawable.ic_btc)
//                .setStyle(textStyle)
//                .setAutoCancel(true);
//
//            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            nm.notify(0, notification.build());
//        }

        return String.format("%.3f", value) + "%";
    }
    protected String getCurrencyDiff(float v1, float v2) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format( v2-v1 );
    }

    protected float fixAmount(float amount) {
        sp.edit().putString(D30.IDX_ORDER_AMOUNT, Float.toString(amount)).apply();
        return amount;
    }

    // to be implemented by `kids`
    protected abstract int getItem();
    protected abstract int getCurrency();
    protected abstract String getConfFile();
    protected abstract String getIntentAddress();
}

