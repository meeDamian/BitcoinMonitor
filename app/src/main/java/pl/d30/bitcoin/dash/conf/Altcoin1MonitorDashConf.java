package pl.d30.bitcoin.dash.conf;

import android.preference.PreferenceFragment;

public class Altcoin1MonitorDashConf extends MonitorDashConf {

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.equals( AltcoinMonitorConfFragment.class.getName() );
    }

    @Override
    protected PreferenceFragment getFragment() {
        return new AltcoinMonitorConfFragment( context );
    }

}
