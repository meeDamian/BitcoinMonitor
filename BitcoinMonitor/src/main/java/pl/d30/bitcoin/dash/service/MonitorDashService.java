package pl.d30.bitcoin.dash.service;

import android.content.SharedPreferences;

import com.google.android.apps.dashclock.api.DashClockExtension;

import pl.d30.bitcoin.dash.exchange.Exchange;

public abstract class MonitorDashService extends DashClockExtension {

    protected SharedPreferences sp;

    protected int currency = Exchange.USD;
    protected int source = Exchange.MTGOX;
    protected int item = Exchange.BTC;

    protected Exchange exchange;

    protected float a;


}
