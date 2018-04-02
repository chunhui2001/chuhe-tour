package com.shenmao.chuhe.serialization;

import com.shenmao.chuhe.commons.exports.CSVWriter;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CsvSerialization {

    public static void serialize(RoutingContext context, SerializeOptions options) {

        context.response().putHeader("Content-Type", "application/csv;charset=UTF-8");
        JsonObject result = JsonSerialization.getData(context, options);

        try {

            File csvFile = null;

            if (result.containsKey("error") && result.getBoolean("error")) {
                csvFile = getCsvErrorData(result);
            } else {
                csvFile = getCsvData(result);
            }

            sendCsvFile(context, csvFile, null);

        } catch (Exception e) {

        }

    }

    public static void sendCsvFile(RoutingContext context, File file, String displayName) {
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/csv;charset=UTF-8")
                .putHeader("Content-Disposition", "attachment; filename=\""
                        + (displayName == null ? file.getName() : displayName) + "\"")
                .putHeader(HttpHeaders.TRANSFER_ENCODING, "chunked")
                .sendFile(file.getAbsolutePath()).end();
    }

    private static File getCsvErrorData(JsonObject result) {

        List<ArrayList<Object>> data = new ArrayList<>();

        ArrayList a = new ArrayList();
        a.add(result.getString("message"));

        data.add(a);

        try {
            return CSVWriter.get(new String[] {"ERROR"}, data);
        } catch (Exception e) {

        }

        return null;

    }

    private static File getCsvData(JsonObject result) {

        List<ArrayList<Object>> data = new ArrayList<>();

        JsonArray csvData = result.getJsonArray("data");

        ArrayList<Object> a = null;
        JsonObject jsonObject = null;
        String[] fieldNames = null;

        for (int i=0; i<csvData.size(); i++) {

            a = new ArrayList<>();
            jsonObject = csvData.getJsonObject(i);

            if (fieldNames == null) fieldNames = jsonObject.fieldNames().toArray(new String[]{});

            for (String f : fieldNames) {
                a.add(jsonObject.getValue(f));
            }

            data.add(a);

        }

        try {
            return CSVWriter.get(fieldNames , data);
        } catch (Exception e) {

        }

        return null;

    }

}
