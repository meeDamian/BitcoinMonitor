package pl.d30.bitcoin.dash.conf;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import pl.d30.bitcoin.D30;
import pl.d30.bitcoin.R;
import pl.d30.bitcoin.dash.cryptocoin.Btc;
import pl.d30.bitcoin.dash.cryptocoin.Ltc;

public class NotificationsConf extends PreferenceActivity {

    private Context context;

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
            .add(android.R.id.content, new NotYetImplementedFragment())
            .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notifications, menu);

        MenuItem m = menu.findItem(R.id.notification_switch);
        if( m!=null ) {
            RelativeLayout rl = (RelativeLayout) m.getActionView();
            if( rl!=null ) {
                Switch masterSwitch = (Switch) rl.findViewById(R.id.actionBarSwitch);
                if( masterSwitch!=null ) {
//                    nf.registerMasterSwitch( masterSwitch );
                    masterSwitch.setChecked(false);
                    masterSwitch.setEnabled(false);
                }

            }
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

    public class NotYetImplementedFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.not_yet_implemented_dummy, container, false);
            if( v!=null ) {
                Button email = (Button) v.findViewById(R.id.email);
                if( email!=null ) {
                    email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "m@urycy.pl", null));
                        i.putExtra(Intent.EXTRA_SUBJECT, "[DashCoin] Dude, implement those notifications already!");
                        startActivity(i);
                        }
                    });
                }

                Button donateBtc = (Button) v.findViewById(R.id.donateBtc);
                if( donateBtc!=null ) {
                    donateBtc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Btc.getPaymentUri()));

                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, R.string.warn_no_wallet_btc, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Intent.ACTION_VIEW, Btc.getStoreUri()));
                        }
                        }
                    });
                }

                Button donateLtc = (Button) v.findViewById(R.id.donateLtc);
                if( donateLtc!=null ) {
                    donateLtc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Ltc.getPaymentUri() ));

                        } catch(ActivityNotFoundException e) {
                            Toast.makeText(context, R.string.warn_no_wallet_ltc, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Intent.ACTION_VIEW, Ltc.getStoreUri() ));
                        }
                        }
                    });
                }

            }
            return v;
        }
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

            addPreferencesFromResource(R.xml.dash_notifications_conf);
        }

        public void registerMasterSwitch(Switch s) {
            masterSwitch = s;

            boolean isEnabled = getSp().getBoolean(D30.IDX_MASTER_SWITCH, false);
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
            getSp().edit().putBoolean(D30.IDX_MASTER_SWITCH, state).apply();
        }
        private void updatePreferencesState(boolean state) {
            getPreferenceScreen().setEnabled(state);
        }
        private void updateMasterState(boolean state) {
            masterSwitch.setChecked(state);
        }

        private SharedPreferences getSp() {
            if( sp==null ) sp = getSharedPreferences(D30.PREF_FILE_NOTIF, MODE_PRIVATE);
            return sp;
        }
    }
}
