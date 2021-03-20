package com.olio.finnkinoapp;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.ArrayList;

public class TheatreCollection {
    private static TheatreCollection tc_instance = null;
    ArrayList<Theatre> theatreList;
    private TheatreCollection(){
        readXML();
    }

    public static TheatreCollection getInstance(){
        if (tc_instance == null){
            tc_instance = new TheatreCollection();
        }
        return tc_instance;
    }

    public ArrayList<Theatre> getTheatreList(){
        return theatreList;
    }
    public void addTheatre(String location, int id){
        Theatre theatre = new Theatre();
        theatreList.add(theatre);
    }
    public void readXML(){
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            URL xmlURL = new URL("https://www.finnkino.fi/xml/TheatreAreas/");
            InputStream is = xmlURL.openConnection().getInputStream();
            parser.setInput(is, "UTF-8");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parseXML(parser);
        } catch (XmlPullParserException e){

        } catch (IOException e){

        }
    }
    private ArrayList<Theatre> parseXML(XmlPullParser parser) throws IOException, XmlPullParserException{
        theatreList = new ArrayList<>();
        int eventType = parser.getEventType();
        Theatre currentTheatre = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            String elementName = null;
            switch (eventType){
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    if (elementName.equals("TheatreArea")){
                        currentTheatre = new Theatre();
                        theatreList.add(currentTheatre);
                    } else if (currentTheatre != null){
                        if (elementName.equals("ID")){
                            currentTheatre.id = Integer.parseInt(parser.nextText());
                        } else if (elementName.equals("Name")){
                            currentTheatre.location = parser.nextText();
                        }
                    }
                    break;

            }
            eventType = parser.next();
        }
        return theatreList;
    }
}
