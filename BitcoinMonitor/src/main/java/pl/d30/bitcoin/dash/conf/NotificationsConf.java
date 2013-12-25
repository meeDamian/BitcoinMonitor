package pl.d30.bitcoin.dash.conf;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;

public class NotificationsConf extends PreferenceActivity {

    private Context context;
    private Switch masterSwitch;

    private NotificationsFragment nf;

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
        nf = new NotificationsFragment();
        getFragmentManager()
            .beginTransaction()
            .add(android.R.id.content, nf)
            .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notifications, menu);

        MenuItem m = menu.findItem(R.id.notification_switch);
        if( m!=null ) {
            RelativeLayout rl = (RelativeLayout) m.getActionView();
            if( rl!=null ) nf.registerMasterSwitch( (Switch) rl.findViewById(R.id.actionBarSwitch) );
        }
        return true;
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
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.equals( NotificationsFragment.class.getName() );
    }

    public class NotificationsFragment extends PreferenceFragment {

        private PreferenceManager pm;
        private SharedPreferences sp;

        private Switch masterSwitch;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            pm = getPreferenceManager();
            if( pm!=null ) pm.setSharedPreferencesName(D30.PREF_FILE_NOTIF);

            sp = getSharedPreferences(D30.PREF_FILE_NOTIF, MODE_PRIVATE);

            addPreferencesFromResource(R.xml.dash_notifications_conf);
        }

        public void registerMasterSwitch(Switch s) {
            masterSwitch = s;

            boolean isEnabled = sp.getBoolean(D30.IDX_MASTER_SWITCH, false);
            updatePreferencesState(isEnabled);
            updateMasterState(isEnabled);

            masterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updatePreferencesState(isChecked);
                    saveMasterState(isChecked);
                }
            });

        }

        private void saveMasterState(boolean state) {
            sp.edit().putBoolean(D30.IDX_MASTER_SWITCH, state).apply();
        }
        private void updatePreferencesState(boolean state) {
            getPreferenceScreen().setEnabled(state);
        }
        private void updateMasterState(boolean state) {
            masterSwitch.setChecked(state);
        }
    }
}
