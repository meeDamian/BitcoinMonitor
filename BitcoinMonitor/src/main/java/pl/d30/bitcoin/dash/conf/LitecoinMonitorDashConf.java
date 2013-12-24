package pl.d30.bitcoin.dash.conf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;

import com.google.analytics.tracking.android.EasyTracker;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;

public class LitecoinMonitorDashConf extends BitcoinMonitorDashConf {

    @Override
    protected void setFragment() {
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new LitecoinMonitorConfFragment())
            .commit();
    }

    protected void setEasyTracker() {
        EasyTracker.getInstance().activityStart(this);
    }
    protected void unsetEasyTracker() {
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return LitecoinMonitorConfFragment.class.getName().equals(fragmentName);
    }

    protected class LitecoinMonitorConfFragment extends BitcoinMonitorConfFragment {

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
        protected void setPreferenceFiles() {
            pm.setSharedPreferencesName(D30.PREF_FILE_LTC);
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
