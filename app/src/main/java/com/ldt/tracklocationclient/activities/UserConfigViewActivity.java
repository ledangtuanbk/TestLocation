package com.ldt.tracklocationclient.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.utilities.DateHelper;
import com.ldt.tracklocationclient.utilities.DateTimeFormat;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserConfigViewActivity extends AppCompatActivity {

    private static final String TAG = UserConfigViewActivity.class.getSimpleName();
    long startTime = 0;
    long endTime = Long.MAX_VALUE;

    Calendar calendarStart = Calendar.getInstance();
    Calendar calendarEnd = Calendar.getInstance();

    @BindView(R.id.etStart)
    EditText etStartTime;

    @BindView(R.id.etEnd)
    EditText etEndTime;

    @BindView(R.id.tvName)
    TextView tvName;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config_view);
        ButterKnife.bind(this);
        userId = getIntent().getStringExtra(getString(R.string.userId));
        tvName.setText(userId);
        calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.HOUR,-1);
        calendarEnd = Calendar.getInstance();
        etStartTime.setText(DateHelper.dateToString(calendarStart, DateTimeFormat.DateTime));
        etEndTime.setText(DateHelper.dateToString(calendarEnd, DateTimeFormat.DateTime));
    }


    @OnClick(R.id.etStart)
    public void startClick(View view) {
        Log.d(TAG, "startClick: " + view.getId());
        datePicker(view);
    }

    @OnClick(R.id.etEnd)
    public void endClick(View view) {
        Log.d(TAG, "endClick: " + view.getId());
        datePicker(view);
    }

    @OnClick(R.id.btnViewLocation)
    public void viewLocation(){
        Intent intent = new Intent(this, LocationViewer.class);
        intent.putExtra(getString(R.string.userId), userId);
        intent.putExtra(getString(R.string.startTime), calendarStart.getTimeInMillis());
        intent.putExtra(getString(R.string.endTime), calendarEnd.getTimeInMillis());
        startActivity(intent);
    }

    private void datePicker(final View etView) {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        if (etView.getId() == R.id.etStart) {
                            calendarStart.set(Calendar.YEAR, year);
                            calendarStart.set(Calendar.MONTH, monthOfYear);
                            calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        } else {
                            calendarEnd.set(Calendar.YEAR, year);
                            calendarEnd.set(Calendar.MONTH, monthOfYear);
                            calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        }
                        timePicker(etView);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker(final View etView) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (etView.getId() == R.id.etStart) {
                            calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendarStart.set(Calendar.MINUTE, minute);
                            startTime = calendarStart.getTimeInMillis();
                            etStartTime.setText(DateHelper.dateToString(calendarStart, DateTimeFormat.DateTime));
                        } else {
                            calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendarEnd.set(Calendar.MINUTE, minute);
                            endTime = calendarEnd.getTimeInMillis();
                            etEndTime.setText(DateHelper.dateToString(calendarEnd, DateTimeFormat.DateTime));
                        }

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}
