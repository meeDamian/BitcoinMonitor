package pl.d30.bitcoin;

public class D30 {

    public static final String LOG = "BtcManager";

    // configuration files
    public static final String PREF_FILE_BTC = "btc";
    public static final String PREF_FILE_LTC = "ltc";

    //configuration indexes
    public static final String IDX_SOURCE = "source";
    public static final String IDX_CURRENCY = "currency";
    public static final String IDX_AMOUNT= "amount";
    public static final String IDX_EXPERIMENTAL = "experimental";


    // integer values
    public static final int ITEM_BTC = 0;
    public static final int ITEM_LTC = 1;

    public static final int SOURCE_MTGOX = 0;
    public static final int SOURCE_BITSTAMP = 1;
    public static final int SOURCE_BTCE = 2;


    // DEFAULTS
    public static final String DEF_CURRENCY = "USD";


}
