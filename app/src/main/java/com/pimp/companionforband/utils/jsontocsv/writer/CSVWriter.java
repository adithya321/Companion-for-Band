package com.pimp.companionforband.utils.jsontocsv.writer;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import autovalue.shaded.org.apache.commons.lang.StringUtils;

public class CSVWriter {

    public void writeAsCSV(List<LinkedHashMap<String, String>> flatJson, String fileName) throws FileNotFoundException {
        LinkedHashSet<String> headers = collectHeaders(flatJson);
        String output = StringUtils.join(headers.toArray(), ",") + "\n";
        for (LinkedHashMap<String, String> map : flatJson) {
            output = output + getCommaSeperatedRow(headers, map) + "\n";
        }
        writeToFile(output, fileName);
    }

    private void writeToFile(String output, String fileName) throws FileNotFoundException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    private void close(BufferedWriter writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCommaSeperatedRow(LinkedHashSet<String> headers, LinkedHashMap<String, String> map) {
        List<String> items = new ArrayList<>();
        for (String header : headers) {
            String value = map.get(header) == null ? "" : map.get(header).replace(",", "");
            items.add(value);
        }
        return StringUtils.join(items.toArray(), ",");
    }

    private LinkedHashSet<String> collectHeaders(List<LinkedHashMap<String, String>> flatJson) {
        LinkedHashSet<String> headers = new LinkedHashSet<>();
        for (LinkedHashMap<String, String> map : flatJson) {
            headers.addAll(map.keySet());
        }
        return headers;
    }
}
