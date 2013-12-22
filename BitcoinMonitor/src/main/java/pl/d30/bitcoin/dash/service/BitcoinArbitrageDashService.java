package pl.d30.bitcoin.dash.service;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class BitcoinArbitrageDashService extends ArbitrageDashService {

    @Override
    protected int getItem() {
        return Exchange.BTC;
    }

    @Override
    protected String getConfFile() {
        return D30.PREF_FILE_BTC;
    }

    @Override
    protected String getIntendAddress() {
        return "https://cointhink.com/arbitrage/btcusd";
    }
}
