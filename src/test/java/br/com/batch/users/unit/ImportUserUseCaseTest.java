package br.com.batch.users.unit;

import br.com.batch.users.application.import_user.ImportUserInputDto;
import br.com.batch.users.application.import_user.ImportUserUseCase;
import br.com.batch.users.domain.exception.EmailInvalidException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.infrastructure.messaging.UserImportProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class ImportUserUseCaseTest {

    @Mock
    private UserImportRepositoryInterface userImportRepositoryInterface;

    @Mock
    private UserImportProducer userImportProducer;

    @InjectMocks
    private ImportUserUseCase importUserUseCase;

    @Test
    void executeSuccessfullyImportsUser() {
        final var inputDto = ImportUserInputDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .build();

        final var userImport = UserImport.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.importUser(any(UserImport.class)))
                .thenReturn(userImport);

        final var outputDto = importUserUseCase.execute(inputDto);

        assertEquals(userImport.getId(), outputDto.getId());
        assertEquals(userImport.getStatus(), outputDto.getStatus());
        verify(userImportProducer).send(any(UserImport.class));
    }

    @Test
    void executeThrowsExceptionWhenUserIsNull() {
        final ImportUserInputDto inputDto = null;

        assertThrows(NullPointerException.class, () -> importUserUseCase.execute(inputDto));
    }

    @Test
    void executeThrowsExceptionWhenUserEmailIsInvalid() {
        final var inputDto = ImportUserInputDto.builder()
                .name("John Doe")
                .email("invalid-email")
                .documentNumber("123456789")
                .build();

        assertThrows(EmailInvalidException.class, () -> importUserUseCase.execute(inputDto));
    }

    @Test
    void executeThrowsExceptionWhenEmailIsNull() {
        final var inputDto = ImportUserInputDto.builder()
                .name("John Doe")
                .email(null)
                .documentNumber("123456789")
                .build();

        assertThrows(EmailInvalidException.class, () -> importUserUseCase.execute(inputDto));
    }

}
