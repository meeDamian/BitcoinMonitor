package pl.d30.bitcoin.dash.cryptocoin;

import android.net.Uri;

import pl.d30.bitcoin.R;

public class Ltc extends Coin {

    public static final String DONATION_ADDRESS = "LKpdDVpnWWk8tNtrBbyxCSrQKTMcRbJcop";
    public static final String DONATION_DEFAULT = "0.1";
    public static final String ALTERNATIVE_APP = "de.schildbach.wallet.litecoin";

    public static int getId() { return LTC; }
    public static String getName() { return "LTC"; }

    public static Integer getDrawable() { return R.drawable.ic_ltc; }
    public static Integer getPreferenceDrawable() { return R.drawable.ic_ltc_blue; }
    public static Integer getPreferenceTitleRes() { return R.string.donate_title_ltc; }
    public static Integer getPreferenceSummaryRes() { return R.string.donate_summary; }
    public static Integer getNoWalletWarn() { return R.string.warn_no_wallet_ltc; }

    public static Uri getPaymentUri() {
        return Uri.parse("litecoin:" + DONATION_ADDRESS + "?amount=" + DONATION_DEFAULT );
    }
    public static Uri getStoreUri() {
        return Uri.parse(STORE_PREFIX + ALTERNATIVE_APP);
    }
}
