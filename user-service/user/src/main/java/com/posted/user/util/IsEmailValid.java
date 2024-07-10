package com.posted.user.util;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;

import com.posted.exception.ApiRequestException;

public class IsEmailValid implements Predicate<String> {
  @Override
    public boolean test(String email) {
        Pattern emailPattern =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                        Pattern.CASE_INSENSITIVE);

        if (email == null || email.isEmpty()) {
            throw new ApiRequestException("Email is required", HttpStatus.NO_CONTENT);
        }
        String[] parts = email.split("@");
        if (parts.length!= 2) {
            return false;
        }
        for (String part : parts) {
            if (part.length() > 64) {
                return false;
            }
        }
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()){
            return false;
        }
        return true;
    }
}