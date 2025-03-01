package br.com.batch.users.application.import_list_user;

import br.com.batch.users.domain.exception.EmailInvalidException;
import br.com.batch.users.domain.exception.UserImportListIsNullException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.domain.seedwork.UseCaseInterface;
import br.com.batch.users.infrastructure.messaging.UserImportProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportListUserUseCase implements UseCaseInterface<ImportListUserInputDto, ImportListUserOutputDto> {

    private final UserImportRepositoryInterface userImportRepositoryInterface;
    private final UserImportProducer userImportProducer;

    @Override
    public ImportListUserOutputDto execute(final ImportListUserInputDto userInputList) {
        log.info("Importing list of users: {}", userInputList);
        if (listIsNotEmpty(userInputList)) {
            throw new UserImportListIsNullException("UserInputList cannot be null");
        }

        final var userImportedList = userInputList.getUsersInputList().stream()
                .map(userInput -> {
                    try {
                        final var userImport = UserImport.builder()
                                .name(userInput.getName())
                                .email(userInput.getEmail())
                                .documentNumber(userInput.getDocumentNumber())
                                .status(ImportStatus.PENDING)
                                .build();
                        return userImportRepositoryInterface.importUser(userImport);
                    } catch (final EmailInvalidException e) {
                        log.error("Error importing user: {}. Cause {}", userInput, e.getMessage());
                        return UserImport.builder()
                                .name(userInput.getName())
                                .email(userInput.getEmail())
                                .documentNumber(userInput.getDocumentNumber())
                                .status(ImportStatus.FAILED)
                                .build();
                    }
                }).toList();

        userImportedList.parallelStream()
                .filter(user -> user.getStatus() == ImportStatus.PENDING)
                .forEach(userImportProducer::send);

        final var statusUserList = userImportedList.parallelStream()
                .map(this::mapUserImportToOutputDto)
                .toList();

        return ImportListUserOutputDto.builder()
                .usersInputList(statusUserList)
                .build();
    }

    private boolean listIsNotEmpty(final ImportListUserInputDto userInputList) {
        return userInputList == null || userInputList.getUsersInputList() == null || userInputList.getUsersInputList().isEmpty();
    }

    private UserOutputDto mapUserImportToOutputDto(final UserImport userImport) {
        return UserOutputDto.builder()
                .id(userImport.getId())
                .status(userImport.getStatus())
                .build();
    }
}
