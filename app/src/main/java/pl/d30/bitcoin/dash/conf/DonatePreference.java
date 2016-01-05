package pl.d30.bitcoin.dash.conf;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.cryptocoin.Coin;

public class DonatePreference extends Preference {

    private Integer coin = Coin.BTC;

    public DonatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DonatePreference);
        if(a != null) {
            final int N = a.getIndexCount();
            for(int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch(attr) {
                    case R.styleable.DonatePreference_coin:
                        String tmpCoin = a.getString(attr);
                        if(tmpCoin != null)
                            coin = Coin.getCoinIdByName(tmpCoin);

                        break;
                }
            }
            a.recycle();
        }
        if(coin == null)
            coin = Coin.BTC;

        setIcon(context.getResources().getDrawable(Coin.getPreferenceDrawable(coin)));
    }

    @Override
    protected void onClick() {
        Intent i = Coin.getPaymentIntent(coin);
        PackageManager pm = getContext().getPackageManager();
        if(pm != null && i.resolveActivity(pm) != null)
            getContext().startActivity(Intent.createChooser(i, "Choose payment app"));

        else {
            Toast.makeText(getContext(), Coin.getNoWalletWarn(coin), Toast.LENGTH_SHORT).show();
            getContext().startActivity( Coin.getStoreIntent(coin) );
        }
    }

    @Override
    public void setPersistent(boolean persistent) {
        super.setPersistent(false);
    }

    @Override
    public CharSequence getTitle() {
        return getContext().getString(Coin.getPreferenceTitleRes(coin));
    }

    @Override
    public CharSequence getSummary() {
        return getContext().getString(Coin.getPreferenceSummaryRes(coin));
    }
}
