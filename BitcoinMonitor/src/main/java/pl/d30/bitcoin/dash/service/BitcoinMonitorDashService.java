package pl.d30.bitcoin.dash.service;

import android.content.Intent;
import android.content.pm.PackageManager;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.cryptocoin.Coin;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class BitcoinMonitorDashService extends MonitorDashService {

    @Override
    protected int getItem() {
        return Coin.BTC;
    }

    @Override
    protected String getConfFile() {
        return D30.PREF_FILE_BTC;
    }

    @Override
    protected Intent getClickIntent() {
        PackageManager pm = getPackageManager();
        if( pm!=null ) {
            Intent i = pm.getLaunchIntentForPackage("com.phlint.android.zeroblock");
            if( i!=null ) return i;
        }
        return super.getClickIntent();
    }

    @Override
    protected String getIntentAddress() {
        return "http://preev.com/btc/" + Exchange.getCurrencyName(currency).toLowerCase();
    }

}
