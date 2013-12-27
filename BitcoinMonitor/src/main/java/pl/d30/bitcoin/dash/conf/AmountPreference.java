package pl.d30.bitcoin.dash.conf;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import pl.d30.bitcoin.R;

public class AmountPreference extends Preference {

    private CheckBox checkBox;
    private SeekBar seekBar;

    public AmountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View v = super.getView(convertView, parent);
        if( v!=null ) {

            checkBox = (CheckBox) v.findViewById(android.R.id.title);
            seekBar = (SeekBar) v.findViewById(R.id.amount_value);

            if( checkBox !=null && seekBar !=null ) {

                boolean isEnabled = isSeekBarEnabled();
                float value = getPersistedFloat( 0f );

                checkBox.setChecked( isEnabled );
                seekBar.setEnabled( true );
                seekBar.setMax( 140 );
                seekBar.setProgress( convertToInt(value) );

                seekBar.setThumb( getContext().getResources().getDrawable( isEnabled
                    ? R.drawable.sb_thumb
                    : R.drawable.sb_thumb_disabled
                ));
                seekBar.setProgressDrawable( getContext().getResources().getDrawable( isEnabled
                    ? R.drawable.sb_progress
                    : R.drawable.sb_track
                ));

                updateLabel( isEnabled, value );

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        seekBar.setThumb( getContext().getResources().getDrawable( isChecked
                            ? R.drawable.sb_thumb
                            : R.drawable.sb_thumb_disabled
                        ));
                        seekBar.setProgressDrawable( getContext().getResources().getDrawable( isChecked
                            ? R.drawable.sb_progress
                            : R.drawable.sb_track
                        ));

                        updateLabel( isChecked, getPersistedFloat(0f) );
                        setSeekBarEnabled( isChecked );
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                        checkBox.setChecked( true );
                        seekBar.setThumb(getContext().getResources().getDrawable(R.drawable.sb_thumb));
                        updateLabel( true, convertToFloat(progress) );
                        persistFloat( convertToFloat(progress) );
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
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
    private void updateLabel(boolean isEnabled, float value) {
        checkBox.setText(isEnabled
            ? getContext().getResources().getString(getTitleRes(), String.format("%.1f", value))
            : "Disabled"
        );
    }

    private boolean isSeekBarEnabled() {
        SharedPreferences sp = getSharedPreferences();
        return ( sp!=null ) && sp.getBoolean(getStatusKey(), false);
    }
    private void setSeekBarEnabled(boolean state) {
        SharedPreferences.Editor spe = getEditor();
        if( spe!=null ) spe.putBoolean(getStatusKey(), state).apply();
    }

    private String getStatusKey() {
        return getKey() + "_status";
    }

}
