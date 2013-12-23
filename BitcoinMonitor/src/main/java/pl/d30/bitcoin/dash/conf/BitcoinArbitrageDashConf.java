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
                updateIcon(exchangeBuy, Integer.parseInt(exchangeBuy.getValue()));
                exchangeBuy.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int position = Integer.parseInt(newValue.toString());
                    updateIcon(preference, position);
                    hideExchange(position);
                    return true;
                    }
                });
            }

            if( exchangeSell!=null ) {
                updateIcon(exchangeSell, Integer.parseInt(exchangeSell.getValue()));
                exchangeSell.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                    updateIcon(preference, Integer.parseInt(newValue.toString()));
                    return true;
                    }
                });
            }
        }

        protected void setPreferenceFiles() {
            pm.setSharedPreferencesName(D30.PREF_FILE_BTC);
        }

        protected void updateIcon(Preference p, int item) {
            switch( item ) {
                case 0:
                    p.setIcon( R.drawable.ic_mtgox_blue);
                    break;

                case 1:
                    p.setIcon( R.drawable.ic_bitstamp_blue);
                    break;

                case 2:
                    p.setIcon( R.drawable.ic_btce_blue);
                    break;
            }
        }

        // NOTE: isn't like the ugliest code ever? (:
        protected void hideExchange(int position) {
            ArrayList<CharSequence> tmpNames = new ArrayList<CharSequence>();
            ArrayList<CharSequence> tmpValues = new ArrayList<CharSequence>();
            for(int i=0; i<exchangeNames.length && i!=position; i++) {
                tmpNames.add( exchangeNames[i] );
                tmpValues.add( exchangeValues[i] );
            }
            if( Integer.parseInt(exchangeSell.getValue())==position ) {
                String newValue = tmpValues.get(0).toString();
                exchangeSell.setValue(newValue);
                updateIcon(exchangeSell, Integer.parseInt(newValue));
            }
            exchangeSell.setEntries( tmpNames.toArray(new CharSequence[tmpNames.size()]) );
            exchangeSell.setEntryValues( tmpValues.toArray(new CharSequence[tmpValues.size()]) );
        }
    }

}
