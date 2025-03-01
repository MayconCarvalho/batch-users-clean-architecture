package br.com.batch.users.unit;

import br.com.batch.users.application.import_list_user.ImportListUserInputDto;
import br.com.batch.users.application.import_list_user.ImportListUserOutputDto;
import br.com.batch.users.application.import_list_user.ImportListUserUseCase;
import br.com.batch.users.application.import_list_user.UserInputDto;
import br.com.batch.users.domain.exception.UserImportListIsNullException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.infrastructure.messaging.UserImportProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class ImportListUserUseCaseTest {

    @Mock
    private UserImportRepositoryInterface userImportRepositoryInterface;

    @Mock
    private UserImportProducer userImportProducer;

    @InjectMocks
    private ImportListUserUseCase importListUserUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executeSuccessfullyImportsUsers() {
        ImportListUserInputDto inputDto = new ImportListUserInputDto(List.of(
                new UserInputDto("John Doe", "john.doe@example.com", "123456789"),
                new UserInputDto("Jane Doe", "jane.doe@example.com", "987654321")
        ));

        UserImport userImport1 = UserImport.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        UserImport userImport2 = UserImport.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .documentNumber("987654321")
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.importUser(any(UserImport.class)))
                .thenReturn(userImport1, userImport2);

        ImportListUserOutputDto outputDto = importListUserUseCase.execute(inputDto);

        assertEquals(2, outputDto.getUsersInputList().size());
        verify(userImportProducer, times(2)).send(any(UserImport.class));
    }

    @Test
    void executeReturnsFailedStatusWhenExceptionOccurs() {
        ImportListUserInputDto inputDto = new ImportListUserInputDto(List.of(
                new UserInputDto("John Doe", "john.doe@example.com", "123456789"),
                new UserInputDto("Jane Doe", "jane.doe@example.com", "987654321")
        ));

        when(userImportRepositoryInterface.importUser(any(UserImport.class)))
                .thenThrow(new RuntimeException("Database error"));

        ImportListUserOutputDto outputDto = importListUserUseCase.execute(inputDto);

        assertEquals(ImportStatus.FAILED, outputDto.getUsersInputList().get(0).getStatus());
        assertEquals(ImportStatus.FAILED, outputDto.getUsersInputList().get(1).getStatus());
        verify(userImportProducer, times(2)).send(any(UserImport.class));
    }

    @Test
    void executeSuccessfullyImportsUsersWithMixedResults() {
        ImportListUserInputDto inputDto = new ImportListUserInputDto(List.of(
                new UserInputDto("John Doe", "john.doe@example.com", "123456789"),
                new UserInputDto("Jane Doe", "jane.doe@example.com", "987654321")
        ));

        UserImport userImport1 = UserImport.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.importUser(any(UserImport.class)))
                .thenReturn(userImport1)
                .thenThrow(new RuntimeException("Database error"));

        ImportListUserOutputDto outputDto = importListUserUseCase.execute(inputDto);

        assertEquals(ImportStatus.PENDING, outputDto.getUsersInputList().get(0).getStatus());
        assertEquals(ImportStatus.FAILED, outputDto.getUsersInputList().get(1).getStatus());
        verify(userImportProducer, times(2)).send(any(UserImport.class));
    }

    @Test
    void executeThrowExceptionWhenUserInputIsNull() {
        assertThrows(UserImportListIsNullException.class, () -> importListUserUseCase.execute(null));
    }

    @Test
    void executeThrowsExceptionWhenUserInputListIsNull() {
        ImportListUserInputDto inputDto = new ImportListUserInputDto(null);

        assertThrows(UserImportListIsNullException.class, () -> importListUserUseCase.execute(inputDto));
    }

    @Test
    void executeThrowsExceptionWhenUserInputListIsEmpty() {
        ImportListUserInputDto inputDto = new ImportListUserInputDto(List.of());

        assertThrows(UserImportListIsNullException.class, () -> importListUserUseCase.execute(inputDto));
    }
}
