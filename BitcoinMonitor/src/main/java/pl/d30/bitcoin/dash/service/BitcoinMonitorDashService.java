package pl.d30.bitcoin.dash.service;

import android.content.Intent;
import android.net.Uri;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.google.gson.JsonObject;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.exchange.BitStampExchange;
import pl.d30.bitcoin.dash.exchange.BtceExchange;
import pl.d30.bitcoin.dash.exchange.Exchange;
import pl.d30.bitcoin.dash.exchange.MtGoxExchange;

public class BitcoinMonitorDashService extends MonitorDashService {

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);

        EasyTracker.getInstance().setContext(this);

        sp = getSharedPreferences(D30.PREF_FILE_BTC, MODE_PRIVATE);
        item = Exchange.BTC;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onUpdateData(int reason) {

        source = Integer.parseInt(sp.getString(D30.IDX_SOURCE, "" + source));
        switch( source ) {
            case Exchange.MTGOX:
                exchange = MtGoxExchange.getInstance(this);
                break;

            case Exchange.BITSTAMP:
                exchange = BitStampExchange.getInstance(this);
                break;

            case Exchange.BTCE:
                exchange = BtceExchange.getInstance(this);
                break;
        }
        if( !exchange.isItemSupported(item) ) fixSource();


        currency = Integer.parseInt(sp.getString(D30.IDX_CURRENCY, "" + currency));
        if( !exchange.isCurrencySupported(currency) ) fixCurrency();


        exchange.getTicker(currency, Exchange.PRICE_LAST, item, new Exchange.OnTickerDataAvailable() {
            @Override
            public void onTicker(Exchange.LastValue lastValue, JsonObject rawResponse) {
            if( !updateWidget(lastValue) ) handleError();
            }
        });
    }

    protected boolean updateWidget(Exchange.LastValue lastValue) {

        String amount = sp.getString(D30.IDX_AMOUNT, "");
        if( !amount.isEmpty() ) {
            try {
                lastValue.setAmount(amount);

            } catch(Exception e) {
                fixAmount();

            }
        }

        // TODO: if( lastValue.getTimestamp() older than 15 minutes ) hideUpdate()

        publishUpdate(lastValue);

        logEntry( lastValue.getFloat() );

        return true;
    }

    protected void publishUpdate(Exchange.LastValue value) {
        publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_btc)
            .status(value.getCompact())
            .expandedTitle(value.getString())
            .expandedBody(getString(R.string.expanded_body, value.getPrettyAmount(), Exchange.getItemName(item), exchange.getPrettyName()))
            .clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://preev.com/btc/" + Exchange.getCurrencyName(currency).toLowerCase()))));
    }

    protected boolean hideUpdate() { // TODO: or show info about outdated data
        publishUpdate(new ExtensionData().visible(false));
        return false;
    }

    protected void handleError() {

    }

    protected void logEntry(float value) {
        sp.edit()
            .putFloat("prevValue", value)
            .putLong("prevEpoch", System.currentTimeMillis() / 1000)
            .putInt("prevSource", source)
            .putString("prevCurrency", Exchange.getCurrencyName(currency))
            .apply();
    }

    protected void fixSource() {
        sp.edit().putString(D30.IDX_SOURCE, Integer.toString(source = Exchange.MTGOX)).apply();
    }
    protected void fixCurrency() {
        sp.edit().putString(D30.IDX_CURRENCY, Exchange.getCurrencyName(currency = Exchange.USD)).apply();
    }
    protected void fixAmount() {
        sp.edit().putString(D30.IDX_AMOUNT, Float.toString(a = 1f)).apply();
    }

}
