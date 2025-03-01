package br.com.batch.users.application.find_all_users;

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
public class FindAllUsersOutputDto {

    private UUID id;
    private String name;
    private String email;
    private String documentNumber;
    private ImportStatus status;

}
