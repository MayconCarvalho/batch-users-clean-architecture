package br.com.batch.users.application.find_status_user;

import br.com.batch.users.domain.exception.UserImportNotFoundException;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.domain.seedwork.UseCaseInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindStatusUserByIdUseCase implements UseCaseInterface<FindStatusUserByIdInputDto, FindStatusUserByIdOutputDto> {

    private final UserImportRepositoryInterface userImportRepositoryInterface;

    @Override
    public FindStatusUserByIdOutputDto execute(final FindStatusUserByIdInputDto input) {
        log.info("Finding status user by id: {}", input.getId());
        final var userImported = userImportRepositoryInterface.findUserById(input.getId())
                .orElseThrow(() -> new UserImportNotFoundException("User not found with id: " + input.getId()));

        return FindStatusUserByIdOutputDto.builder()
                .status(userImported.getStatus())
                .build();
    }
}
