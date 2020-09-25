package com.gmail.roma.teodorovich.server.helper;

import com.gmail.roma.teodorovich.server.Config;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class IpHelper {

    public static class IpData {

        private String country;

        private String city;

        public IpData(String country, String city) {
            this.country = country;
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }
    }

    public static String getMyGlobalIP() {
        String globalIP = null;

        try {
            URL ifconfigMe = new URL("https://ifconfig.me/");

            URLConnection c = ifconfigMe.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(c.getInputStream())
            );

            globalIP = reader.readLine();

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return globalIP;
    }

    public static IpData getIpData(String ip) {
        IpData ipData = new IpData("Unknown", "Unknown");

        // Get location info
        try {
            URL extremeIpLookup = new URL("https://ipinfo.io/"
                    + ip + "?token=" + Config.getIpinfoToken());

            URLConnection c = extremeIpLookup.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(c.getInputStream())
            );

            String line;
            StringBuilder responseBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            reader.close();

            try {
                JsonObject jsonObject = new JsonHelper(responseBuilder.toString()).getAsObject();

                ipData.country = jsonObject.get("country").getAsString();
                ipData.city = jsonObject.get("city").getAsString();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ipData;
    }

}
