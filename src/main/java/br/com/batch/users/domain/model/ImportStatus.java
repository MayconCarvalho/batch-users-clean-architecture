package br.com.batch.users.domain.model;

import lombok.Getter;

@Getter
public enum ImportStatus {

    PENDING(1, "pending"),
    PROCESSING(2, "processing"),
    COMPLETED(3, "completed"),
    FAILED(4, "failed");

    private final int code;
    private final String description;

    ImportStatus(final int code, final String description) {
        this.code = code;
        this.description = description;
    }
}
