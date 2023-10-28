package com.dbms.project.model.validators;

import com.dbms.project.exceptions.DBMSException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern datePattern = Pattern.compile("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", Pattern.CASE_INSENSITIVE);


    public static void validate(String value, String type) {
        if (type.contains("varchar")) {
            int length = Integer.parseInt(type.replaceAll("[^0-9]", ""));
            if (length < value.length()) {
                throw new DBMSException("String length invalid.");
            }
        }

        if (type.contains("date")) {
            Matcher matcher = datePattern.matcher(value);
            if (!matcher.find()) {
                throw new DBMSException("Invalid date.");
            }


        }

        if (type.contains("int")) {
            String intValue = value.replaceAll("[0-9]", "");
            if (!intValue.isEmpty()) {
                throw new DBMSException("Int can contain only digits.");
            }
        }
    }
}
