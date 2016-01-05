package pl.d30.bitcoin.dash.cryptocoin;

import android.content.Intent;
import android.net.Uri;

public abstract class Coin {

    public static final String STORE_PREFIX = "http://play.google.com/store/apps/details?id=";

    public static final int BTC = 0;
    public static final int LTC = 1;

    public static Integer getCoinIdByName(String name) {
        if( name.toUpperCase().equals(Btc.getName()) ) return BTC;
        else if( name.toUpperCase().equals(Ltc.getName()) ) return LTC;
        else return null;
    }

    public static String getName(int item) {
        switch( item ) {
            case BTC: return Btc.getName();
            case LTC: return Ltc.getName();
        }
        return null;
    }
    public static Integer getDrawable(int item) {
        switch( item ) {
            case BTC: return Btc.getDrawable();
            case LTC: return Ltc.getDrawable();
        }
        return null;
    }
    public static Integer getPreferenceDrawable(int coin) {
        switch( coin ) {
            case BTC: return Btc.getPreferenceDrawable();
            case LTC: return Ltc.getPreferenceDrawable();
        }
        return null;
    }

    public static Intent getPaymentIntent(int coin) {
        Uri u = null;
        switch( coin ) {
            case BTC: u = Btc.getPaymentUri(); break;
            case LTC: u = Ltc.getPaymentUri(); break;
        }
        return u!=null ? new Intent(Intent.ACTION_VIEW, u) : null;
    }

    public static Intent getStoreIntent(int coin) {
        Uri u = null;
        switch( coin ) {
            case BTC: u = Btc.getStoreUri(); break;
            case LTC: u = Ltc.getStoreUri(); break;
        }
        return u!=null ? new Intent(Intent.ACTION_VIEW, u) : null;
    }
    public static Integer getPreferenceTitleRes(int coin) {
        switch( coin ) {
            case BTC: return Btc.getPreferenceTitleRes();
            case LTC: return Ltc.getPreferenceTitleRes();
        }
        return null;
    }
    public static Integer getPreferenceSummaryRes(int coin) {
        switch( coin ) {
            case BTC: return Btc.getPreferenceSummaryRes();
            case LTC: return Ltc.getPreferenceSummaryRes();
        }
        return null;
    }
    public static Integer getNoWalletWarn(int coin) {
        switch( coin ) {
            case BTC: return Btc.getNoWalletWarn();
            case LTC: return Ltc.getNoWalletWarn();
        }
        return null;
    }

}
