package util;

import exception.NoDataAvailableException;
import jdk.nashorn.internal.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class GeneralService {
    public static String sendErrorJson(Exception e) {
        Map<String, String> exceptionMap = new HashMap<>();
        exceptionMap.put("There was an issue with your request",e.getMessage());
        return JSONParser.quote(exceptionMap.toString());
    }
}
