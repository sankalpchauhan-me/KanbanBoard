package me.sankalpchauhan.kanbanboard.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.sankalpchauhan.kanbanboard.R;
import me.sankalpchauhan.kanbanboard.view.CardActivity;

public class DateTimePickerDialog extends DialogFragment {
    boolean timePicked=false, datepicked=false;
    TextView mDatePicker, mTimePickerTV;
    Button mCancel, mDone;
    final Calendar myCalendar = Calendar.getInstance();
    int sHour, sMinute, sDay, sYear, sMonth;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_date_time_picker, container, false);
        mDatePicker = v.findViewById(R.id.date_picker);
        mTimePickerTV = v.findViewById(R.id.time_picker);
        mCancel = v.findViewById(R.id.cancel);
        mDone = v.findViewById(R.id.done);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                sYear = year;
                sMonth = monthOfYear;
                sDay = dayOfMonth;
                updateLabel();
                datepicked = true;

            }
        };

        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mTimePickerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mTimePickerTV.setText(String.format("%d:%d", selectedHour, selectedMinute));
                        timePicked=true;
                        sHour = selectedHour;
                        sMinute = selectedMinute;
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(datepicked&&timePicked){
                    Activity act = getActivity();
                    if (act instanceof CardActivity) {
                        ((CardActivity) act).formatDateForDB(sDay, sMonth, sYear, sHour, sMinute, 0);
                        dismiss();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Please Select Date & Time", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return v;
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mDatePicker.setText(sdf.format(myCalendar.getTime()));
    }
}
