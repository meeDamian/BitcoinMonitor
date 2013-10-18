package pl.d30.bitcoin.dash.conf;

import android.os.Bundle;

import pl.d30.bitcoin.R;

public class LitecoinDashConf extends BitcoinDashConf {

    @Override
    protected void setFragment() {
        getFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new LitecoinConfFragment())
            .commit();
    }

    protected class LitecoinConfFragment extends BitcoinConfFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            source.setTitle(R.string.source_title_ltc);
            source.setSummary(R.string.source_summary_ltc);
            source.setEnabled(false);

            currency.setEntries(R.array.currencies_btce_list);
            currency.setEntryValues(R.array.currencie_btce_values);

            amount.setTitle(R.string.amount_title_ltc);
            amount.setSummary(R.string.amount_summary_ltc);
        }

        @Override
        protected void setPreferenceFiles() {
            pm.setSharedPreferencesName("ltc");
        }
    }
}
