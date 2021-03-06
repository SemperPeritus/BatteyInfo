package com.platonefimov.batteyinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;

import java.util.Locale;

public class StatusFragment extends Fragment {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private IconRoundCornerProgressBar mBatteryProgressBar;

    private TextView mBatteryLevelValue;
    private TextView mBatteryHealthValue;
    private TextView mBatteryPluggedValue;
    private TextView mBatteryStatusValue;
    private TextView mBatteryTemperatureValue;
    private TextView mBatteryVoltageValue;
    private TextView mBatteryTechnologyValue;

    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)) {
                mBatteryProgressBar.setIconImageResource(R.drawable.ic_battery_unknown_black);
                return;
            }

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);

            mBatteryProgressBar.setMax(scale);
            mBatteryProgressBar.setProgress(level);
            mBatteryProgressBar.invalidate();

            mBatteryLevelValue.setText(String.format(Locale.ENGLISH, "%d%%", batteryPct));

            boolean isUnknown = false;
            boolean isNormal = false;
            boolean isPlugged = true;

            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            switch (health) {
                case (BatteryManager.BATTERY_HEALTH_COLD):
                    mBatteryHealthValue.setText("Cold");
                    break;
                case (BatteryManager.BATTERY_HEALTH_DEAD):
                    mBatteryHealthValue.setText("Dead");
                    break;
                case (BatteryManager.BATTERY_HEALTH_GOOD):
                    mBatteryHealthValue.setText("Good");
                    isNormal = true;
                    break;
                case (BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE):
                    mBatteryHealthValue.setText("Over voltage");
                    break;
                case (BatteryManager.BATTERY_HEALTH_OVERHEAT):
                    mBatteryHealthValue.setText("Overheat");
                    break;
                case (BatteryManager.BATTERY_HEALTH_UNKNOWN):
                    mBatteryHealthValue.setText("Unknown");
                    isUnknown = true;
                    break;
                case (BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE):
                    mBatteryHealthValue.setText("Failure");
                    isUnknown = true;
                    break;
            }

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch (status) {
                case (BatteryManager.BATTERY_STATUS_CHARGING):
                    mBatteryStatusValue.setText("Charging");
                    break;
                case (BatteryManager.BATTERY_STATUS_DISCHARGING):
                    mBatteryStatusValue.setText("Discharging");
                    break;
                case (BatteryManager.BATTERY_STATUS_FULL):
                    mBatteryStatusValue.setText("Full");
                    break;
                case (BatteryManager.BATTERY_STATUS_NOT_CHARGING):
                    mBatteryStatusValue.setText("Not charging");
                    break;
                case (BatteryManager.BATTERY_STATUS_UNKNOWN):
                    mBatteryStatusValue.setText("Unknown");
                    isUnknown = true;
                    break;
            }

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            switch (plugged) {
                case (BatteryManager.BATTERY_PLUGGED_AC):
                    mBatteryPluggedValue.setText("AC charger");
                    break;
                case (BatteryManager.BATTERY_PLUGGED_USB):
                    mBatteryPluggedValue.setText("USB charger");
                    break;
                case (BatteryManager.BATTERY_PLUGGED_WIRELESS):
                    mBatteryPluggedValue.setText("Wireless charger");
                    break;
                default:
                    mBatteryPluggedValue.setText("Unplugged");
                    isPlugged = false;
                    break;
            }

            float temperature = (float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10;
            mBatteryTemperatureValue.setText(String.valueOf(temperature) + " \u2103");

            float voltage = (float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            while (voltage > 20)
                voltage /= 10;
            mBatteryVoltageValue.setText(String.valueOf(voltage) + " V");

            String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            mBatteryTechnologyValue.setText(technology);

            if (isUnknown)
                mBatteryProgressBar.setIconImageResource(R.drawable.ic_battery_unknown_black);
            else if (isNormal) {
                if (isPlugged)
                    mBatteryProgressBar.setIconImageResource(R.drawable.ic_battery_charging_full_black);
                else {
                    if (batteryPct <= 15)
                        mBatteryProgressBar.setIconImageResource(R.drawable.ic_battery_alert_black);
                    else
                        mBatteryProgressBar.setIconImageResource(R.drawable.ic_battery_full_black);
                }
            } else
                mBatteryProgressBar.setIconImageResource(R.drawable.ic_battery_alert_black);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_status, container, false);

        mBatteryProgressBar = rootView.findViewById(R.id.battery_progress_bar);
        mBatteryLevelValue = rootView.findViewById(R.id.battery_level_value);
        mBatteryHealthValue = rootView.findViewById(R.id.battery_health_value);
        mBatteryPluggedValue = rootView.findViewById(R.id.battery_plugged_value);
        mBatteryStatusValue = rootView.findViewById(R.id.battery_status_value);
        mBatteryTemperatureValue = rootView.findViewById(R.id.battery_temperature_value);
        mBatteryVoltageValue = rootView.findViewById(R.id.battery_voltage_value);
        mBatteryTechnologyValue = rootView.findViewById(R.id.battery_technology_value);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(statusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(statusReceiver);
    }
}
