package com.olio.finnkinoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    TheatreCollection tc;
    Spinner theatreSpinner;
    Theatre theatreSelected;
    EditText datePicker, searchStartTime, searchEndTime;
    String[] theatreNameList;
    DatePickerDialog datePickerDialog;
    TimePickerDialog startTimePicker, endTimePicker;
    ListView listView;
    ArrayAdapter<String> movieArrayAdapter;
    Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        tc = TheatreCollection.getInstance();
        theatreSpinner = (Spinner) findViewById(R.id.theatreSelector);
        listView = (ListView) findViewById(R.id.listView);
        searchStartTime = (EditText) findViewById(R.id.searchStartTime);
        searchEndTime = (EditText) findViewById(R.id.searchEndTime);

        //Initialize spinner for theatres
        theatreNameList = tc.getTheatreList();
        ArrayAdapter<String> theatreArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, theatreNameList);
        theatreArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theatreSpinner.setAdapter(theatreArrayAdapter);
        theatreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theatreSelected = tc.theatreList.get(position);
                updateMovieList(theatreSelected, datePicker.getText().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                theatreSelected = tc.theatreList.get(0);
                updateMovieList(theatreSelected, "");
            }
        });


        //Initialize datePickerDialog when clicking on editText
        datePicker = (EditText) findViewById(R.id.datePicker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datePicker.setText(String.format("%02d.%02d.%04d", dayOfMonth, (month + 1), year));
                        updateMovieList(theatreSelected, datePicker.getText().toString());
                    }
                },mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //Open TimePickerDialog when clicking on time editTexts
        searchStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour = 0, mMin = 0;
                startTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        searchStartTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, mHour, mMin, true);
                startTimePicker.show();
            }
        });

        searchEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour = 0, mMin = 0;
                startTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        searchEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, mHour, mMin, true);
                startTimePicker.show();
            }
        });

        //Update movies on button click
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMovieList(theatreSelected, datePicker.getText().toString());
            }
        });

    }

    //Updates ListView with parameters given
    public void updateMovieList(Theatre theatreSelected, String date){
        String start = searchStartTime.getText().toString();
        String end = searchEndTime.getText().toString();
        ArrayList<String> movieArray = tc.updateMovies(theatreSelected, date, start, end);
        movieArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, movieArray);
        movieArrayAdapter.notifyDataSetChanged();
        listView.setAdapter(movieArrayAdapter);
    }
}