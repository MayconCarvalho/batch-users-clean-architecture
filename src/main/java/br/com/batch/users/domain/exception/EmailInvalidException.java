package br.com.batch.users.domain.exception;

public class EmailInvalidException extends RuntimeException {

    public EmailInvalidException(final String message) {
        super(message);
    }
}
