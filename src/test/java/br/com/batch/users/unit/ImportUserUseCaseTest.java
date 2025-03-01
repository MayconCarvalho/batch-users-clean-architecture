package br.com.batch.users.unit;

import br.com.batch.users.application.import_user.ImportUserInputDto;
import br.com.batch.users.application.import_user.ImportUserOutputDto;
import br.com.batch.users.application.import_user.ImportUserUseCase;
import br.com.batch.users.domain.exception.EmailInvalidException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.infrastructure.messaging.UserImportProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ImportUserUseCaseTest {

    @Mock
    private UserImportRepositoryInterface userImportRepositoryInterface;

    @Mock
    private UserImportProducer userImportProducer;

    @InjectMocks
    private ImportUserUseCase importUserUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executeSuccessfullyImportsUser() {
        ImportUserInputDto inputDto = new ImportUserInputDto("John Doe", "john.doe@example.com", "123456789");

        UserImport userImport = UserImport.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.importUser(any(UserImport.class)))
                .thenReturn(userImport);

        ImportUserOutputDto outputDto = importUserUseCase.execute(inputDto);

        assertEquals(userImport.getId(), outputDto.getId());
        assertEquals(userImport.getStatus(), outputDto.getStatus());
        verify(userImportProducer).send(any(UserImport.class));
    }

    @Test
    void executeThrowsExceptionWhenUserIsNull() {
        ImportUserInputDto inputDto = null;

        assertThrows(NullPointerException.class, () -> importUserUseCase.execute(inputDto));
    }

    @Test
    void executeThrowsExceptionWhenUserEmailIsInvalid() {
        var inputDto = new ImportUserInputDto("John Doe", "invalid-email", "123456789");
        var userImport = UserImport.builder()
                .name(inputDto.getName())
                .email(inputDto.getEmail())
                .documentNumber(inputDto.getDocumentNumber())
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.importUser(userImport)).thenThrow(EmailInvalidException.class);

        assertThrows(EmailInvalidException.class, () -> importUserUseCase.execute(inputDto));
    }

}
