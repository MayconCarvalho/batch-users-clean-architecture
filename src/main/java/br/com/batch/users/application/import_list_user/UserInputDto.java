package br.com.batch.users.application.import_list_user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInputDto {

    private String name;
    private String email;
    private String documentNumber;
}
