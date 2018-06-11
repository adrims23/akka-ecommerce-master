package util;

import exception.NoDataAvailableException;
import jdk.nashorn.internal.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class GeneralService {
    public static String sendErrorJson(Exception e) {
        Map<String, String> exceptionMap = new HashMap<>();
        return JSONParser.quote(exceptionMap.toString());
    }
}
