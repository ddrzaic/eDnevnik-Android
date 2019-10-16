package com.example.kalkulatorocjenazae_dnevnik;


import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HTTPSConnection {

    public List<String> cookies;
    public HttpsURLConnection conn;
    public final String USER_AGENT = "Mozilla/5.0";



    public void sendPost(String url, String postParams) throws Exception {

        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host", "ocjene.skole.hr");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");


        for (String cookie: this.cookies) {
            conn.setRequestProperty("Cookie",  cookie.split(";", 1)[0]);
        }

        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Referer", "https://ocjene.skole.hr/pocetna/prijava");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
        conn.setDoOutput(true);
        conn.setDoInput(true);
        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();
        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }


    public String GetPageContent(String url) throws Exception {
        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setUseCaches(false);
        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        cookies=getCookies();




        if (cookies != null) {
            for (String cookie : this.cookies) {
                conn.setRequestProperty("Cookie",  cookie.split(";", 1)[0]);
            }
        }
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // Get the response cookies


            setCookies(conn.getHeaderFields().get("Set-Cookie"));


        return response.toString();
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }
    public List<String> getCookies() {
        return cookies;
    }

    public String getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(html);
        Element loginform = doc.getElementById("login-students");
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("user_login"))
                value = username;
            else if (key.equals("user_password"))
                value = password;
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }
        return result.toString();
    }
}

