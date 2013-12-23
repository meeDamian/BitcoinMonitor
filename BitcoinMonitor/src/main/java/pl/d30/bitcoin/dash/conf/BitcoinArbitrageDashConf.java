package pl.d30.bitcoin.dash.conf;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class BitcoinArbitrageDashConf extends PreferenceActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        context = getApplicationContext();

        final ActionBar ab = getActionBar();
        if( ab!=null ) ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId()==android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    protected void setFragment() {
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new BitcoinArbitrageConfFragment())
            .commit();
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return BitcoinArbitrageConfFragment.class.getName().equals(fragmentName);
    }

    protected class BitcoinArbitrageConfFragment extends PreferenceFragment {

        protected PreferenceManager pm;
        protected ListPreference exchangeBuy;
        protected ListPreference exchangeSell;

        protected CharSequence[] exchangeNames;
        protected CharSequence[] exchangeValues;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            pm = getPreferenceManager();
            setPreferenceFiles();

            addPreferencesFromResource(R.xml.dash_arbitrage_conf);

            exchangeNames = getResources().getTextArray(R.array.source_list);
            exchangeValues = getResources().getTextArray(R.array.source_values);

            exchangeBuy = (ListPreference) findPreference(D30.IDX_BUY_SRC);
            exchangeSell = (ListPreference) findPreference(D30.IDX_SELL_SRC);

            if( exchangeBuy!=null ) {
                exchangeBuy.setIcon( getIcon(exchangeBuy.getValue()) );
                exchangeBuy.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int position = Integer.parseInt(newValue.toString());
                    preference.setIcon( getIcon(position) );
                    hideExchange(position);
                    return true;
                    }
                });
            }

            if( exchangeSell!=null ) {
                exchangeSell.setIcon( getIcon(exchangeSell.getValue()) );
                hideExchange(Integer.parseInt(exchangeBuy.getValue()));
                exchangeSell.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setIcon(getIcon(newValue.toString()));
                    return true;
                    }
                });
            }

            Preference donate = findPreference(D30.IDX_DONATE);
            if( donate!=null ) {
                donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, getPaymentUri(Exchange.BTC) ));

                    } catch(ActivityNotFoundException e) {
                        Toast.makeText(context, R.string.warn_no_wallet_btc, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intent.ACTION_VIEW, getStoreUri(Exchange.BTC) ));
                    }
                    return false;
                    }
                });
            }

            Preference donateLTC = findPreference(D30.IDX_DONATE_LTC);
            if( donateLTC!=null ) {
                donateLTC.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, getPaymentUri(Exchange.LTC) ));

                    } catch(ActivityNotFoundException e) {
                        Toast.makeText(context, R.string.warn_no_wallet_ltc, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intent.ACTION_VIEW, getStoreUri(Exchange.LTC) ));
                    }
                    return false;
                    }
                });
            }

        }

        protected void setPreferenceFiles() {
            pm.setSharedPreferencesName(D30.PREF_FILE_BTC);
        }

        protected Integer getIcon(int item) {
            switch(item) {
                case Exchange.MTGOX: return R.drawable.ic_mtgox_blue;
                case Exchange.BITSTAMP: return R.drawable.ic_bitstamp_blue;
                case Exchange.BTCE: return R.drawable.ic_btce_blue;
            }
            return null;
        }
        protected Integer getIcon(String item) {
            return getIcon( Integer.parseInt(item) );
        }

        // NOTE: isn't like the ugliest code ever? (:
        protected void hideExchange(int position) {
            ArrayList<CharSequence> tmpNames = new ArrayList<CharSequence>();
            ArrayList<CharSequence> tmpValues = new ArrayList<CharSequence>();
            for(int i=0; i<exchangeNames.length; i++) {
                if( i!=position ) {
                    tmpNames.add( exchangeNames[i] );
                    tmpValues.add( exchangeValues[i] );
                }
            }
            if( Integer.parseInt(exchangeSell.getValue())==position ) {
                String newValue = tmpValues.get(0).toString();
                exchangeSell.setValue(newValue);
                exchangeSell.setIcon(getIcon(newValue));
            }
            exchangeSell.setEntries( tmpNames.toArray(new CharSequence[tmpNames.size()]) );
            exchangeSell.setEntryValues( tmpValues.toArray(new CharSequence[tmpValues.size()]) );
        }

        private Uri getPaymentUri(int item) {
            return Uri.parse(item == Exchange.LTC
                    ? "litecoin:" + D30.LITECOIN_ADDRESS + "?amount=" + D30.LITECOIN_DEFAULT_DONATION
                    : "bitcoin:" + D30.BITCOIN_ADDRESS + "?amount=" + D30.BITCOIN_DEFAULT_DONATION
            );
        }
        private Uri getStoreUri(int item) {
            return Uri.parse("http://play.google.com/store/apps/details?id=" + (item==Exchange.LTC ? D30.LITECOIN_ALTERNATIVE_APP : D30.BITCOIN_ALTERNATIVE_APP));
        }
    }

}
