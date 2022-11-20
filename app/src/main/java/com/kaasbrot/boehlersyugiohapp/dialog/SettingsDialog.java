package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaasbrot.boehlersyugiohapp.GlobalOptions;
import com.kaasbrot.boehlersyugiohapp.R;

public class SettingsDialog extends AppCompatDialogFragment {

    private static final int POINTS_MIN = 1;
    private static final int POINTS_MAX = 40_000;

    private EditText startLifeText = null;

    private boolean keyEvent(EditText startLifeText) {
        startLifeText.clearFocus();

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(startLifeText.getApplicationWindowToken(), 0);

        String startLifeTemp = startLifeText.getText().toString();
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

        startLifeText.setText(String.valueOf(GlobalOptions.getStartingLifePoints()));

        return true;
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

//        ((TextView) view.findViewById(R.id.settingsok)).setTextSize(GlobalOptions.settingstextsize);
//        ((TextView) view.findViewById(R.id.StartLifeText)).setTextSize(GlobalOptions.settingstextsize);
//        ((TextView) view.findViewById(R.id.KeepScreenOnText)).setTextSize(GlobalOptions.settingstextsize);
//        ((TextView) view.findViewById(R.id.KeepHistoryText)).setTextSize(GlobalOptions.settingstextsize);
//        ((TextView) view.findViewById(R.id.BehindEdit)).setTextSize(GlobalOptions.settingstextsize);
//        ((EditText) view.findViewById(R.id.StartLifeInput)).setTextSize(GlobalOptions.settingstextsize);

        ((TextView) view.findViewById(R.id.settingsPointsLabel)).setTextSize(GlobalOptions.settingstextsize);
        ((TextView) view.findViewById(R.id.settingsScreenOnLabel)).setTextSize(GlobalOptions.settingstextsize);
        ((TextView) view.findViewById(R.id.settingsDeleteAfter4Label)).setTextSize(GlobalOptions.settingstextsize);
        ((TextView) view.findViewById(R.id.settingsShowNamesLabel)).setTextSize(GlobalOptions.settingstextsize);

        EditText pointsField = view.findViewById(R.id.settingsPointsField);
        pointsField.setTextSize(GlobalOptions.settingstextsize);
        pointsField.setText(String.valueOf(GlobalOptions.getStartingLifePoints()), TextView.BufferType.EDITABLE);
        pointsField.setSelectAllOnFocus(true);
        pointsField.setOnEditorActionListener((v, actionId, event) -> keyEvent(startLifeText));
        this.startLifeText = pointsField;

//        startLifeText = view.findViewById(R.id.StartLifeInput);
//        startLifeText.setText(String.valueOf(GlobalOptions.getStartingLifePoints()), TextView.BufferType.EDITABLE);
//        startLifeText.setSelectAllOnFocus(true);
//        startLifeText.setOnEditorActionListener((v, actionId, event) -> keyEvent(startLifeText));

        ImageView img = view.findViewById(R.id.settingsScreenOnImage);
        img.setImageResource(GlobalOptions.isScreenAlwaysOn() ? R.drawable.tick1 : R.drawable.tick0);

        img = view.findViewById(R.id.settingsDeleteAfter4Image);
        img.setImageResource(GlobalOptions.isDeleteAfter4() ? R.drawable.tick1 : R.drawable.tick0);

        builder.setPositiveButton("ok", (dialogInterface, i) -> {});

        view.setOnKeyListener((view1, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                startLifeText.clearFocus();
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
        GlobalOptions.setDeleteAfter4(!GlobalOptions.isDeleteAfter4());
        updateImageToggle(R.id.settingsShowNamesImage, GlobalOptions.isDeleteAfter4());
    }

    @Override
    public void onStart() {
        // once the dialog is built and shown, we can adjust text size and font alignment
        super.onStart();
        TextView texts = this.getDialog().findViewById(android.R.id.message);
        texts.setTextSize(30);
        texts.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            int startPoints = Integer.parseInt(startLifeText.getText().toString());
            if(POINTS_MIN <= startPoints && startPoints <= POINTS_MAX)
                GlobalOptions.setStartingLifePoints(startPoints);

        } catch(Exception ignored) {}

        startLifeText = null;
    }
}
