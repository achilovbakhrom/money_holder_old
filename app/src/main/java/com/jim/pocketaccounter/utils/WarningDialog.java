package com.jim.pocketaccounter.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.jim.pocketaccounter.R;

/**
 * Created by DEV on 29.08.2016.
 */

public class WarningDialog extends Dialog {
    private TextView tv;
    View dialogView;
    public WarningDialog(Context context) {
        super(context);
        dialogView = getLayoutInflater().inflate(R.layout.warning_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
        tv = ((TextView) dialogView.findViewById(R.id.tvWarningText));
        tv.setText(context.getResources().getString(R.string.currency_delete_warning));
    }

    public WarningDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WarningDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setText(String text) {
        tv.setText(text);
    }
    public void setOnYesButtonListener(View.OnClickListener yesButtonClickListener) {
        dialogView.findViewById(R.id.btnWarningYes).setOnClickListener(yesButtonClickListener);
    }
    public void setOnNoButtonClickListener(View.OnClickListener noButtonClickListener) {
        dialogView.findViewById(R.id.btnWarningNo).setOnClickListener(noButtonClickListener);
    }
}
