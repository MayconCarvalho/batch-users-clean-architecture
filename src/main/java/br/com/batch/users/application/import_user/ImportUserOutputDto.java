package br.com.batch.users.application.import_user;

import br.com.batch.users.domain.model.ImportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportUserOutputDto {

    private UUID id;
    private ImportStatus status;
}
