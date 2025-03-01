package br.com.batch.users.domain.exception;

public class UserImportListIsNullException extends RuntimeException {

    public UserImportListIsNullException(final String message) {
        super(message);
    }
}
