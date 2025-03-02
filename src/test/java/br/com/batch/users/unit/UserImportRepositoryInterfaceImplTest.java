package br.com.batch.users.unit;

import br.com.batch.users.domain.exception.UserImportNotFoundException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.infrastructure.database.postgres.UserImportEntity;
import br.com.batch.users.infrastructure.database.postgres.UserImportRepository;
import br.com.batch.users.infrastructure.database.postgres.UserImportRepositoryInterfaceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserImportRepositoryInterfaceImplTest {

    @Mock
    private UserImportRepository repository;

    @InjectMocks
    private UserImportRepositoryInterfaceImpl userImportRepositoryInterfaceImpl;

    @Test
    void importUserSuccessfully() {
        final var user = UserImport.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        final var userImportEntity = UserImportEntity.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        when(repository.save(any(UserImportEntity.class))).thenReturn(userImportEntity);

        final var result = userImportRepositoryInterfaceImpl.importUser(user);

        assertEquals(userImportEntity.getId(), result.getId());
    }

    @Test
    void updateStatusUserSuccessfully() {
        final var user = UserImport.builder()
                .id(UUID.randomUUID())
                .status(ImportStatus.COMPLETED)
                .build();

        final var userImportEntity = UserImportEntity.builder()
                .id(user.getId())
                .status(ImportStatus.PENDING)
                .build();

        when(repository.findById(user.getId())).thenReturn(Optional.of(userImportEntity));

        userImportRepositoryInterfaceImpl.updateStatusUser(user);

        assertEquals(ImportStatus.COMPLETED, userImportEntity.getStatus());
    }

    @Test
    void updateStatusUserThrowsUserImportNotFoundException() {
        final var user = UserImport.builder()
                .id(UUID.randomUUID())
                .status(ImportStatus.COMPLETED)
                .build();

        when(repository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserImportNotFoundException.class, () -> userImportRepositoryInterfaceImpl.updateStatusUser(user));
    }

    @Test
    void findUserByIdSuccessfully() {
        final UUID userId = UUID.randomUUID();

        final var userImportEntity = UserImportEntity.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        when(repository.findById(userId)).thenReturn(Optional.of(userImportEntity));

        final Optional<UserImport> result = userImportRepositoryInterfaceImpl.findUserById(userId);

        assertEquals(userId, result.get().getId());
    }

    @Test
    void findUserByIdReturnsEmptyWhenNotFound() {
        final UUID userId = UUID.randomUUID();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        final Optional<UserImport> result = userImportRepositoryInterfaceImpl.findUserById(userId);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void findAllUsersSuccessfully() {
        final var user1 = UserImportEntity.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        final var user2 = UserImportEntity.builder()
                .id(UUID.randomUUID())
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .documentNumber("987654321")
                .status(ImportStatus.PENDING)
                .build();

        when(repository.findAll()).thenReturn(List.of(user1, user2));

        final List<UserImport> result = userImportRepositoryInterfaceImpl.findAllUsers();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
    }

    @Test
    void findAllUsersReturnsEmptyListWhenNoUsersFound() {
        when(repository.findAll()).thenReturn(List.of());

        final List<UserImport> result = userImportRepositoryInterfaceImpl.findAllUsers();

        assertEquals(0, result.size());
    }
}
