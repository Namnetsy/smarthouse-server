package com.gmail.roma.teodorovich.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Config {

    private static final String PATH_TO_FILE = "config.cfg";

    private static String sendgridToken;

    private static String ipinfoToken;

    private static String dbURL;

    private static String dbUser;

    private static String dbPassword;

    private static String adminEmail;

    private static String port;

    private static boolean isOnline;

    static {
        try {
            List<String> lines = Files.readAllLines(Paths.get(PATH_TO_FILE));

            for (String line : lines) {
                if (!line.isEmpty()) {
                    String[] entry = line.split(":", 2);

                    switch (entry[0]) {
                        case "db_url":
                            dbURL = entry[1];
                            break;
                        case "port":
                            port = entry[1];
                            break;
                        case "online":
                            isOnline = entry[1].equals("yes");
                            break;
                        case "ipinfo_token":
                            ipinfoToken = entry[1];
                            break;
                        case "db_user":
                            dbUser = entry[1];
                            break;
                        case "db_pass":
                            dbPassword = entry[1];
                            break;
                        case "admin_email":
                            adminEmail = entry[1];
                            break;
                        case "sendgrid_token":
                            sendgridToken = entry[1];
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

            System.exit(1);
        }
    }

    public static String getPathToFile() {
        return PATH_TO_FILE;
    }

    public static String getSendgridToken() {
        return sendgridToken;
    }

    public static String getIpinfoToken() {
        return ipinfoToken;
    }

    public static String getDbURL() {
        return dbURL;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static String getDbPassword() {
        return dbPassword;
    }

    public static String getAdminEmail() {
        return adminEmail;
    }

    public static boolean getIsOnline() {
        return isOnline;
    }

    public static String getPort() {
        return port;
    }
}
