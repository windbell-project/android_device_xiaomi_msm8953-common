package com.xiaomi.parts;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SELinux;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;

import com.xiaomi.parts.fps.FPSInfoService;
import com.xiaomi.parts.kcal.KCalSettingsActivity;
import com.xiaomi.parts.preferences.SecureSettingListPreference;
import com.xiaomi.parts.preferences.SecureSettingSwitchPreference;
import com.xiaomi.parts.preferences.VibrationSeekBarPreference;
import com.xiaomi.parts.preferences.CustomSeekBarPreference;
import com.xiaomi.parts.ambient.AmbientGesturePreferenceActivity;
import com.xiaomi.parts.Fastcharge;
import com.xiaomi.parts.Touchboost;
import android.util.Log;

import com.xiaomi.parts.R;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "DeviceSettings";

    private static final String PREF_DEVICE_KCAL = "device_kcal";
private static final String AMBIENT_DISPLAY = "ambient_display_gestures";

    // Vibration override will use bool instead of integer
    public static final String PREF_VIBRATION_OVERRIDE = "vmax_override";
    public static final String PREF_VIBRATION_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,haptics@c000/leds/vibrator/vmax_override";

    public static final String PREF_VIBRATION_SYSTEM_STRENGTH = "vibration_system";
    public static final String PREF_VIBRATION_NOTIFICATION_STRENGTH = "vibration_notification";
    public static final String PREF_VIBRATION_CALL_STRENGTH = "vibration_call";
    public static final String VIBRATION_SYSTEM_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,haptics@c000/leds/vibrator/vmax_mv_user";
    public static final String VIBRATION_NOTIFICATION_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,haptics@c000/leds/vibrator/vmax_mv_strong";
    public static final String VIBRATION_CALL_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,haptics@c000/leds/vibrator/vmax_mv_call";

    // value of vtg_min and vtg_max
    public static final int MIN_VIBRATION = 116;
    public static final int MAX_VIBRATION = 3596;

    public static final String PREF_KEY_FPS_INFO = "fps_info";

    public static final String PREF_HEADPHONE_GAIN = "headphone_gain";
    public static final String HEADPHONE_GAIN_PATH = "/sys/kernel/sound_control/headphone_gain";
    public static final String PREF_MICROPHONE_GAIN = "microphone_gain";
    public static final String MICROPHONE_GAIN_PATH = "/sys/kernel/sound_control/mic_gain";
    public static final String PREF_SPEAKER_GAIN = "speaker_gain";
    public static final String SPEAKER_GAIN_PATH = "/sys/kernel/sound_control/speaker_gain";

    private static final String PREF_ENABLE_DIRAC = "dirac_enabled";
    private static final String PREF_HEADSET = "dirac_headset_pref";
    private static final String PREF_PRESET = "dirac_preset_pref";

    public static final String KEY_YELLOW_TORCH_BRIGHTNESS = "yellow_torch_brightness";
    public static final String KEY_WHITE_TORCH_BRIGHTNESS = "white_torch_brightness";
    public static final String TORCH_1_BRIGHTNESS_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,leds@d300/leds/led:torch_0/max_brightness";
    public static final String TORCH_2_BRIGHTNESS_PATH = "/sys/devices/platform/soc/200f000.qcom,spmi/spmi-0/spmi0-03/200f000.qcom,spmi:qcom,pmi8950@3:qcom,leds@d300/leds/led:torch_1/max_brightness";

    public static final String PREF_USB_FASTCHARGE = "fastcharge";
    public static final String USB_FASTCHARGE_PATH = "/sys/kernel/fast_charge/force_fast_charge";

    public static final String PREF_SPECTRUM = "spectrum";
    public static final String SPECTRUM_SYSTEM_PROPERTY = "persist.spectrum.profile";

    public static final String PREF_MSM_TOUCHBOOST = "touchboost";
    public static final String MSM_TOUCHBOOST_PATH = "/sys/module/msm_performance/parameters/touchboost";

    public static final String PREF_GPUBOOST = "gpuboost";
    public static final String GPUBOOST_SYSTEM_PROPERTY = "persist.gpuboost.profile";
    public static final String PREF_CPUBOOST = "cpuboost";
    public static final String CPUBOOST_SYSTEM_PROPERTY = "persist.cpuboost.profile";

    private static Context mContext;

    private CustomSeekBarPreference mHeadphoneGain;
    private CustomSeekBarPreference mMicrophoneGain;
    private CustomSeekBarPreference mSpeakerGain;

    private CustomSeekBarPreference mWhiteTorchBrightness;
    private CustomSeekBarPreference mYellowTorchBrightness;

    private SecureSettingListPreference mSPECTRUM;
    private SecureSettingSwitchPreference mTouchboost;
    private SecureSettingListPreference mGPUBOOST;
    private SecureSettingListPreference mCPUBOOST;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.xiaomi_main, rootKey);
        mContext = this.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        SecureSettingSwitchPreference vib = (SecureSettingSwitchPreference) findPreference(PREF_VIBRATION_OVERRIDE);
        vib.setEnabled(Vibration.isSupported());
        vib.setChecked(Vibration.isCurrentlyEnabled(this.getContext()));
        vib.setOnPreferenceChangeListener(new Vibration(getContext()));
        Preference ambientDisplay = findPreference(AMBIENT_DISPLAY);
        ambientDisplay.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), AmbientGesturePreferenceActivity.class);
            startActivity(intent);
            return true;
        });
        VibrationSeekBarPreference vibrationSystemStrength = (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_SYSTEM_STRENGTH);
        vibrationSystemStrength.setEnabled(FileUtils.fileWritable(VIBRATION_SYSTEM_PATH));
        vibrationSystemStrength.setOnPreferenceChangeListener(this);

        VibrationSeekBarPreference vibrationNotificationStrength = (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_NOTIFICATION_STRENGTH);
        vibrationNotificationStrength.setEnabled(FileUtils.fileWritable(VIBRATION_NOTIFICATION_PATH));
        vibrationNotificationStrength.setOnPreferenceChangeListener(this);

        VibrationSeekBarPreference vibrationCallStrength = (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_CALL_STRENGTH);
        vibrationCallStrength.setEnabled(FileUtils.fileWritable(VIBRATION_CALL_PATH));
        vibrationCallStrength.setOnPreferenceChangeListener(this);

        SwitchPreference fpsInfo = (SwitchPreference) findPreference(PREF_KEY_FPS_INFO);
        fpsInfo.setChecked(prefs.getBoolean(PREF_KEY_FPS_INFO, false));
        fpsInfo.setOnPreferenceChangeListener(this);

        boolean enhancerEnabled;
        try {
            enhancerEnabled = DiracService.sDiracUtils.isDiracEnabled();
        } catch (java.lang.NullPointerException e) {
            getContext().startService(new Intent(getContext(), DiracService.class));
            try {
                enhancerEnabled = DiracService.sDiracUtils.isDiracEnabled();
            } catch (NullPointerException ne) {
                // Avoid crash
                ne.printStackTrace();
                enhancerEnabled = false;
            }
        }

        SecureSettingSwitchPreference enableDirac = (SecureSettingSwitchPreference) findPreference(PREF_ENABLE_DIRAC);
        enableDirac.setOnPreferenceChangeListener(this);
        enableDirac.setChecked(enhancerEnabled);

        SecureSettingListPreference headsetType = (SecureSettingListPreference) findPreference(PREF_HEADSET);
        headsetType.setOnPreferenceChangeListener(this);

        SecureSettingListPreference preset = (SecureSettingListPreference) findPreference(PREF_PRESET);
        preset.setOnPreferenceChangeListener(this);


        Preference kcal = findPreference(PREF_DEVICE_KCAL);
        kcal.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), KCalSettingsActivity.class);
            startActivity(intent);
            return true;
        });

	// Sound Control
        mHeadphoneGain = (CustomSeekBarPreference) findPreference(PREF_HEADPHONE_GAIN);
        mHeadphoneGain.setOnPreferenceChangeListener(this);

        mMicrophoneGain = (CustomSeekBarPreference) findPreference(PREF_MICROPHONE_GAIN);
        mMicrophoneGain.setOnPreferenceChangeListener(this);

        mSpeakerGain = (CustomSeekBarPreference) findPreference(PREF_SPEAKER_GAIN);
        mSpeakerGain.setOnPreferenceChangeListener(this);

	// Torch
        mWhiteTorchBrightness = (CustomSeekBarPreference) findPreference(KEY_WHITE_TORCH_BRIGHTNESS);
        mWhiteTorchBrightness.setEnabled(FileUtils.fileWritable(TORCH_1_BRIGHTNESS_PATH));
        mWhiteTorchBrightness.setOnPreferenceChangeListener(this);

        mYellowTorchBrightness = (CustomSeekBarPreference) findPreference(KEY_YELLOW_TORCH_BRIGHTNESS);
        mYellowTorchBrightness.setEnabled(FileUtils.fileWritable(TORCH_2_BRIGHTNESS_PATH));
        mYellowTorchBrightness.setOnPreferenceChangeListener(this);

	// FASTCHARGE
        SwitchPreference usbfastCharger = (SwitchPreference) findPreference(PREF_USB_FASTCHARGE);
        usbfastCharger.setEnabled(FileUtils.fileWritable(USB_FASTCHARGE_PATH));
        usbfastCharger.setChecked(FileUtils.getFileValueAsBoolean(USB_FASTCHARGE_PATH, true));
        usbfastCharger.setOnPreferenceChangeListener(this);

	// SPECTRUM
        mSPECTRUM = (SecureSettingListPreference) findPreference(PREF_SPECTRUM);
        mSPECTRUM.setValue(FileUtils.getStringProp(SPECTRUM_SYSTEM_PROPERTY, "0"));
        mSPECTRUM.setSummary(mSPECTRUM.getEntry());
        mSPECTRUM.setOnPreferenceChangeListener(this);

	// BOOST
        if (FileUtils.fileWritable(MSM_TOUCHBOOST_PATH)) {
            mTouchboost = (SecureSettingSwitchPreference) findPreference(PREF_MSM_TOUCHBOOST);
            mTouchboost.setChecked(FileUtils.getFileValueAsBoolean(MSM_TOUCHBOOST_PATH, true));
            mTouchboost.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(PREF_MSM_TOUCHBOOST));
        }

        mGPUBOOST = (SecureSettingListPreference) findPreference(PREF_GPUBOOST);
        mGPUBOOST.setValue(FileUtils.getStringProp(GPUBOOST_SYSTEM_PROPERTY, "0"));
        mGPUBOOST.setSummary(mGPUBOOST.getEntry());
        mGPUBOOST.setOnPreferenceChangeListener(this);

        mCPUBOOST = (SecureSettingListPreference) findPreference(PREF_CPUBOOST);
        mCPUBOOST.setValue(FileUtils.getStringProp(CPUBOOST_SYSTEM_PROPERTY, "0"));
        mCPUBOOST.setSummary(mCPUBOOST.getEntry());
        mCPUBOOST.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {
            case KEY_WHITE_TORCH_BRIGHTNESS:
                FileUtils.setValue(TORCH_1_BRIGHTNESS_PATH, (int) value);
                break;

            case KEY_YELLOW_TORCH_BRIGHTNESS:
                FileUtils.setValue(TORCH_2_BRIGHTNESS_PATH, (int) value);
                break;

            case PREF_VIBRATION_SYSTEM_STRENGTH:
                double VibrationSystemValue = (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_SYSTEM_PATH, VibrationSystemValue);
                break;

            case PREF_VIBRATION_NOTIFICATION_STRENGTH:
                double VibrationNotificationValue = (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_NOTIFICATION_PATH, VibrationNotificationValue);
                break;

            case PREF_VIBRATION_CALL_STRENGTH:
                double VibrationCallValue = (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_CALL_PATH, VibrationCallValue);
                break;

            case PREF_HEADPHONE_GAIN:
                FileUtils.setValue(HEADPHONE_GAIN_PATH, value + " " + value);
                break;

            case PREF_MICROPHONE_GAIN:
                FileUtils.setValue(MICROPHONE_GAIN_PATH, (int) value);
                break;

            case PREF_SPEAKER_GAIN:
                FileUtils.setValue(SPEAKER_GAIN_PATH, (int) value);
                break;
            default:
                break;

            case PREF_ENABLE_DIRAC:
                try {
                    DiracService.sDiracUtils.setEnabled((boolean) value);
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setEnabled((boolean) value);
                }
                break;

            case PREF_HEADSET:
                try {
                    DiracService.sDiracUtils.setHeadsetType(Integer.parseInt(value.toString()));
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setHeadsetType(Integer.parseInt(value.toString()));
                }
                break;

            case PREF_PRESET:
                try {
                    DiracService.sDiracUtils.setLevel(String.valueOf(value));
                } catch (java.lang.NullPointerException e) {
                    getContext().startService(new Intent(getContext(), DiracService.class));
                    DiracService.sDiracUtils.setLevel(String.valueOf(value));
                }
                break;

            case PREF_KEY_FPS_INFO:
                boolean enabled = (Boolean) value;
                Intent fpsinfo = new Intent(this.getContext(), FPSInfoService.class);
                if (enabled) {
                    this.getContext().startService(fpsinfo);
                } else {
                    this.getContext().stopService(fpsinfo);
                }
                break;

            case PREF_USB_FASTCHARGE:
                FileUtils.setValue(USB_FASTCHARGE_PATH, (boolean) value);
                break;

            case PREF_MSM_TOUCHBOOST:
                FileUtils.setValue(MSM_TOUCHBOOST_PATH, (boolean) value);
                break;

            case PREF_SPECTRUM:
                mSPECTRUM.setValue((String) value);
                mSPECTRUM.setSummary(mSPECTRUM.getEntry());
                FileUtils.setStringProp(SPECTRUM_SYSTEM_PROPERTY, (String) value);
                break;

            case PREF_GPUBOOST:
                mGPUBOOST.setValue((String) value);
                mGPUBOOST.setSummary(mGPUBOOST.getEntry());
                FileUtils.setStringProp(GPUBOOST_SYSTEM_PROPERTY, (String) value);
                break;

            case PREF_CPUBOOST:
                mCPUBOOST.setValue((String) value);
                mCPUBOOST.setSummary(mCPUBOOST.getEntry());
                FileUtils.setStringProp(CPUBOOST_SYSTEM_PROPERTY, (String) value);
                break;
        }
        return true;
    }

}

