package com.nasageek.utexasutilities.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.nasageek.utexasutilities.PasswordEditTextPreferenceDialogFragmentCompat;
import com.nasageek.utexasutilities.R;
import com.nasageek.utexasutilities.SecurePreferences;
import com.nasageek.utexasutilities.UTilitiesApplication;
import com.nasageek.utexasutilities.Utility;
import com.nasageek.utexasutilities.activities.AboutMeActivity;

import java.io.IOException;

import static com.nasageek.utexasutilities.UTilitiesApplication.PNA_AUTH_COOKIE_KEY;
import static com.nasageek.utexasutilities.UTilitiesApplication.UTD_AUTH_COOKIE_KEY;

/**
 * Created by chris on 9/3/15.
 */
public class UTilitiesPreferenceFragment extends PreferenceFragmentCompat {

    private Preference loginfield;
    private Preference passwordfield;
    private CheckBoxPreference autologin;
    private RecyclerView.Adapter ba;
    private SecurePreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = new SecurePreferences(getActivity(), "com.nasageek.utexasutilities.password", false);
        autologin = (CheckBoxPreference) findPreference("autologin");
        loginfield = findPreference("eid");
        passwordfield = findPreference("password");

        // bypass the default SharedPreferences and save the password to the
        // encrypted SP instead
        passwordfield.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                sp.put(preference.getKey(), (String) newValue);
                return false;
            }
        });

        final Preference logincheckbox = findPreference(getString(R.string.pref_logintype_key));

        // TODO: figure out why this is here, was it related to the old Login
        // Pref stuff?
        logincheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue == false) {
                    autologin.setChecked(false);
                }
                return true;
            }
        });

        logincheckbox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                if (((CheckBoxPreference) preference).isChecked()) {
                    AlertDialog.Builder nologin_builder = new AlertDialog.Builder(getActivity());
                    nologin_builder
                            .setMessage(
                                    "NOTE: This will save your UT credentials to your device! If that worries you, "
                                            + "uncheck this preference and go tap one of the buttons on the main screen to log in. See "
                                            + "the Privacy Policy on the About page for more information.")
                            .setCancelable(true)
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog nologin = nologin_builder.create();
                    nologin.show();
                } else {
                    /*
                     * if they switch to temp login we'll save their EID, but
                     * clear their password for security purposes
                     */
                    sp.removeValue("password");
                    ba.notifyDataSetChanged();
                }
                // whenever they switch between temp and persistent, log them out
                UTilitiesApplication mApp = (UTilitiesApplication) getActivity().getApplication();
                mApp.logoutAll();
                return true;
            }
        });

        setupLoginFields();

        final CheckBoxPreference analytics =
                (CheckBoxPreference) findPreference(getString(R.string.pref_analytics_key));
        analytics.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                GoogleAnalytics.getInstance(getActivity()).setAppOptOut(!((Boolean) newValue));
                return true;
            }
        });

        final Preference about = findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Intent about_intent = new Intent(getActivity(), AboutMeActivity.class);
                startActivity(about_intent);
                return true;
            }
        });
        final Preference updateBusStops = findPreference("update_stops");
        updateBusStops.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Utility.updateBusStops(getActivity());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Stops could not be written to file.",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ba = getListView().getAdapter();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    private void setupLoginFields() {
        // disable the EID and password preferences if the user is logged in
        if (isUserLoggedIn()) {
            loginfield.setEnabled(false);
            passwordfield.setEnabled(false);
        } else {
            loginfield.setEnabled(true);
            passwordfield.setEnabled(true);
        }
    }

    private boolean isUserLoggedIn() {
        UTilitiesApplication mApp = (UTilitiesApplication) getActivity().getApplication();
        return mApp.getAuthCookie(UTD_AUTH_COOKIE_KEY).hasCookieBeenSet() &&
                mApp.getAuthCookie(PNA_AUTH_COOKIE_KEY).hasCookieBeenSet();
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        final String DIALOG_FRAGMENT_TAG =
                "android.support.v7.preference.PreferenceFragment.DIALOG";
        if (preference.getKey().equals("password")) {
            DialogFragment f = PasswordEditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }  else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
