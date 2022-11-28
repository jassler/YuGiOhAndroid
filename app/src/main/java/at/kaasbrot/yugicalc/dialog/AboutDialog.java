package at.kaasbrot.yugicalc.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import at.kaasbrot.yugicalc.BuildConfig;
import at.kaasbrot.yugicalc.R;


public class AboutDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setMessage(R.string.app_name);

        LayoutInflater inflater =  LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.about_us, null);
        builder.setView(view);

        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        builder.setCancelable(true);

        ((TextView) view.findViewById(R.id.aboutVersion)).setText("v" + BuildConfig.VERSION_NAME);
        TextView email = (TextView) view.findViewById(R.id.about_us_email);
        email.setMovementMethod(LinkMovementMethod.getInstance());
        String content = "<a href=\"mailto:yugicalcapp@gmail.com\">yugicalcapp@gmail.com</a>";
        Spannable s = (Spannable) Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT);
        for (URLSpan u: s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        email.setText(s);

        TextView coffee = (TextView) view.findViewById(R.id.about_us_coffee_text);
        coffee.setMovementMethod(LinkMovementMethod.getInstance());

        builder.setPositiveButton("ok", (dialogInterface, i) -> {});

        return builder.create();
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
    }
}
