package br.com.batch.users.application.import_user;

import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.seedwork.UseCaseInterface;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.infrastructure.messaging.UserImportProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportUserUseCase implements UseCaseInterface<ImportUserInputDto, ImportUserOutputDto> {

    private final UserImportRepositoryInterface userImportRepository;
    private final UserImportProducer userImportProducer;

    @Override
    public ImportUserOutputDto execute(final ImportUserInputDto user) {
        log.info("Importing user: {}", user);
        var userImport = UserImport.builder()
                .name(user.getName())
                .email(user.getEmail())
                .documentNumber(user.getDocumentNumber())
                .status(ImportStatus.PENDING)
                .build();

        userImport = userImportRepository.importUser(userImport);
        userImportProducer.send(userImport);

        return ImportUserOutputDto.builder()
                .id(userImport.getId())
                .status(userImport.getStatus())
                .build();
    }
}
