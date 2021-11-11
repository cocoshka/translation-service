package com.ailleron.translation.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    public static String caseString(String text) {
        return text != null ? text.toLowerCase() : null;
    }

    public static Collection<String> caseCollection(Collection<String> collection) {
        return collection != null ?
                collection.stream().map(Utils::caseString).collect(Collectors.toList())
                : null;
    }

    public static Collection<String> nonEmptyCollection(Collection<String> collection) {
        return collection != null ?
                collection.stream().filter(item -> !item.isEmpty()).collect(Collectors.toList())
                : null;
    }

    public static Map<String, String> readPropertiesFile(File file) throws IOException {
        FileInputStream is = null;
        Properties props = null;

        try {
            is = new FileInputStream(file);
            props = new Properties();
            props.load(is);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) is.close();
        }

        return propertiesToMap(props);
    }

    public static Map<String, String> propertiesToMap(Properties props) {
        Map<String, String> map = new HashMap<>();
        if (props != null) {
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                map.put(Utils.caseString(entry.getKey().toString()), entry.getValue().toString());
            }
        }
        return map;
    }

    public static boolean isMapUnchanged(Map<?, ?> a, Map<?, ?> b) {
        if (a != null && b != null) return a.equals(b);
        return (a != null) == (b != null);
    }
}
