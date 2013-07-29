package pl.d30.bitcoin;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class BitcoinDashConf extends PreferenceActivity {
    private static final String BITCOIN_ADDRESS = "1NEDwbKeTH4isApMg94R2C4y72i8qu63GG";
    private static final String DEFAULT_DONATION = "0.02";
    private static final String ALTERNATIVE_APP = "piuk.blockchain.android";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.icon_small);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new BitcoinConfFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class BitcoinConfFragment extends PreferenceFragment {

        ListPreference currency, lp;
        EditTextPreference amount;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.wall_dash_conf);

            lp = (ListPreference) findPreference("source");
            currency = (ListPreference) findPreference("currency");
            amount = (EditTextPreference) findPreference("amount");
            if(Integer.parseInt(lp.getValue())!=BitcoinDashService.MTGOX) enableCurrency(false);

            lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int nv = Integer.parseInt(newValue.toString());
                    if(nv==BitcoinDashService.BITSTAMP || nv==BitcoinDashService.BTCE) enableCurrency(false);
                    else if(nv==BitcoinDashService.MTGOX) enableCurrency(true);

                    CheckBoxPreference cbp = (CheckBoxPreference) findPreference("experimental");
                    if(nv==BitcoinDashService.BTCE) {
                        cbp.setChecked(false);
                        cbp.setEnabled(false);

                    } else cbp.setEnabled(true);

                    return true;
                }
            });

            CheckBoxPreference litecoin = (CheckBoxPreference) findPreference("litecoin");
            if( litecoin!=null ) {
                setItem( litecoin.isChecked() );
                litecoin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        return setItem( newValue.toString().equals("true") );
                    }
                });
            }

            if( amount!=null ) {
                amount.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        try {
                            Float.parseFloat(newValue.toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Changes NOT saved due to invalid value provided.", Toast.LENGTH_SHORT).show();
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

        private void enableCurrency(boolean enable) {
            if( enable ) {
                currency.setEnabled(true);
                currency.setSummary(R.string.currency_active);

            } else {
                currency.setValue(BitcoinDashService.DEF_CURRENCY);
                currency.setEnabled(false);
                currency.setSummary(R.string.currency_not_supported);
            }
        }

        private boolean setItem( boolean isLtc ) {
            if( isLtc ) {
                amount.setTitle(R.string.amount_title_ltc);
                amount.setSummary(R.string.amount_summary_ltc);

                if( !currency.getValue().matches("USD|EUR") ) currency.setValue(BitcoinDashService.DEF_CURRENCY);
                currency.setEntryValues(R.array.currency_values_ltc);
                currency.setEntries(R.array.currency_list_ltc);

                lp.setValue(""+BitcoinDashService.BTCE);
                lp.setSummary(R.string.sources_litecoin);
                lp.setEnabled(false);

            } else {
                amount.setTitle(R.string.amount_title_btc);
                amount.setSummary(R.string.amount_summary_btc);

                currency.setEntryValues(R.array.currency_values);
                currency.setEntries(R.array.currency_list);

                lp.setSummary(R.string.sources_bitcoin);
                lp.setEnabled(true);

            }
            return true;
        }
    }
}
