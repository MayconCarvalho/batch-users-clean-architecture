package br.com.batch.users.application.find_status_user;

import br.com.batch.users.domain.model.ImportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindStatusUserByIdOutputDto {

    private ImportStatus status;

}
