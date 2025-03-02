package br.com.batch.users.unit;

import br.com.batch.users.application.find_status_user.FindStatusUserByIdInputDto;
import br.com.batch.users.application.find_status_user.FindStatusUserByIdUseCase;
import br.com.batch.users.domain.exception.UserImportNotFoundException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindStatusUserByIdUseCaseTest {

    @Mock
    private UserImportRepositoryInterface userImportRepositoryInterface;

    @InjectMocks
    private FindStatusUserByIdUseCase findStatusUserByIdUseCase;

    @Test
    void successfullyFindsUserStatusById() {
        final UUID uuid = UUID.randomUUID();
        final var inputDto = new FindStatusUserByIdInputDto(uuid);

        final var userImport = UserImport.builder()
                .id(uuid)
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.findUserById(uuid)).thenReturn(Optional.of(userImport));

        final var outputDto = findStatusUserByIdUseCase.execute(inputDto);

        assertEquals(ImportStatus.PENDING, outputDto.getStatus());
    }

    @Test
    void throwsExceptionWhenUserNotFound() {
        final UUID uuid = UUID.randomUUID();
        final var inputDto = new FindStatusUserByIdInputDto(uuid);

        when(userImportRepositoryInterface.findUserById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserImportNotFoundException.class, () -> findStatusUserByIdUseCase.execute(inputDto));
    }
}