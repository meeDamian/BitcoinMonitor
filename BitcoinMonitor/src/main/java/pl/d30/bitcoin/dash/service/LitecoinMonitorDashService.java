package pl.d30.bitcoin.dash.service;

import com.google.analytics.tracking.android.EasyTracker;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class LitecoinMonitorDashService extends BitcoinMonitorDashService {

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);

        EasyTracker.getInstance().setContext(this);

        sp = getSharedPreferences(D30.PREF_FILE_LTC, MODE_PRIVATE);
        item = Exchange.LTC;
    }

    @Override
    protected String getIntentAddress() {
        return "http://www.litecoinrates.com";
    }

    @Override
    protected void fixSource() {
        sp.edit().putString(D30.IDX_SOURCE, Integer.toString(source = Exchange.BTCE)).apply();
    }
}
