package pl.d30.bitcoin.dash.conf;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
import pl.d30.bitcoin.dash.cryptocoin.Btc;
import pl.d30.bitcoin.dash.cryptocoin.Ltc;
import pl.d30.bitcoin.dash.exchange.Exchange;

public class BitcoinArbitrageDashConf extends PreferenceActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        final ActionBar ab = getActionBar();
        if( ab!=null ) ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getFragmentManager()
            .beginTransaction()
            .add(android.R.id.content, new BitcoinArbitrageConfFragment())
            .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId()==android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected( item );
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
            if( pm!=null ) pm.setSharedPreferencesName(D30.PREF_FILE_BTC);

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
                    preference.setIcon( Exchange.getIcon(position) );
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

            final CheckBoxPreference orderBook = (CheckBoxPreference) findPreference("order_book");
            if( orderBook!=null ) {
                final DialogInterface.OnClickListener onClicker = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if( AlertDialog.BUTTON_POSITIVE==which )
                            orderBook.setChecked(true);
                    }
                };
                orderBook.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference, Object newValue) {
                    if( (Boolean)newValue ) {
                        new AlertDialog.Builder(BitcoinArbitrageDashConf.this)
                            .setIcon(R.drawable.ic_notice)
                            .setTitle(R.string.order_book_confirmation_title)
                            .setMessage(R.string.order_book_confirmation_msg)
                            .setPositiveButton(android.R.string.ok, onClicker)
                            .setNegativeButton(android.R.string.cancel, onClicker)
                            .create()
                            .show();

                        return false;
                    }
                    return true;
                    }
                });

            }

//            EditTextPreference diffBelow = (EditTextPreference) findPreference(D30.IDX_DIFF_BELOW);
//            if( diffBelow!=null ) {
//                updateBelow(diffBelow, Float.parseFloat(diffBelow.getText()));
//                diffBelow.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                    @Override
//                    public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    try {
//                        Float value = Float.parseFloat(newValue.toString());
//                        updateBelow(preference, value);
//                        return true;
//
//                    } catch(NumberFormatException e) {
//                        Toast.makeText(context, getString(R.string.error_invalid_amount), Toast.LENGTH_LONG).show();
//                        return false;
//                    }
//                    }
//                });
//            }
//
//            EditTextPreference diffAbove = (EditTextPreference) findPreference(D30.IDX_DIFF_ABOVE);
//            if( diffAbove!=null ) {
//                updateAbove(diffAbove, Float.parseFloat(diffAbove.getText()));
//                diffAbove.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                    @Override
//                    public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    try {
//                        Float value = Float.parseFloat(newValue.toString());
//                        updateAbove(preference, value);
//                        return true;
//
//                    } catch(NumberFormatException e) {
//                        Toast.makeText(context, getString(R.string.error_invalid_amount), Toast.LENGTH_LONG).show();
//                        return false;
//                    }
//                    }
//                });
//            }

            Preference donate = findPreference(D30.IDX_DONATE_BTC);
            if( donate!=null ) {
                donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Btc.getPaymentUri() ));

                    } catch(ActivityNotFoundException e) {
                        Toast.makeText(context, R.string.warn_no_wallet_btc, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Intent.ACTION_VIEW, Btc.getStoreUri() ));
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

        protected Integer getIcon(String item) {
            return Exchange.getIcon(Integer.parseInt(item));
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

        private void updateAbove(Preference p, Float value) {
            if( value==0f ) {
                p.setTitle(R.string.notif_diff_above_title_off);
                p.setSummary(R.string.notif_diff_above_summary);

            } else {
                p.setTitle( getString(R.string.notif_diff_above_title_on, "" + value, "%") );
                p.setSummary(R.string.notif_enabled);

            }
        }
        private void updateBelow(Preference p, Float value) {
            if( value==0f ) {
                p.setTitle(R.string.notif_diff_below_title_off);
                p.setSummary(R.string.notif_diff_below_summary);

            } else {
                p.setTitle( getString(R.string.notif_diff_below_title_on, "" + value, "%") );
                p.setSummary(R.string.notif_enabled);

            }
        }
    }

}
