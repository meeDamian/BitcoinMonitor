package pl.d30.bitcoin.dash.cryptocoin;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import pl.d30.bitcoin.R;

public class Btc extends Coin {

    public static final String DONATION_ADDRESS = "1CMZNs2nQjkfvit8Qoq1ZL38XUPJ6EDwCu";
    public static final String DONATION_DEFAULT = "0.005";
    public static final String ALTERNATIVE_APP = "piuk.blockchain.android";

    public static int getId() { return BTC; }
    @NonNull
    @Contract(pure = true)
    public static String getName() { return "BTC"; }

    @NonNull
    @Contract(pure = true)
    public static Integer getDrawable() { return R.drawable.ic_btc; }
    @NonNull
    @Contract(pure = true)
    @DrawableRes
    public static Integer getPreferenceDrawable() { return R.drawable.ic_btc_blue; }
    @NonNull
    @Contract(pure = true)
    public static Integer getPreferenceTitleRes() { return R.string.donate_title; }
    @NonNull
    @Contract(pure = true)
    public static Integer getPreferenceSummaryRes() { return R.string.donate_summary; }
    @NonNull
    @Contract(pure = true)
    public static Integer getNoWalletWarn() { return R.string.warn_no_wallet_btc; }

    public static Uri getPaymentUri() {
        return Uri.parse("bitcoin:" + DONATION_ADDRESS + "?amount=" + DONATION_DEFAULT );
    }
    public static Uri getStoreUri() {
        return Uri.parse(STORE_PREFIX + ALTERNATIVE_APP);
    }
}
