package pl.d30.bitcoin.dash.conf;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Arrays;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.cryptocoin.Btc;
import pl.d30.bitcoin.dash.cryptocoin.Ltc;
import pl.d30.bitcoin.dash.exchange.Exchange;

public abstract class MonitorDashFragment extends PreferenceFragment{

    protected ListPreference currency, source;
    protected EditTextPreference amount;
    protected PreferenceManager pm;

    protected Context context;

    public MonitorDashFragment(Context c) {
        context = c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pm = getPreferenceManager();
        pm.setSharedPreferencesName( getPreferenceFileName() );

        addPreferencesFromResource(R.xml.dash_monitor_conf);


        // temporary!
        handleNotice();



        currency = (ListPreference) findPreference( D30.IDX_CURRENCY );

        source = (ListPreference) findPreference(D30.IDX_SOURCE);
        if( source!=null ) {
            int v = Integer.parseInt(source.getValue());
            adjustCurrencies( v );
            source.setIcon( Exchange.getIcon(v) );

            source.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                int nv = Integer.parseInt( newValue.toString() );

                adjustCurrencies(nv);
                preference.setIcon( Exchange.getIcon(nv) );

//                CheckBoxPreference cbp = (CheckBoxPreference) findPreference("experimental");
//                if( cbp!=null ) {
//                    cbp.setEnabled( nv==D30.BTCE );
//                    if( nv==D30.BTCE ) cbp.setChecked(false);
//                }
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
                    Toast.makeText(context, getString(R.string.error_invalid_amount), Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
                }
            });
        }

        EditTextPreference priceBelow = (EditTextPreference) findPreference(D30.IDX_PRICE_BELOW);
        if( priceBelow!=null ) {
            updateBelow(priceBelow, Float.parseFloat(priceBelow.getText()));
            priceBelow.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    Float value = Float.parseFloat(newValue.toString());
                    updateBelow(preference, value);
                    return true;

                } catch(NumberFormatException e) {
                    Toast.makeText(context, getString(R.string.error_invalid_amount), Toast.LENGTH_LONG).show();
                    return false;
                }
                }
            });
        }

        EditTextPreference priceAbove = (EditTextPreference) findPreference(D30.IDX_PRICE_ABOVE);
        if( priceAbove!=null ) {
            updateAbove(priceAbove, Float.parseFloat(priceAbove.getText()));
            priceAbove.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    Float value = Float.parseFloat(newValue.toString());
                    updateAbove(preference, value);
                    return true;

                } catch(NumberFormatException e) {
                    Toast.makeText(context, getString(R.string.error_invalid_amount), Toast.LENGTH_LONG).show();
                    return false;
                }
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

    private void updateAbove(Preference p, Float value) {
        if( value==0f ) {
            p.setTitle(R.string.notif_price_above_title_off);
            p.setSummary(R.string.notif_price_above_summary);

        } else if(value>0) {
            p.setTitle( getString(R.string.notif_price_above_title_on, "$", "" + value) );
            p.setSummary(R.string.notif_enabled);

        } else throw new NumberFormatException("that value cannot be negative");
    }
    private void updateBelow(Preference p, Float value) {
        if( value==0f ) {
            p.setTitle(R.string.notif_price_below_title_off);
            p.setSummary(R.string.notif_price_below_summary);

        } else if(value>0) {
            p.setTitle( getString(R.string.notif_price_below_title_on, "$", "" + value) );
            p.setSummary(R.string.notif_enabled);

        } else throw new NumberFormatException("that value cannot be negative");
    }

    protected abstract String getPreferenceFileName();
    protected abstract void handleNotice();
}
