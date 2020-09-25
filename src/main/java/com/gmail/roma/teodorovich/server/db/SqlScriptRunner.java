package com.gmail.roma.teodorovich.server.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlScriptRunner {

    public static void execute(String scriptFilePath) throws IOException, SQLException {
        try (Connection conn = PostgreSqlPool.getConnection();
             Statement statement = conn.createStatement();
             FileReader file = new FileReader(scriptFilePath);
             BufferedReader reader = new BufferedReader(file)) {
            String line;
            StringBuilder sql = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sql.append(line);
            }

            statement.execute(sql.toString());
        }
    }

}