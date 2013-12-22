package pl.d30.bitcoin.dash.service;

import android.content.SharedPreferences;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.apps.dashclock.api.DashClockExtension;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.dash.exchange.Exchange;

public abstract class ArbitrageDashService extends DashClockExtension {

    protected SharedPreferences sp;

    protected int currency = Exchange.USD;

    protected int buyPrice = Exchange.PRICE_BUY;
    protected int sellPrice = Exchange.PRICE_SELL;

    protected boolean displayPriority = Exchange.PERCENTAGE;

    protected Exchange buyExchange;
    protected Exchange sellExchange;

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);

        EasyTracker.getInstance().setContext(this);

        sp = getSharedPreferences(getConfFile(), MODE_PRIVATE);
    }

    @Override
    protected void onUpdateData(int reason) {

        int buySource = getIntFromStringPreference(D30.IDX_BUY_SRC, Exchange.BITSTAMP);
        buyExchange = Exchange.getExchange(buySource, this);

        int sellSource = getIntFromStringPreference(D30.IDX_SELL_SRC, Exchange.MTGOX);
        sellExchange = Exchange.getExchange(sellSource, this);

        buyPrice = getIntFromStringPreference(D30.IDX_BUY_PRICE, buyPrice);
        sellPrice = getIntFromStringPreference(D30.IDX_SELL_PRICE, sellPrice);

        displayPriority = sp.getBoolean(D30.IDX_PRIORITY, displayPriority);

    }


    // helpers:
    private int getIntFromStringPreference(String index, int defValue) {
        return Integer.parseInt( sp.getString(index, "" + defValue) );
    }


    // to be implemented by `kids`
    protected abstract int getItem();
    protected abstract String getConfFile();
    protected abstract String getIntendAddress();
}
