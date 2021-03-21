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
    DatePickerDialog datePickerDialog;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        tc = TheatreCollection.getInstance();
        theatreSpinner = (Spinner) findViewById(R.id.theatreSelector);
        listView = (ListView) findViewById(R.id.listView);
        String [] theatreNameList = new String[tc.theatreList.size()];
        //Create array of strings to put into spinner TODO move this to TheatreCollection
        for (int i = 0;i<tc.theatreList.size();i++){
            theatreNameList[i] = tc.theatreList.get(i).getLocation();
        }
        ArrayAdapter<String> theatreArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, theatreNameList);
        theatreArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theatreSpinner.setAdapter(theatreArrayAdapter);
        theatreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theatreSelected = tc.theatreList.get(position);
                System.out.println(theatreSelected.getId());
                try { //TODO move this to TheatreCollection
                    System.out.println(tc.scheduleURLBuilder(theatreSelected.getId(), null).toString());
                    ArrayList<Movie> moviesList = tc.parseScheduleXML(tc.readXML(tc.scheduleURLBuilder(theatreSelected.getId(), null)));
                    String [] movieArray = new String[moviesList.size()];
                    for (int i=0; i<moviesList.size();i++){
                        StringBuilder sb = new StringBuilder();
                        sb.append(moviesList.get(i).getTitle());
                        sb.append("\t\t");
                        sb.append(String.format("%s-%s", moviesList.get(i).getStartTime(), moviesList.get(i).getEndTime()));
                        movieArray[i] = sb.toString();
                    }
                    ArrayAdapter<String> movieArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, movieArray);
                    movieArrayAdapter.notifyDataSetChanged();
                    listView.setAdapter(movieArrayAdapter);
                } catch (XmlPullParserException e){

                } catch (IOException e){

                }

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
                        datePicker.setText(dayOfMonth + "." + (month + 1) + "." + year);
                        //TODO updateList function here
                    }
                },mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

}