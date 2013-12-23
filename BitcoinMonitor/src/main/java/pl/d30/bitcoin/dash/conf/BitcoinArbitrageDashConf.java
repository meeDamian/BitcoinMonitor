package pl.d30.bitcoin.dash.conf;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import java.util.ArrayList;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class BitcoinArbitrageDashConf extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

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
                hideExchange( Integer.parseInt(exchangeBuy.getValue()) );
                exchangeSell.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setIcon( getIcon(newValue.toString()) );
                    return true;
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
                exchangeSell.setIcon( getIcon(newValue) );
            }
            exchangeSell.setEntries( tmpNames.toArray(new CharSequence[tmpNames.size()]) );
            exchangeSell.setEntryValues( tmpValues.toArray(new CharSequence[tmpValues.size()]) );
        }
    }

}
