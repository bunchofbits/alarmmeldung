package de.richter.alarmmeldung;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class SmsTextWatcher implements TextWatcher {

    private TextView show_remain_chars_vw = null;
    public SmsTextWatcher(TextView show_remain_chars_vw){
        super();
        this.show_remain_chars_vw = show_remain_chars_vw;
    }

    public String recalculate_remaining_chars(Editable editable){
        String text = editable.toString();
        int sms_cnt = (text.length() / 160) + 1;
        int remaining_chars = (160*sms_cnt) - text.length();
        return "" + remaining_chars + "/" + (160*sms_cnt) + "(" + sms_cnt + ")";
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        show_remain_chars_vw.setText(recalculate_remaining_chars(editable));
    }
}
