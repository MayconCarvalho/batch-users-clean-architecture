package br.com.batch.users.unit;

import br.com.batch.users.application.find_all_users.FindAllUsersInputDto;
import br.com.batch.users.application.find_all_users.FindAllUsersOutputDto;
import br.com.batch.users.application.find_all_users.FindAllUsersUseCase;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindAllUsersUseCaseTest {

    @Mock
    private UserImportRepositoryInterface userImportRepositoryInterface;

    @InjectMocks
    private FindAllUsersUseCase findAllUsersUseCase;

    @Test
    void successfullyFindsAllUsers() {
        final UUID uuid1 = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        final var user1 = UserImport.builder()
                .id(uuid1)
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        final var user2 = UserImport.builder()
                .id(uuid2)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .documentNumber("987654321")
                .status(ImportStatus.PENDING)
                .build();

        when(userImportRepositoryInterface.findAllUsers()).thenReturn(List.of(user1, user2));

        final List<FindAllUsersOutputDto> outputDtoList = findAllUsersUseCase.execute(new FindAllUsersInputDto());

        assertEquals(2, outputDtoList.size());
        assertEquals(uuid1, outputDtoList.get(0).getId());
        assertEquals(uuid2, outputDtoList.get(1).getId());
        assertEquals("John Doe", outputDtoList.get(0).getName());
        assertEquals("Jane Doe", outputDtoList.get(1).getName());
    }

    @Test
    void returnsEmptyListWhenNoUsersFound() {
        when(userImportRepositoryInterface.findAllUsers()).thenReturn(List.of());

        final List<FindAllUsersOutputDto> outputDtoList = findAllUsersUseCase.execute(new FindAllUsersInputDto());

        assertEquals(0, outputDtoList.size());
    }
}
