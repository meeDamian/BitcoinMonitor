package pl.d30.bitcoin.dash.service;

import com.google.analytics.tracking.android.EasyTracker;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.exchange.Exchange;

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
    protected String getIntentAddress() {
        return "http://preev.com/btc/" + Exchange.getCurrencyName(currency).toLowerCase();
    }

}
