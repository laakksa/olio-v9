package com.olio.finnkinoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TheatreCollection tc;
    Spinner theatreSpinner;
    Theatre theatreSelected;
    EditText datePicker;
    String[] theatreNameList, movieArray;
    DatePickerDialog datePickerDialog;
    ListView listView;
    ArrayAdapter<String> movieArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        tc = TheatreCollection.getInstance();
        theatreSpinner = (Spinner) findViewById(R.id.theatreSelector);
        listView = (ListView) findViewById(R.id.listView);
        theatreNameList = tc.getTheatreList();
        ArrayAdapter<String> theatreArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, theatreNameList);
        theatreArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theatreSpinner.setAdapter(theatreArrayAdapter);
        theatreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theatreSelected = tc.theatreList.get(position);
                System.out.println(theatreSelected.getId());
                System.out.println("Date selected: " + datePicker.getText().toString());
                updateMovieList(theatreSelected, datePicker.getText().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                        //TODO updateList function here
                    }
                },mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
    public void updateMovieList(Theatre theatreSelected, String date){
        movieArray = tc.updateMovies(theatreSelected, date);
        movieArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, movieArray);
        movieArrayAdapter.notifyDataSetChanged();
        listView.setAdapter(movieArrayAdapter);
    }
}