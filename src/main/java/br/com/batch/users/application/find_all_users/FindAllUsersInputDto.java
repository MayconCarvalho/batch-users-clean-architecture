package br.com.batch.users.application.find_all_users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindAllUsersInputDto {

    private Integer page;
    private Integer size;
    private String sort;
    private String direction;
}
