package com.shenmao.chuhe.commons.exports;

import io.vertx.core.json.JsonArray;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVWriter {

    private static final String SAMPLE_CSV_FILE_DIR = System.getProperty("java.io.tmpdir");

    static {

    }

    public static File get(String[] columns, List<ArrayList<Object>> data) throws IOException {

        Path path = Paths.get(SAMPLE_CSV_FILE_DIR, RandomStringUtils.randomAlphanumeric(15) + ".csv");


        CSVFormat format = CSVFormat.DEFAULT;

        if (columns != null) {
            format = format.withHeader(columns);
        }

        try (

            BufferedWriter writer = Files.newBufferedWriter(path);
            CSVPrinter csvPrinter = new CSVPrinter(writer, format);

        ) {

            /* if (data != null) {
                csvPrinter.printRecord("1", "Sundar, Pichai ♥", "CEO", "Google");
                csvPrinter.printRecord("2", "Satya Nadella", null, "Microsoft");
                csvPrinter.printRecord("3", "Tim cook", "CEO", 1304.00);
                csvPrinter.printRecord(Arrays.asList("4", "Mark Zuckerberg", "CEO", "Facebook"));
            } else {
                csvPrinter.printRecord("error heere");
            } */

            for (int i=0; i<data.size(); i++) {
                csvPrinter.printRecord(data.get(i));
            }

            csvPrinter.flush();

            return path.toFile();

        }


    }

}
