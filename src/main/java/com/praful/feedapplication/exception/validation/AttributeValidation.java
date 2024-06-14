package com.praful.feedapplication.exception.validation;

import com.praful.feedapplication.exception.InvalidInputException;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class AttributeValidation {
    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public void emailValidation(String emailAddress) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (!patternMatches(emailAddress, regexPattern)) {
            throw new InvalidInputException("username/email is not valid");
        }
    }
}
