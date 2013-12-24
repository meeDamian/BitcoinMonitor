package pl.d30.bitcoin.dash.cryptocoin;

public abstract class Coin {

    protected static final String STORE_PREFIX = "http://play.google.com/store/apps/details?id=";

    public static final int BTC = 0;
    public static final int LTC = 1;

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

}
