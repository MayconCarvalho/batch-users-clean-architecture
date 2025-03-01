package br.com.batch.users.domain.utils;

import java.util.regex.Pattern;

public class EmailValidator {

    private EmailValidator() { }

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static boolean validate(final String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }
}
