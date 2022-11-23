package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.LocaleListCompat;

import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kaasbrot.boehlersyugiohapp.GlobalOptions;
import com.kaasbrot.boehlersyugiohapp.MainActivity;
import com.kaasbrot.boehlersyugiohapp.R;

import java.util.Locale;

public class SettingsDialog extends AppCompatDialogFragment {

    private static final int POINTS_MIN = 1;
    private static final int POINTS_MAX = 40_000;

    private static final int MAX_PLAYER_NAME_LENGTH = 20;

    private String selectedLanguage = null;

    // these are needed for the onDestroy method
    private EditText startLifeText = null;
    private EditText player1nameText = null;
    private EditText player2nameText = null;
    private Spinner languageSelector = null;
    private MainActivity mainActivity = null;

    private boolean lifePointsKeyEvent(EditText field) {
        field.clearFocus();

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(field.getApplicationWindowToken(), 0);

        String startLifeTemp = field.getText().toString();
        Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);

        try {
            int startLifetempint = Integer.parseInt(startLifeTemp);
            toast.setGravity(Gravity.TOP, 0, 10);
            if (startLifetempint > POINTS_MAX) {
                toast.setText(R.string.set_lifepoint_max);
                toast.show();
            } else if (startLifetempint < POINTS_MIN) {
                toast.setText(R.string.set_lifepoint_min);
                toast.show();
            } else {
                /*
                 * Only place where startLifePoints is updated and saved in local storage
                 */
                GlobalOptions.setStartingLifePoints(startLifetempint);
            }
        } catch(NumberFormatException e) {
            toast.setText(R.string.bad_number_format);
        }

        field.setText(String.valueOf(GlobalOptions.getStartingLifePoints()));

