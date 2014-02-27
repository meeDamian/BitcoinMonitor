package pl.d30.bitcoin.dash.cryptocoin;

import android.net.Uri;

import pl.d30.bitcoin.R;

public class Btc extends Coin {

    public static final String DONATION_ADDRESS = "1CMZNs2nQjkfvit8Qoq1ZL38XUPJ6EDwCu";
    public static final String DONATION_DEFAULT = "0.005";
    public static final String ALTERNATIVE_APP = "piuk.blockchain.android";

    public static int getId() { return BTC; }
    public static String getName() { return "BTC"; }

    public static Integer getDrawable() { return R.drawable.ic_btc; }
    public static Integer getPreferenceDrawable() { return R.drawable.ic_btc_blue; }
    public static Integer getPreferenceTitleRes() { return R.string.donate_title; }
    public static Integer getPreferenceSummaryRes() { return R.string.donate_summary; }
    public static Integer getNoWalletWarn() { return R.string.warn_no_wallet_btc; }

    public static Uri getPaymentUri() {
        return Uri.parse("bitcoin:" + DONATION_ADDRESS + "?amount=" + DONATION_DEFAULT );
    }
    public static Uri getStoreUri() {
        return Uri.parse(STORE_PREFIX + ALTERNATIVE_APP);
    }
}
