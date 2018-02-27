package com.shenmao.chuhe.commons.handlebarhelper;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;

// https://handlebarsjs.com/block_helpers.html
public class SpinnerHelper implements Helper<Object> {
    @Override
    public CharSequence apply(Object pamam0, Options options) throws IOException {

        String theme = pamam0 + "";
        String themeClass = "darkturquoise";

        switch (theme) {

            case "theme1":
                themeClass = "tur";     // darkturquoise
                break;
            case "theme2":
                themeClass = "able";    // aliceblue
                break;
            case "theme3":
                themeClass = "bvlt";    // blueviolet
                break;
            case "theme4":
                themeClass = "byw";     // burlywood
                break;
            case "theme5":
                themeClass = "cal";     // coral
                break;
            default:
                themeClass = "red";
        }

        return new Handlebars.SafeString(
                "<div class=\"hd-spinner " + themeClass + "\">\n" +
                "    <div class=\"rect1\"></div>\n" +
                "    <div class=\"rect2\"></div>\n" +
                "    <div class=\"rect3\"></div>\n" +
                "    <div class=\"rect4\"></div>\n" +
                "    <div class=\"rect5\"></div>\n" +
                "</div>");
    }
}
