package pl.d30.bitcoin.dash.conf;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;

public class LitecoinMonitorDashConf extends MonitorDashConf {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.equals( LitecoinMonitorConfFragment.class.getName() );
    }

    @Override
    protected PreferenceFragment getFragment() {
        return new LitecoinMonitorConfFragment( context );
    }

    protected class LitecoinMonitorConfFragment extends MonitorDashFragment {

        public LitecoinMonitorConfFragment(Context c) {
            super(c);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            source.setTitle(R.string.source_title_ltc);
            source.setSummary(R.string.source_summary_ltc);
            source.setEnabled(false);

            currency.setEntries(R.array.currencies_btce_list);
            currency.setEntryValues(R.array.currencies_btce_values);

            amount.setTitle(R.string.amount_title_ltc);
            amount.setSummary(R.string.amount_summary_ltc);
        }

        @Override
        protected String getPreferenceFileName() {
            return D30.PREF_FILE_LTC;
        }



        @Override
        protected void handleNotice() {
            Preference p = findPreference("notice");
            if( p!=null ) {
                p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "m@urycy.pl", null));
                    i.putExtra(Intent.EXTRA_SUBJECT, "[DashCoin] Loyal user premium request.");
                    i.putExtra(Intent.EXTRA_TEXT, "Your email: \n\n*DashClock Bitcoin Widget*\nWhat do you like about it: \nWhat would you change: ");
                    startActivity(i);
                    return false;
                    }
                });
            }
        }
    }
}
