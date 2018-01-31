package com.cafe24.kye1898.library.Search.BookSearchFromLib;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BooksParser {
    private InputStream urlStream;
    private XmlPullParserFactory factory;
    private XmlPullParser parser;

    private List<BookFeed> rssFeedList;
    private BookFeed rssFeed;

    private String urlString;
    private String tagName;

    private String manage_code;
    private String title_info;
    private String author_info;
    private String pub_info;
    private String manage_name;
    private String detailLink;
    private String id;

    public static final String ITEM = "item";
    public static final String CHANNEL = "channel";

    public static final String MANAGE_CODE = "manage_code";
    public static final String TITLE_INFO = "title_info";
    public static final String AUTHOR_INFO = "author_info";
    public static final String PUB_INFO = "pub_info";
    public static final String MANAGE_NAME = "manage_name";
    public static final String DETAILLINK = "detailLink";
    public static final String ID = "id";


    public BooksParser(String urlString) {
        this.urlString = urlString;
    }

    public static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    public List<BookFeed> parse() {
        try {
            int count = 0;
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            urlStream = downloadUrl(urlString);
            parser.setInput(urlStream, null);
            int eventType = parser.getEventType();
            boolean done = false;
            rssFeed = new BookFeed();
            rssFeedList = new ArrayList<BookFeed>();

            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (tagName.equals("manage_code")) {
                            rssFeed = new BookFeed();
                        }
                        if (tagName.equals("manage_code")) {

                            manage_code = parser.nextText().toString();
                            manage_code = manage_code.replaceAll("</b>","");
                            manage_code = manage_code.replaceAll("<b>","");
                        }
                        if (tagName.equals("title_info")) {
                            title_info = parser.nextText().toString();
                            title_info = title_info.replaceAll("</b>","");
                            title_info = title_info.replaceAll("<b>","");
                        }
                        if (tagName.equals("author_info")) {
                            author_info = parser.nextText().toString();
                            author_info = author_info.replaceAll("</b>","");
                            author_info = author_info.replaceAll("<b>","");
                        }
                        if (tagName.equals("pub_info")) {
                            pub_info = parser.nextText().toString();
                            pub_info = pub_info.replaceAll("</b>","");
                            pub_info = pub_info.replaceAll("<b>","");
                        }
                        if (tagName.equals("manage_name")) {

                            manage_name = parser.nextText().toString();
                            manage_name = manage_name.replaceAll("</b>","");
                            manage_name = manage_name.replaceAll("<b>","");
                        }
                        if (tagName.equals("detailLink")) {
                            detailLink = parser.nextText().toString();
                            detailLink = detailLink.replaceAll("</b>","");
                            detailLink = detailLink.replaceAll("<b>","");
                        }
                        if (tagName.equals("id")) {
                            id = parser.nextText().toString();
                            id = id.replaceAll("</b>","");
                            id = id.replaceAll("<b>","");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        double condition=0;
                        if(manage_code!=null) {
                            condition= Double.parseDouble(manage_code.trim());
                        }
                        if (tagName.equals("root")) {
                            done = true;
                        } else if (tagName.equals("item")&&(((condition>111000)&&(condition<112000))||((condition>311000)&&(condition<312000))||((condition>711000)&&(condition<712000))||manage_code.contains("011001")||manage_code.contains("011003"))) {

                            rssFeed = new BookFeed(manage_code, title_info, author_info, pub_info, manage_name, detailLink, id);
                            rssFeedList.add(rssFeed);
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rssFeedList;
    }
}
