package pl.d30.bitcoin.dash.conf;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import pl.d30.bitcoin.R;

public class AmountPreference extends Preference {

    private static final String PREF_SUFFIX = "_status";

    private CheckBox checkBox;
    private TextView summary;
    private SeekBar seekBar;
    private Resources res;

    public AmountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View v = super.getView(convertView, parent);
        if( v!=null ) {

            checkBox = (CheckBox) v.findViewById(android.R.id.title);
            summary = (TextView) v.findViewById(android.R.id.summary);
            seekBar = (SeekBar) v.findViewById(R.id.amount_value);

            if( checkBox!=null && seekBar !=null ) {

                boolean isEnabled = isSeekBarEnabled();
                float value = getPersistedFloat( 0f );

                checkBox.setChecked( isEnabled );
                seekBar.setEnabled( isEnabled() );
                seekBar.setMax( 140 );
                seekBar.setProgress( convertToInt(value) );

                updateSeekBarAppearance( isEnabled );
                updateLabel( isEnabled, value );

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        updateSeekBarAppearance( isChecked );
                        setSeekBarEnabled(isChecked);
                        updateLabel(isChecked, getPersistedFloat(0f));
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                        checkBox.setChecked( true );
                        float value = convertToFloat(progress);
                        updateLabel( true, value );
                        persistFloat( value );
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
        summary.setText( isEnabled
            ? getSummary() //getRes().getString(getTitleRes(), String.format("%.1f", value))
            : "Notification Disabled"
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

    private void setSeekBarThumbDrawable(int thumbRes) {
        seekBar.setThumb( getRes().getDrawable( thumbRes ) );
    }
    private void setSeekBarProgressDrawable(int progressRes) {
        seekBar.setProgressDrawable( getRes().getDrawable( progressRes));
    }
    private void updateSeekBarAppearance(boolean isEnabled) {
        setSeekBarThumbDrawable(isEnabled
            ? R.drawable.sb_thumb
            : R.drawable.sb_thumb_disabled
        );
        setSeekBarProgressDrawable(isEnabled
            ? R.drawable.sb_progress
            : R.drawable.sb_track
        );
    }

    private String getStatusKey() {
        return getKey() + PREF_SUFFIX;
    }
    private Resources getRes() {
        if( res==null ) res = getContext().getResources();
        return res;
    }

}
