package br.com.batch.users.application.find_all_users;

import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.domain.seedwork.UseCaseInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindAllUsersUseCase implements UseCaseInterface<FindAllUsersInputDto, List<FindAllUsersOutputDto>> {

    private final UserImportRepositoryInterface userImportRepositoryInterface;

    @Override
    public List<FindAllUsersOutputDto> execute(final FindAllUsersInputDto input) {
        log.info("Finding all users");
        return userImportRepositoryInterface.findAllUsers().stream()
                .map(this::mapToOutputDto)
                .toList();
    }

    private FindAllUsersOutputDto mapToOutputDto(final UserImport userImport) {
        return FindAllUsersOutputDto.builder()
                .id(userImport.getId())
                .name(userImport.getName())
                .email(userImport.getEmail())
                .documentNumber(userImport.getDocumentNumber())
                .status(userImport.getStatus())
                .build();
    }
}
