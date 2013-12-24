package pl.d30.bitcoin.dash.service;

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
    protected String getIntentAddress() {
        return "http://preev.com/btc/" + Exchange.getCurrencyName(currency).toLowerCase();
    }

}
