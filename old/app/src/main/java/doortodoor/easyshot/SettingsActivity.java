package doortodoor.easyshot;


import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

//        getFragmentManager().beginTransaction().replace(android.R.id.content,
//                new GeneralPreferenceFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        private static final int MANAGE_ASSIST = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_6);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            Preference.OnPreferenceClickListener pref6AssistOnOffListener = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (preference.getKey().equals("6_assist_on_off"))
                        Toast.makeText(getActivity(), "hi", Toast.LENGTH_SHORT).show();
                    return false;
                }
            };
            Preference.OnPreferenceChangeListener pref6AssistOnOffChangeListener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String stringValue = newValue.toString();
                    Toast.makeText(getActivity(), stringValue, Toast.LENGTH_SHORT).show();
                    if (preference.getKey().equals("6_assist_on_off") && stringValue.equals("true")) {
                        String curServiceComponent = Settings.Secure.getString(getActivity().getContentResolver(),
                                "assistant");
                        String serviceComponent = "doortodoor.easyshot" + "/" + ".over_lollipop.assistant.AssistService";
                        if (!serviceComponent.equals(curServiceComponent)) {
                            Toast.makeText(getActivity(), "wrong", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$ManageAssistActivity"));
                            startActivityForResult(intent, MANAGE_ASSIST);
                        }
                    }
                    return true;
                }
            };
            Preference.OnPreferenceChangeListener pref6AccessibilityOnOffChangeListener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String stringValue = newValue.toString();
                    Toast.makeText(getActivity(), stringValue, Toast.LENGTH_SHORT).show();
                    if (preference.getKey().equals("6_accessibility_on_off") && stringValue.equals("true")) {
                        if (!isAccessibilityEnabled()) {
                            Toast.makeText(getActivity(), "wrong", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$AccessibilitySettingsActivity"));
                            startActivityForResult(intent, MANAGE_ASSIST);
                        }
                    }
                    return true;
                }
            };
            findPreference("6_assist_on_off").setOnPreferenceClickListener(pref6AssistOnOffListener);
            findPreference("6_assist_on_off").setOnPreferenceChangeListener(pref6AssistOnOffChangeListener);

            findPreference("6_accessibility_on_off").setOnPreferenceChangeListener(pref6AccessibilityOnOffChangeListener);
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public void onResume() {
            super.onResume();
            // Make sure the request was successful
            String curServiceComponent = Settings.Secure.getString(getActivity().getContentResolver(),
                    "assistant");
            String serviceComponent = "doortodoor.easyshot" + "/" + ".over_lollipop.assistant.AssistService";
            boolean isAssist = serviceComponent.equals(curServiceComponent);
            synchronizeServicePreferences(isAssist, "6_assist_on_off");

            boolean isAccessibility = isAccessibilityEnabled();
            synchronizeServicePreferences(isAccessibility, "6_accessibility_on_off");
        }

        public boolean isAccessibilityEnabled() {
            int accessibilityEnabled = 0;
            final String ACCESSIBILITY_SERVICE_NAME = "doortodoor.easyshot/doortodoor.easyshot.MyAccessibilityService";
            boolean accessibilityFound = false;
            try {
                accessibilityEnabled = Settings.Secure.getInt(getActivity().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
                Log.d("settings", "ACCESSIBILITY: " + accessibilityEnabled);
            } catch (Settings.SettingNotFoundException e) {
                Log.d("settings", "Error finding setting, default accessibility to not found: " + e.getMessage());
            }

            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

            if (accessibilityEnabled == 1) {
                Log.d("settings", "***ACCESSIBILIY IS ENABLED***: ");


                String settingValue = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                Log.d("settings", "Setting: " + settingValue);
                if (settingValue != null) {
                    TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                    splitter.setString(settingValue);
                    while (splitter.hasNext()) {
                        String accessabilityService = splitter.next();
                        Log.d("settings", "Setting: " + accessabilityService);
                        if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)) {
                            Log.d("settings", "We've found the correct setting - accessibility is switched on!");
                            return true;
                        }
                    }
                }

                Log.d("settings", "***END***");
            } else {
                Log.d("settings", "***ACCESSIBILIY IS DISABLED***");
            }
            return accessibilityFound;
        }

        public void synchronizeServicePreferences(boolean check, String key) {
//            String curServiceComponent = Settings.Secure.getString(getActivity().getContentResolver(),
//                    settingsType);
//            String serviceComponent = "doortodoor.easyshot" + "/" + ".over_lollipop.assistant.AssistService";
            if (!check) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
                prefEditor.putBoolean(key, false); // set your default value here (could be empty as well)
                prefEditor.commit(); // finally save changes
                getActivity().getWindow().getDecorView().invalidate();
                onCreate(null);
            } else {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
                prefEditor.putBoolean(key, true); // set your default value here (could be empty as well)
                prefEditor.commit(); // finally save changes
                getActivity().getWindow().getDecorView().invalidate();
                onCreate(null);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

//        @Override
//        public void onActivityResult(int requestCode, int resultCode, Intent data) {
//            // Check which request we're responding to
//            if (requestCode == MANAGE_ASSIST) {
//                // Make sure the request was successful
//                String curServiceComponent = Settings.Secure.getString(getActivity().getContentResolver(),
//                        "assistant");
//                String serviceComponent = "doortodoor.easyshot" + "/" + ".over_lollipop.assistant.AssistService";
//                if (!curServiceComponent.equals(serviceComponent)) {
//                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                    SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
//                    prefEditor.putBoolean("6_assist_on_off", false); // set your default value here (could be empty as well)
//                    prefEditor.commit(); // finally save changes
//                    getActivity().getWindow().getDecorView().invalidate();
//                    onCreate(null);
//                }
//            }
//        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
