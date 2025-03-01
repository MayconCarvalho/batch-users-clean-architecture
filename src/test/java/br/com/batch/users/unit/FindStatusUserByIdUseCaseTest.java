package br.com.batch.users.unit;

import br.com.batch.users.application.find_status_user.FindStatusUserByIdInputDto;
import br.com.batch.users.application.find_status_user.FindStatusUserByIdOutputDto;
import br.com.batch.users.application.find_status_user.FindStatusUserByIdUseCase;
import br.com.batch.users.domain.exception.UserImportNotFoundException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class FindStatusUserByIdUseCaseTest {

    @Mock
    private UserImportRepositoryInterface userImportRepositoryInterface;

    @InjectMocks
    private FindStatusUserByIdUseCase findStatusUserByIdUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void successfullyFindsUserStatusById() {
        UUID uuid = UUID.randomUUID();
        FindStatusUserByIdInputDto inputDto = new FindStatusUserByIdInputDto(uuid);

        UserImport userImport = UserImport.builder()
                .id(uuid)
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.findUserById(uuid)).thenReturn(Optional.of(userImport));

        FindStatusUserByIdOutputDto outputDto = findStatusUserByIdUseCase.execute(inputDto);

        assertEquals(ImportStatus.PENDING, outputDto.getStatus());
    }

    @Test
    void throwsExceptionWhenUserNotFound() {
        UUID uuid = UUID.randomUUID();
        FindStatusUserByIdInputDto inputDto = new FindStatusUserByIdInputDto(uuid);

        when(userImportRepositoryInterface.findUserById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserImportNotFoundException.class, () -> findStatusUserByIdUseCase.execute(inputDto));
    }
}