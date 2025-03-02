package br.com.batch.users.unit;

import br.com.batch.users.application.import_list_user.ImportListUserInputDto;
import br.com.batch.users.application.import_list_user.ImportListUserUseCase;
import br.com.batch.users.application.import_list_user.UserInputDto;
import br.com.batch.users.domain.exception.UserImportListIsNullException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import br.com.batch.users.infrastructure.messaging.UserImportProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ImportListUserUseCaseTest {

    @Mock
    private UserImportRepositoryInterface userImportRepositoryInterface;

    @Mock
    private UserImportProducer userImportProducer;

    @InjectMocks
    private ImportListUserUseCase importListUserUseCase;

    @Test
    void executeSuccessfullyImportsUsers() {
        final var inputDto = ImportListUserInputDto.builder()
                .usersInputList(List.of(
                        UserInputDto.builder()
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .documentNumber("123456789")
                                .build(),
                        UserInputDto.builder()
                                .name("Jane Doe")
                                .email("jane.doe@example.com")
                                .documentNumber("987654321")
                                .build()))
                .build();

        final var userImport1 = UserImport.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        final var userImport2 = UserImport.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .documentNumber("987654321")
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.importUser(any(UserImport.class)))
                .thenReturn(userImport1, userImport2);

        final var outputDto = importListUserUseCase.execute(inputDto);

        assertEquals(2, outputDto.getUsersInputList().size());
        verify(userImportProducer, times(2)).send(any(UserImport.class));
    }

    @Test
    void executeReturnsFailedStatusWhenExceptionOccurs() {
        final var inputDto = ImportListUserInputDto.builder()
                .usersInputList(List.of(
                    UserInputDto.builder()
                            .name("John Doe")
                            .email("john-invalid-email")
                            .documentNumber("123456789")
                            .build(),
                    UserInputDto.builder()
                            .name("Jane Doe")
                            .email("john-invalid-email")
                            .documentNumber("987654321")
                            .build()))
                .build();

        final var outputDto = importListUserUseCase.execute(inputDto);

        assertEquals(ImportStatus.FAILED, outputDto.getUsersInputList().get(0).getStatus());
        assertEquals(ImportStatus.FAILED, outputDto.getUsersInputList().get(1).getStatus());
        verifyNoInteractions(userImportRepositoryInterface);
        verifyNoInteractions(userImportProducer);
    }

    @Test
    void executeSuccessfullyImportsUsersWithMixedResults() {
        final var inputDto = ImportListUserInputDto.builder()
                .usersInputList(List.of(
                    UserInputDto.builder()
                            .name("John Doe")
                            .email("john.doe@example.com")
                            .documentNumber("123456789")
                            .build(),
                    UserInputDto.builder()
                            .name("Jane Doe")
                            .email("jane.doe.invalid.email")
                            .documentNumber("987654321")
                            .build()))
                .build();

        final var userImport1 = UserImport.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.importUser(userImport1))
                .thenReturn(userImport1);

        final var outputDto = importListUserUseCase.execute(inputDto);

        assertEquals(ImportStatus.PENDING, outputDto.getUsersInputList().get(0).getStatus());
        assertEquals(ImportStatus.FAILED, outputDto.getUsersInputList().get(1).getStatus());
        verify(userImportRepositoryInterface, times(1)).importUser(userImport1);
        verify(userImportProducer, times(1)).send(userImport1);
    }

    @Test
    void executeThrowExceptionWhenUserInputIsNull() {
        assertThrows(UserImportListIsNullException.class, () -> importListUserUseCase.execute(null));
    }

    @Test
    void executeThrowsExceptionWhenUserInputListIsNull() {
        final var inputDto = new ImportListUserInputDto(null);

        assertThrows(UserImportListIsNullException.class, () -> importListUserUseCase.execute(inputDto));
    }

    @Test
    void executeThrowsExceptionWhenUserInputListIsEmpty() {
        final var inputDto = new ImportListUserInputDto(List.of());

        assertThrows(UserImportListIsNullException.class, () -> importListUserUseCase.execute(inputDto));
    }
}
