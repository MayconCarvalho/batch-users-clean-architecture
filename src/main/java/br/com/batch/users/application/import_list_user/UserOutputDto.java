package br.com.batch.users.application.import_list_user;

import br.com.batch.users.domain.model.ImportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOutputDto {

    private UUID id;
    private String email;
    private ImportStatus status;
}
