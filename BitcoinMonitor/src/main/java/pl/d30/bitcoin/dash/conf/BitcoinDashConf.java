package pl.d30.bitcoin.dash.conf;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;

public class BitcoinDashConf extends PreferenceActivity {

    private static final String BITCOIN_ADDRESS = "1NEDwbKeTH4isApMg94R2C4y72i8qu63GG";
    private static final String DEFAULT_DONATION = "0.02";
    private static final String ALTERNATIVE_APP = "piuk.blockchain.android";

    private Context context;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        final ActionBar ab = getActionBar();
        if( ab!=null ) {
            ab.setIcon(R.drawable.icon_small);
            ab.setDisplayHomeAsUpEnabled(true);
        }
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

    protected void setFragment() {
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new BitcoinConfFragment())
            .commit();
    }


    protected class BitcoinConfFragment extends PreferenceFragment {

        protected ListPreference currency, source;
        protected EditTextPreference amount;
        protected PreferenceManager pm;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            pm = getPreferenceManager();
            setPreferenceFiles();

            addPreferencesFromResource(R.xml.dash_conf);

            currency = (ListPreference) findPreference("currency");

            source = (ListPreference) findPreference("source");
            if( source!=null ) {
                adjustCurrencies( Integer.parseInt(source.getValue()) );

                source.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int nv = Integer.parseInt( newValue.toString() );

                        adjustCurrencies(nv);

//                        CheckBoxPreference cbp = (CheckBoxPreference) findPreference("experimental");
//                        if( cbp!=null ) {
//                            cbp.setEnabled( nv==D30.SOURCE_BTCE );
//                            if( nv==D30.SOURCE_BTCE ) cbp.setChecked(false);
//                        }
                        return true;
                    }
                });
            }

            amount = (EditTextPreference) findPreference("amount");
            if( amount!=null ) {
                amount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        try {
                            Float.parseFloat(newValue.toString());

                        } catch (NumberFormatException e) {
                            Toast.makeText(context, getString(R.string.error_invalid_amount), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }
                });
            }

            Preference donate = findPreference("donate");
            if( donate!=null ) {
                donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("bitcoin:" + BITCOIN_ADDRESS + "?amount=" + DEFAULT_DONATION)));
                    } catch(ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+ALTERNATIVE_APP)));
                    }
                    return false;
                    }
                });
            }
        }

        protected void setPreferenceFiles() {
            pm.setSharedPreferencesName(D30.PREF_FILE_BTC);
        }

        private void adjustCurrencies(int source) {
            if( source==D30.SOURCE_BITSTAMP ) {
                currency.setValue( D30.DEF_CURRENCY );
                currency.setEnabled(false);
                currency.setSummary(R.string.currency_summary_not_supported);

            } else {
                currency.setEnabled(true);
                currency.setSummary(R.string.currency_summary_active);

                if( source==D30.SOURCE_MTGOX ) {
                    currency.setEntries(R.array.currencies_btc_mtgox_list);
                    currency.setEntryValues(R.array.currencies_btc_mtgox_values);

                } else {
                    if( !Arrays.asList(getResources().getStringArray(R.array.currencies_btce_values)).contains(currency.getValue()) )
                        currency.setValue( D30.DEF_CURRENCY );

                    currency.setEntries(R.array.currencies_btce_list);
                    currency.setEntryValues(R.array.currencies_btce_values);
                }
            }
        }
    }
}
