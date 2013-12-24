package pl.d30.bitcoin.dash.cryptocoin;

import android.net.Uri;

import pl.d30.bitcoin.R;

public class Ltc extends Coin {

    public static final String DONATION_ADDRESS = "LKpdDVpnWWk8tNtrBbyxCSrQKTMcRbJcop";
    public static final String DONATION_DEFAULT = "0.1";
    public static final String ALTERNATIVE_APP = "de.schildbach.wallet.litecoin";

    public static int getId() {
        return LTC;
    }

    public static String getName() {
        return "LTC";
    }

    public static Integer getDrawable() {
        return R.drawable.ic_ltc;
    }

    public static Uri getPaymentUri() {
        return Uri.parse("litecoin:" + DONATION_ADDRESS + "?amount=" + DONATION_DEFAULT );
    }


    public static Uri getStoreUri() {
        return Uri.parse(STORE_PREFIX + ALTERNATIVE_APP);
    }
}
