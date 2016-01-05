package pl.d30.bitcoin.dash.conf;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

public abstract class MonitorDashConf extends PreferenceActivity {

    protected Context context;

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
            .add(android.R.id.content, getFragment())
            .commit();
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
    protected abstract boolean isValidFragment(String fragmentName);


    protected abstract PreferenceFragment getFragment();
}
