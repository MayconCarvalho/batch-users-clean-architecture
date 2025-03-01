package br.com.batch.users.domain.exception;

public class UserImportNotFoundException extends RuntimeException {

    public UserImportNotFoundException(final String message) {
        super(message);
    }
}
