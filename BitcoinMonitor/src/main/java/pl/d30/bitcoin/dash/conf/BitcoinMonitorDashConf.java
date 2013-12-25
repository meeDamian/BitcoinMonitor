package pl.d30.bitcoin.dash.conf;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import pl.d30.bitcoin.D30;

public class BitcoinMonitorDashConf extends MonitorDashConf {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.equals( BitcoinMonitorConfFragment.class.getName() );
    }

    @Override
    protected PreferenceFragment getFragment() {
        return new BitcoinMonitorConfFragment( context );
    }

    protected class BitcoinMonitorConfFragment extends MonitorDashFragment {

        public BitcoinMonitorConfFragment(Context c) {
            super(c);
        }

        @Override
        protected String getPreferenceFileName() {
            return D30.PREF_FILE_BTC;
        }


        @Override
        protected void handleNotice() {
            Preference p = findPreference("notice_category");
            if( p!=null ) getPreferenceScreen().removePreference(p);
        }
    }
}
