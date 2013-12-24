package pl.d30.bitcoin.dash.conf;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.Arrays;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.cryptocoin.Btc;
import pl.d30.bitcoin.dash.cryptocoin.Ltc;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class BitcoinMonitorDashConf extends PreferenceActivity {

    private Context context;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEasyTracker();

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
        if(item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unsetEasyTracker();
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return BitcoinMonitorConfFragment.class.getName().equals(fragmentName);
    }

    protected void setFragment() {
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new BitcoinMonitorConfFragment())
            .commit();
    }

    protected void setEasyTracker() {
        EasyTracker.getInstance().activityStart(this);
    }
    protected void unsetEasyTracker() {
        EasyTracker.getInstance().activityStop(this);
    }


    protected class BitcoinMonitorConfFragment extends PreferenceFragment {

        protected ListPreference currency, source;
        protected EditTextPreference amount;
        protected PreferenceManager pm;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            pm = getPreferenceManager();
            setPreferenceFiles();

            addPreferencesFromResource(R.xml.dash_monitor_conf);

            handleNotice();

            currency = (ListPreference) findPreference(D30.IDX_CURRENCY);

            source = (ListPreference) findPreference(D30.IDX_SOURCE);
            if( source!=null ) {
                int v = Integer.parseInt(source.getValue());
                adjustCurrencies( v );
                source.setIcon( getIcon(v) );

                source.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int nv = Integer.parseInt( newValue.toString() );

                    adjustCurrencies(nv);
                    preference.setIcon( getIcon(nv) );

//                    CheckBoxPreference cbp = (CheckBoxPreference) findPreference("experimental");
//                    if( cbp!=null ) {
//                        cbp.setEnabled( nv==D30.BTCE );
//                        if( nv==D30.BTCE ) cbp.setChecked(false);
//                    }
                    return true;
                    }
                });
            }

            amount = (EditTextPreference) findPreference(D30.IDX_AMOUNT);
            if( amount!=null ) {
                amount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        if( Float.parseFloat(newValue.toString())<=0 ) throw new NumberFormatException();

                    } catch(NumberFormatException e) {
                        Toast.makeText(context, getString(R.string.error_invalid_amount), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                    }
                });
            }

            Preference donateBTC = findPreference(D30.IDX_DONATE_BTC);
            if( donateBTC!=null ) {
                donateBTC.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Btc.getPaymentUri()));

                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, R.string.warn_no_wallet_btc, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intent.ACTION_VIEW, Btc.getStoreUri()));
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
                        startActivity(new Intent(Intent.ACTION_VIEW, Ltc.getPaymentUri() ));

                    } catch(ActivityNotFoundException e) {
                        Toast.makeText(context, R.string.warn_no_wallet_ltc, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intent.ACTION_VIEW, Ltc.getStoreUri() ));
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
        protected void handleNotice() {
            Preference p = findPreference("notice");
            if( p!=null ) getPreferenceScreen().removePreference(p);
        }

        private void adjustCurrencies(int source) {
            if( source==Exchange.BITSTAMP ) {
                currency.setValue( "" + Exchange.USD );
                currency.setEnabled(false);
                currency.setSummary(R.string.currency_summary_not_supported);

            } else {
                currency.setEnabled(true);
                currency.setSummary(R.string.currency_summary_active);

                if( source==Exchange.MTGOX) {
                    currency.setEntries(R.array.currencies_btc_mtgox_list);
                    currency.setEntryValues(R.array.currencies_btc_mtgox_values);

                } else {
                    if( !Arrays.asList(getResources().getStringArray(R.array.currencies_btce_values)).contains(currency.getValue()) )
                        currency.setValue( "" + Exchange.USD );

                    currency.setEntries(R.array.currencies_btce_list);
                    currency.setEntryValues(R.array.currencies_btce_values);
                }
            }
        }
    }
}
