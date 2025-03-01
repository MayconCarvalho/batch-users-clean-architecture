package br.com.batch.users.unit;

import br.com.batch.users.domain.exception.EmailInvalidException;
import br.com.batch.users.domain.exception.UserImportNotFoundException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.infrastructure.user.postgres.UserImportRepositoryInterfaceImpl;
import br.com.batch.users.infrastructure.user.postgres.UserImportRepository;
import br.com.batch.users.infrastructure.user.postgres.UserImportEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserImportRepositoryInterfaceImplTest {

    @Mock
    private UserImportRepository repository;

    @InjectMocks
    private UserImportRepositoryInterfaceImpl userImportRepositoryInterfaceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void importUserSuccessfully() {
        UserImport user = UserImport.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        UserImportEntity userImportEntity = UserImportEntity.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        when(repository.save(any(UserImportEntity.class))).thenReturn(userImportEntity);

        UserImport result = userImportRepositoryInterfaceImpl.importUser(user);

        assertEquals(userImportEntity.getId(), result.getId());
    }

    @Test
    void importUserThrowsEmailInvalidException() {
        UserImport user = UserImport.builder()
                .name("John Doe")
                .email("invalid-email")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        assertThrows(EmailInvalidException.class, () -> userImportRepositoryInterfaceImpl.importUser(user));
    }

    @Test
    void executeThrowsExceptionWhenEmailIsNull() {
        UserImport user = UserImport.builder()
                .name("John Doe")
                .email(null)
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        assertThrows(EmailInvalidException.class, () -> userImportRepositoryInterfaceImpl.importUser(user));
    }

    @Test
    void updateStatusUserSuccessfully() {
        UserImport user = UserImport.builder()
                .id(UUID.randomUUID())
                .status(ImportStatus.COMPLETED)
                .build();

        UserImportEntity userImportEntity = UserImportEntity.builder()
                .id(user.getId())
                .status(ImportStatus.PENDING)
                .build();

        when(repository.findById(user.getId())).thenReturn(Optional.of(userImportEntity));

        userImportRepositoryInterfaceImpl.updateStatusUser(user);

        assertEquals(ImportStatus.COMPLETED, userImportEntity.getStatus());
    }

    @Test
    void updateStatusUserThrowsUserImportNotFoundException() {
        UserImport user = UserImport.builder()
                .id(UUID.randomUUID())
                .status(ImportStatus.COMPLETED)
                .build();

        when(repository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserImportNotFoundException.class, () -> userImportRepositoryInterfaceImpl.updateStatusUser(user));
    }

    @Test
    void findUserByIdSuccessfully() {
        UUID userId = UUID.randomUUID();

        UserImportEntity userImportEntity = UserImportEntity.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        when(repository.findById(userId)).thenReturn(Optional.of(userImportEntity));

        Optional<UserImport> result = userImportRepositoryInterfaceImpl.findUserById(userId);

        assertEquals(userId, result.get().getId());
    }

    @Test
    void findUserByIdReturnsEmptyWhenNotFound() {
        UUID userId = UUID.randomUUID();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        Optional<UserImport> result = userImportRepositoryInterfaceImpl.findUserById(userId);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void findAllUsersSuccessfully() {
        UserImportEntity user1 = UserImportEntity.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .status(ImportStatus.PENDING)
                .build();

        UserImportEntity user2 = UserImportEntity.builder()
                .id(UUID.randomUUID())
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .documentNumber("987654321")
                .status(ImportStatus.PENDING)
                .build();

        when(repository.findAll()).thenReturn(List.of(user1, user2));

        List<UserImport> result = userImportRepositoryInterfaceImpl.findAllUsers();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
    }

    @Test
    void findAllUsersReturnsEmptyListWhenNoUsersFound() {
        when(repository.findAll()).thenReturn(List.of());

        List<UserImport> result = userImportRepositoryInterfaceImpl.findAllUsers();

        assertEquals(0, result.size());
    }
}
