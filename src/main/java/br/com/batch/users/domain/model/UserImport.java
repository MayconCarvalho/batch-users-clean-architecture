package br.com.batch.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserImport {

    private UUID id;
    private String name;
    private String email;
    private String documentNumber;
    private ImportStatus status;
}