        return true;
    }

    private boolean playerNameKeyEvent(TextView field, boolean isPlayer1) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        field.clearFocus();
        imm.hideSoftInputFromWindow(field.getApplicationWindowToken(), 0);

        String name = field.getText().toString();

        if(name.length() > MAX_PLAYER_NAME_LENGTH) {
            Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_LONG);
            toast.setText(R.string.playername2long);
            toast.show();
        } else if(isPlayer1) {
            GlobalOptions.setPlayerName1(player1nameText.getText().toString());
        } else {
            GlobalOptions.setPlayerName2(player2nameText.getText().toString());
        }

        if(mainActivity != null)
            mainActivity.updatePlayerNames();

        startLifeText.clearFocus();
        return true;
    }

    private void initLabel(View view, int id) {
        ((TextView) view.findViewById(id)).setTextSize(GlobalOptions.settingstextsize);
    }

    private EditText initTextView(View view, int id, String startValue, TextView.OnEditorActionListener l) {
        EditText field = view.findViewById(id);
        field.setTextSize(GlobalOptions.settingstextsize);
        field.setText(startValue, TextView.BufferType.EDITABLE);
        field.setSelectAllOnFocus(true);
        field.setOnEditorActionListener(l);
        return field;
    }

    /**
     * Get language currently set in app in short form (i.e. "en", "de", ...)
     * @return Language code, two-letters
     */
    private String getCurrentLanguage() {
        // who the hell designs this
        return getResources().getConfiguration().getLocales().get(0).getLanguage();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setMessage(R.string.settings);

        LayoutInflater inflater =  LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.settings_dialogx, null);
        builder.setView(view);

        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        builder.setCancelable(true);

        /*
         * Labels
         */
        initLabel(view, R.id.settingsPointsLabel);
        initLabel(view, R.id.settingsScreenOnLabel);
        initLabel(view, R.id.settingsDeleteAfter4Label);
        initLabel(view, R.id.settingsShowNamesLabel);
        initLabel(view, R.id.settingsPlayerName1Label);
        initLabel(view, R.id.settingsPlayerName2Label);
        initLabel(view, R.id.settingsLanguageLabel);

        /*
         * TextViews
         */
        this.startLifeText = initTextView(
                view, R.id.settingsPointsField,
                String.valueOf(GlobalOptions.getStartingLifePoints()),
                (v, actionId, event) -> lifePointsKeyEvent(startLifeText)
        );
        this.player1nameText = initTextView(
                view, R.id.PlayerName1, GlobalOptions.getPlayerName1(),
                (v, actionId, event) -> playerNameKeyEvent(player1nameText, true)
        );
        this.player2nameText = initTextView(
                view, R.id.PlayerName2, GlobalOptions.getPlayerName2(),
                (v, actionId, event) -> playerNameKeyEvent(player2nameText, false)
        );

        /*
         * Toggles
         */
        ImageView img = view.findViewById(R.id.settingsScreenOnImage);
        img.setImageResource(GlobalOptions.isScreenAlwaysOn() ? R.drawable.tick1 : R.drawable.tick0);

        img = view.findViewById(R.id.settingsDeleteAfter4Image);
        img.setImageResource(GlobalOptions.isDeleteAfter4() ? R.drawable.tick1 : R.drawable.tick0);

        img = view.findViewById(R.id.settingsShowNamesImage);
        img.setImageResource(GlobalOptions.isShowNames() ? R.drawable.tick1 : R.drawable.tick0);

        /*
         * Language selection
         */
        this.languageSelector = view.findViewById(R.id.settingsLanguageSelector);

        // the order of this array is critical I tell you
        // Engrish: index 0
        // Germansh: index 1
        String[] languages = new String[]{
                "Engrish",
                "Germansch"
        };
        languageSelector.setAdapter(new ArrayAdapter<>(view.getContext(), R.layout.language_spinner, languages));
        switch(getCurrentLanguage()) {
            case "en":
                languageSelector.setSelection(0);
                break;
            case "de":
                languageSelector.setSelection(1);
                break;
            default:
                throw new RuntimeException("Invalid language selection");
        }

        languageSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selection = (TextView) languageSelector.getChildAt(0);
                if(selection != null)
                    selection.setTextSize(GlobalOptions.settingstextsize);

                String result = "";
                Locale locale = null;
                switch(position) {
                    case 0:
                        result = "en";
                        locale = Locale.ENGLISH;
                        break;
                    case 1:
                        result = "de";
                        locale = Locale.GERMAN;
                        break;
                    default:
                        throw new RuntimeException("Invalid language selection");
                }

                if(result.equals(getCurrentLanguage()))
                    selectedLanguage = null;
                else {
                    selectedLanguage = result;

                    Resources standardResources = getResources();
                    Configuration config = new Configuration(standardResources.getConfiguration());
                    config.setLocale(locale);
                    Context ctx = getDialog().getContext().createConfigurationContext(config);

                    Toast toast = Toast.makeText(getContext(), ctx.getText(R.string.languageInfo), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 10);
                    toast.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("bbbbb");
            }
        });

        /*
         * öhm... rest
         */
        builder.setPositiveButton("ok", (dialogInterface, i) -> {});

        view.setOnKeyListener((view1, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                startLifeText.clearFocus();
                player1nameText.clearFocus();
                player2nameText.clearFocus();
            }
            return true;
        });

        return builder.create();
    }

    private void updateImageToggle(int id, boolean val) {
        ImageView img = this.getDialog().findViewById(id);
        img.setImageResource(val ? R.drawable.tick1 : R.drawable.tick0);
    }

    public void toggleScreenAlwaysOn() {
        GlobalOptions.setScreenAlwaysOn(!GlobalOptions.isScreenAlwaysOn());
        updateImageToggle(R.id.settingsScreenOnImage, GlobalOptions.isScreenAlwaysOn());
    }

    public void toggleDeleteAfter4Games() {
        GlobalOptions.setDeleteAfter4(!GlobalOptions.isDeleteAfter4());
        updateImageToggle(R.id.settingsDeleteAfter4Image, GlobalOptions.isDeleteAfter4());
    }

    public void toggleShowNames() {
        GlobalOptions.setShowNames(!GlobalOptions.isShowNames());
        updateImageToggle(R.id.settingsShowNamesImage, GlobalOptions.isShowNames());
    }

    @Override
    public void onStart() {
        // once the dialog is built and shown, we can adjust text size and font alignment
        super.onStart();
        TextView texts = this.getDialog().findViewById(android.R.id.message);
        texts.setTextSize(30);
        texts.setGravity(Gravity.CENTER);
//        ((TextView) languageSelector.getChildAt(0)).setTextColor(getContext().getColor(R.color.dial_text));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            int startPoints = Integer.parseInt(startLifeText.getText().toString());
            if(POINTS_MIN <= startPoints && startPoints <= POINTS_MAX)
                GlobalOptions.setStartingLifePoints(startPoints);
        } catch(Exception ignored) {}

        try {
            String player1 = player1nameText.getText().toString();
            GlobalOptions.setPlayerName1(player1);
        } catch(Exception ignored) {}

        try {
            String player2 = player2nameText.getText().toString();
            GlobalOptions.setPlayerName2(player2);
        } catch(Exception ignored) {}

        if(mainActivity != null)
            mainActivity.updatePlayerNames();

        startLifeText = null;
        player1nameText = null;
        player2nameText = null;

        try {
            if(selectedLanguage != null) {
                LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(selectedLanguage);
                selectedLanguage = null;
                AppCompatDelegate.setApplicationLocales(appLocale);
            }
        } catch(Exception ignored) {}
    }

    public void setMainActivity(MainActivity act) {
        this.mainActivity = act;
    }
}
