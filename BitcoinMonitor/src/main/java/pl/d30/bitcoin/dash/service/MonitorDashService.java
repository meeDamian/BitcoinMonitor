package pl.d30.bitcoin.dash.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.cryptocoin.Coin;
import pl.d30.bitcoin.dash.exchange.Exchange;

public abstract class MonitorDashService extends DashClockExtension {

    protected SharedPreferences sp;

    protected int currency = Exchange.USD;
    protected int source = Exchange.MTGOX;

    protected Exchange exchange;

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        setUpdateWhenScreenOn(true);

        EasyTracker.getInstance().setContext(this);

        sp = getSharedPreferences(getConfFile(), MODE_PRIVATE);
    }

    @Override
    protected void onUpdateData(int reason) {

        source = Integer.parseInt(sp.getString(D30.IDX_SOURCE, "" + source));
        exchange = Exchange.getExchange(source, this);
        if( !exchange.isItemSupported(getItem()) ) fixSource();

        try {
            currency = Integer.parseInt(sp.getString(D30.IDX_CURRENCY, "" + currency));
            if( !exchange.isCurrencySupported(currency) ) fixCurrency();

        } catch(NumberFormatException e) { fixCurrency(); }

        exchange.getTicker(currency, getItem(), new Exchange.OnTickerDataAvailable() {
            @Override
            public void onTicker(int source, Exchange.LastValue lastValue) {
            if( !updateWidget(lastValue, Exchange.PRICE_LAST) ) handleError();
            }
        });
    }

    protected boolean updateWidget(Exchange.LastValue lastValue, int priceType) {

        String amount = sp.getString(D30.IDX_AMOUNT, "");
        if( !amount.isEmpty() ) {
            try {
                lastValue.setAmount(amount);

            } catch(Exception e) {
                fixAmount();

            }
        }

        publishUpdate( lastValue, priceType );

        logEntry( lastValue );

        return true;
    }

    protected void publishUpdate(Exchange.LastValue value, int priceType) {
        publishUpdate(new ExtensionData()
            .visible(true)
            .icon(Coin.getDrawable(getItem()))
            .status(value.getCompact(priceType))
            .expandedTitle(value.getString(priceType) + " (" + Exchange.getPriceTypeName(priceType) + ")")
            .expandedBody(getString(R.string.expanded_body_monitor, value.getPrettyAmount(), Coin.getName(getItem()), exchange.getPrettyName()))
            .clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getIntentAddress()))));
    }
    protected boolean hideUpdate() { // TODO: or show info about outdated data(?)
        publishUpdate(new ExtensionData().visible(false));
        return false;
    }

    protected void logEntry(Exchange.LastValue value) {
        sp.edit()
            .putFloat("prevLastValue", value.getFloat(Exchange.PRICE_LAST))
            .putFloat("prevSellValue", value.getFloat(Exchange.PRICE_SELL))
            .putFloat("prevBuyValue", value.getFloat(Exchange.PRICE_BUY))
            .putLong("prevEpoch", System.currentTimeMillis() / 1000)
            .putInt("prevSource", source)
            .putString("prevCurrency", Exchange.getCurrencyName(currency))
            .apply();
    }

    protected void handleError() { }

    protected abstract int getItem();
    protected abstract String getConfFile();
    protected abstract String getIntentAddress();

    protected void fixSource() {
        sp.edit().putString(D30.IDX_SOURCE, Integer.toString(source = Exchange.MTGOX)).apply();
    }
    protected void fixCurrency() {
        sp.edit().putString(D30.IDX_CURRENCY, Exchange.getCurrencyName(currency = Exchange.USD)).apply();
    }
    protected void fixAmount() {
        sp.edit().putString(D30.IDX_AMOUNT, Float.toString(1f)).apply();
    }
}
