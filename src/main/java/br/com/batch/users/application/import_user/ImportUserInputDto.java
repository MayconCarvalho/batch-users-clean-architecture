package br.com.batch.users.application.import_user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportUserInputDto {

    private String name;
    private String email;
    private String documentNumber;
}
