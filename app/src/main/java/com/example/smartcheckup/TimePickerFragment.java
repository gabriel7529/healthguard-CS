package com.example.smartcheckup;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.provider.CalendarContract;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Calendar c=Calendar.getInstance();
        int h=c.get(Calendar.HOUR_OF_DAY);
        int m=c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),R.style.TimePickerTheme,(TimePickerDialog.OnTimeSetListener)getActivity(),
                h,m,true);
    }
}
