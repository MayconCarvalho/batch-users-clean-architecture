package br.com.batch.users.application.import_list_user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportListUserInputDto {

    private List<UserInputDto> usersInputList;
}
