package pl.d30.bitcoin.dash.conf;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import pl.d30.bitcoin.R;

public class NotificationsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.dash_notifications_conf);
    }
}
