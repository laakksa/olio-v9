package com.olio.finnkinoapp;

import android.util.Log;
import android.widget.ArrayAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TheatreCollection {
    private static TheatreCollection tc_instance = null;
    ArrayList<Theatre> theatreList;

    //Initializes theatre list
    private TheatreCollection() {
        try {
            URL theatreAreasURL = new URL("https://www.finnkino.fi/xml/TheatreAreas/");
            theatreList = parseAreasXML(readXML(theatreAreasURL));

        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", "Malformed URL");
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException", "Faulty XmlPullParser");
        } catch (IOException e) {
            Log.e("IOException", "Faulty input");
        }
    }

    //Singleton pattern
    public static TheatreCollection getInstance() {
        if (tc_instance == null) {
            tc_instance = new TheatreCollection();
        }
        return tc_instance;
    }

    //Reads XML file from URL and returns XmlPullParser
    public XmlPullParser readXML(URL xmlURL) throws XmlPullParserException, IOException {
        XmlPullParserFactory parserFactory;
        parserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = parserFactory.newPullParser();
        InputStream is = xmlURL.openConnection().getInputStream();
        parser.setInput(is, "UTF-8");
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        return parser;


    }

    //Parses Xml file containing areas
    private ArrayList<Theatre> parseAreasXML(XmlPullParser parser) throws IOException, XmlPullParserException {
        theatreList = new ArrayList<>();
        int eventType = parser.getEventType();
        Theatre currentTheatre = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String elementName = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    if (elementName.equals("TheatreArea")) {
                        currentTheatre = new Theatre();
                        theatreList.add(currentTheatre);
                    } else if (currentTheatre != null) {
                        if (elementName.equals("ID")) {
                            currentTheatre.id = Integer.parseInt(parser.nextText());
                        } else if (elementName.equals("Name")) {
                            currentTheatre.location = parser.nextText();
                        }
                    }
                    break;

            }
            eventType = parser.next();
        }
        return theatreList;
    }

    //Parses XML file containing schedule for selected theatre
    public ArrayList<Movie> parseScheduleXML(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<Movie> moviesList = new ArrayList<>();
        int eventType = parser.getEventType();
        Movie currentMovie = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String elementName = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    if (elementName.equals("Show")) {
                        currentMovie = new Movie();
                        moviesList.add(currentMovie);
                    } else if (currentMovie != null) {
                        if (elementName.equals("ID")) {
                            currentMovie.setEventID(parser.nextText());
                        } else if (elementName.equals("dttmShowStart")) {
                            currentMovie.setStartTime(parser.nextText());
                        } else if (elementName.equals("dttmShowEnd")) {
                            currentMovie.setEndTime(parser.nextText());
                        } else if (elementName.equals("Title")) {
                            currentMovie.setTitle(parser.nextText());
                        } else if (elementName.equals("ProductionYear")) {
                            currentMovie.setProdYear(parser.nextText());
                        } else if (elementName.equals("LengthInMinutes")) {
                            currentMovie.setLengthMin(parser.nextText());
                        }
                    }
                    break;

            }
            eventType = parser.next();
        }
        return moviesList;
    }

    //Creates URL for schedule from parameters
    public URL scheduleURLBuilder(int id, String d) throws MalformedURLException {
        String date = "";
        if (d.isEmpty()) {
            date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        } else {
            date = d;
        }
        String urlString = "https://www.finnkino.fi/xml/Schedule/?area=" + id + "&dt=" + date;
        URL url = new URL(urlString);
        return url;
    }


    //Create String array from theatre names
    public String[] getTheatreList() {
        String[] theatreNameList = new String[theatreList.size()];
        for (int i = 0; i < theatreList.size(); i++) {
            theatreNameList[i] = theatreList.get(i).getLocation();
        }
        return theatreNameList;
    }


    //Updates movieList to be displayed in ListView, shows movie title, start time and end time
    public ArrayList<String> updateMovies(Theatre theatreSelected, String date, String start, String end, String search) {
        ArrayList<String> movieArray = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startTime, endTime, movieStart;
        try {
            if (start.isEmpty()) {
                startTime = sdf.parse("00:01");
            } else {
                startTime = sdf.parse(start);
            }
            if (end.isEmpty()) {
                endTime = sdf.parse("23:59");
            } else {
                endTime = sdf.parse(end);
            }
            if (search.isEmpty()) {
                ArrayList<Movie> moviesList = parseScheduleXML(readXML(scheduleURLBuilder(theatreSelected.getId(), date)));
                for (int i = 0; i < moviesList.size(); i++) {
                    movieStart = sdf.parse(moviesList.get(i).getStartTime());
                    //Only shows movies whose start time is between selected parameters
                    if (!movieStart.before(startTime) && !movieStart.after(endTime)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(moviesList.get(i).getTitle());
                        sb.append("\t\t");
                        sb.append(String.format("%s-%s", moviesList.get(i).getStartTime(), moviesList.get(i).getEndTime()));
                        movieArray.add(sb.toString());
                    }
                }
                return movieArray;
            } else {
                //If no theatre selected, search from all theatres
                if (theatreSelected.getId() == 1029) {
                    for (Theatre t : theatreList) {
                        //All individual theatres have a : in their name, so use it as a filter
                        if (t.getLocation().contains(":")) {
                            ArrayList<Movie> moviesList = parseScheduleXML(readXML(scheduleURLBuilder(t.getId(), date)));
                            for (int i = 0; i < moviesList.size(); i++) {
                                movieStart = sdf.parse(moviesList.get(i).getStartTime());
                                //Only shows movies whose start time is between selected parameters
                                if (!movieStart.before(startTime) && !movieStart.after(endTime) && moviesList.get(i).getTitle().toLowerCase().contains(search.toLowerCase())) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(moviesList.get(i).getTitle());
                                    sb.append("\t\t");
                                    sb.append(String.format("%s-%s", moviesList.get(i).getStartTime(), moviesList.get(i).getEndTime()));
                                    sb.append("\n" + t.getLocation());
                                    movieArray.add(sb.toString());
                                }
                            }
                        }
                    }
                    return movieArray;
                } else {
                    //If theatre selected and search is not empty, search from selected theatre
                    ArrayList<Movie> moviesList = parseScheduleXML(readXML(scheduleURLBuilder(theatreSelected.getId(), date)));
                    for (int i = 0; i < moviesList.size(); i++) {
                        movieStart = sdf.parse(moviesList.get(i).getStartTime());
                        //Only shows movies whose start time is between selected parameters
                        if (!movieStart.before(startTime) && !movieStart.after(endTime) && moviesList.get(i).getTitle().toLowerCase().contains(search.toLowerCase())) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(moviesList.get(i).getTitle());
                            sb.append("\t\t");
                            sb.append(String.format("%s-%s", moviesList.get(i).getStartTime(), moviesList.get(i).getEndTime()));
                            movieArray.add(sb.toString());
                        }
                    }
                    return movieArray;
                }
            }
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException", "Faulty XmlPullParser");
        } catch (IOException e) {
            Log.e("IOException", "Faulty input");
        } catch (ParseException e) {

        }
        return null;
    }
}
