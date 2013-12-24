package pl.d30.bitcoin.dash.service;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.cryptocoin.Coin;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class BitcoinArbitrageDashService extends ArbitrageDashService {

    @Override
    protected int getItem() {
        return Coin.BTC;
    }

    @Override
    protected int getCurrency() {
        return Exchange.USD;
    }

    @Override
    protected String getConfFile() {
        return D30.PREF_FILE_BTC;
    }

    @Override
    protected String getIntentAddress() {
        return "https://cointhink.com/arbitrage/btcusd";
    }
}
