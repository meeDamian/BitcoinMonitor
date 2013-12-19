package pl.d30.bitcoin.dash.service;

import android.content.Intent;
import android.net.Uri;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.apps.dashclock.api.ExtensionData;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;

public class LitecoinDashService extends BitcoinDashService {

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);

        EasyTracker.getInstance().setContext(this);

        sp = getSharedPreferences(D30.PREF_FILE_LTC, MODE_PRIVATE);

        getAppVersion();
    }

    @Override
    protected void onUpdateData(int reason) {
        super.onUpdateData(reason);
    }


    @Override
    protected void validateSource() {
        if( source!=D30.BTCE ) fixSource();
    }
    @Override
    protected void validateCurrency() {
        if( !currency.equals(D30.USD) && !currency.equals(D30.EUR) ) fixCurrency();
    }

    @Override
    protected void fixSource() {
        sp.edit().putString(D30.IDX_SOURCE, Integer.toString(source = D30.BTCE)).apply();
    }

    @Override
    protected String getUrl() {
        switch( source ) {
            default:
            case D30.BTCE: return "https://btc-e.com/api/2/ltc_" + currency.toLowerCase() + "/ticker";
        }
    }

    @Override
    protected void publishUpdate(String status, String expTitle, String amount) {
        publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.icon_small_ltc)
            .status(status)
            .expandedTitle(expTitle)
            .expandedBody(getString(R.string.expanded_body, amount, "LTC", getSourceName(source)))
            .clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.litecoinrates.com"))));
    }
}
