package pl.d30.bitcoin.dash.conf;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import pl.d30.bitcoin.R;

public class AmountPreference extends Preference {

    private CheckBox cb;
    private SeekBar sb;

    public AmountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View v = super.getView(convertView, parent);
        if( v!=null ) {

            cb = (CheckBox) v.findViewById(android.R.id.title);
            sb = (SeekBar) v.findViewById(R.id.amount_value);

            if( cb!=null && sb!=null ) {
                boolean isOn = getSharedPreferences().getBoolean(getKey()+"_status", false);

                float value = getPersistedFloat(0f);

                sb.setEnabled( isOn );
                updateLabel(value);
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sb.setEnabled(isChecked);
                        getEditor().putBoolean(getKey() + "_status", isChecked);
                    }
                });


                sb.setMax(140);
                sb.setProgress(convertToInt(value));
                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float val = convertToFloat(progress);
                        updateLabel(val);
                        persistFloat(val);
                        callChangeListener(val);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            }

        }
        return v;
    }

    private static float convertToFloat(int v) {
        return (float) v / 2 - 20;
    }
    private static int convertToInt(float v) {
        return (int) v * 2 + 40;
    }
    private void updateLabel(float v) {
        cb.setText(getContext().getResources().getString(R.string.notif_diff_above_check_label, String.format("%.1f", v)));
    }
}
