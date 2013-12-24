package pl.d30.bitcoin.dash.service;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.cryptocoin.Coin;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class LitecoinMonitorDashService extends MonitorDashService {

    @Override
    protected int getItem() {
        return Coin.LTC;
    }

    @Override
    protected String getConfFile() {
        return D30.PREF_FILE_LTC;
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
