package br.com.batch.users.application.import_list_user;

import br.com.batch.users.domain.exception.UserImportListIsNullException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.domain.seedwork.UseCaseInterface;
import br.com.batch.users.domain.utils.EmailValidator;
import br.com.batch.users.infrastructure.messaging.UserImportProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportListUserUseCase implements UseCaseInterface<ImportListUserInputDto, ImportListUserOutputDto> {

    private final UserImportRepositoryInterface userImportRepository;
    private final UserImportProducer userImportProducer;

    @Override
    public ImportListUserOutputDto execute(final ImportListUserInputDto userInputList) {
        log.info("Importing list of users: {}", userInputList);
        if (listIsNotEmpty(userInputList)) {
            throw new UserImportListIsNullException("UserInputList cannot be null");
        }

        final var userImportedList = getUserImportedList(userInputList);

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

    private List<UserImport> getUserImportedList(final ImportListUserInputDto userInputList) {
        return userInputList.getUsersInputList().stream()
                .map(userInput -> {
                    if (!EmailValidator.validate(userInput.getEmail())) {
                        log.warn("Error importing user: {}. Cause Email is invalid", userInput);
                        return UserImport.builder()
                                .name(userInput.getName())
                                .email(userInput.getEmail())
                                .documentNumber(userInput.getDocumentNumber())
                                .status(ImportStatus.FAILED)
                                .build();
                    }
                    final var userImport = UserImport.builder()
                            .name(userInput.getName())
                            .email(userInput.getEmail())
                            .documentNumber(userInput.getDocumentNumber())
                            .status(ImportStatus.PENDING)
                            .build();
                    return userImportRepository.importUser(userImport);
                }).toList();
    }

    private boolean listIsNotEmpty(final ImportListUserInputDto userInputList) {
        return userInputList == null || userInputList.getUsersInputList() == null || userInputList.getUsersInputList().isEmpty();
    }

    private UserOutputDto mapUserImportToOutputDto(final UserImport userImport) {
        return UserOutputDto.builder()
                .id(userImport.getId())
                .email(userImport.getEmail())
                .status(userImport.getStatus())
                .build();
    }
}
