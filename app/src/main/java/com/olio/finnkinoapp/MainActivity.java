package com.olio.finnkinoapp;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {
    TheatreCollection tc;
    Spinner theatreSpinner;
    Theatre theatreSelected;
    TextView text;
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
        //Create array of strings to put into spinner
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
                try {
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

    }

}