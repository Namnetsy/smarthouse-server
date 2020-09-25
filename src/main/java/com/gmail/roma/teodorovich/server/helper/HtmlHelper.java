package com.gmail.roma.teodorovich.server.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlHelper {

    private String html;

    private String originalHtml;

    public HtmlHelper() {
        html = null;
        originalHtml = null;
    }

    public HtmlHelper setHTML(String html) {
        this.html = html;
        originalHtml = html;

        return this;
    }

    public HtmlHelper loadHtmlFromFile(String path) {
        if (path == null) {
            return this;
        }

        try {
            html = Files.readString(Paths.get(path));
            originalHtml = html;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public String getCompactHtml() {
        if (html == null) {
            return null;
        }

        return html.trim();
    }

    public HtmlHelper fillPlaceholder(String placeholder, String value) {
        if (html == null) {
            return this;
        }

        html = html.replace(placeholder, value);

        return this;
    }

    public void resetPlaceholders() {
        if (html == null) {
            return;
        }

        html = originalHtml;
    }

}
