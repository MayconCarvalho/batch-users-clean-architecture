package br.com.batch.users.infrastructure.database.postgres;

import br.com.batch.users.domain.exception.UserImportNotFoundException;
import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.domain.repository.UserImportRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.DataException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserImportRepositoryInterfaceImpl implements UserImportRepositoryInterface {

    private final UserImportRepository repository;

    @Override
    public UserImport importUser(final UserImport user) {
        var userImportEntity = UserImportEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .documentNumber(user.getDocumentNumber())
                .status(user.getStatus())
                .build();

        try {
            userImportEntity = repository.save(userImportEntity);
            user.setId(userImportEntity.getId());
            return user;
        } catch (final DataException ex) {
            userImportEntity.setStatus(ImportStatus.FAILED);
            return user;
        }
    }

    @Override
    public void updateStatusUser(final UserImport userImport) {
        final var userImportEntity = repository.findById(userImport.getId())
                .orElseThrow(() -> new UserImportNotFoundException("User import not found"));

        userImportEntity.setStatus(userImport.getStatus());
        repository.save(userImportEntity);
    }

    @Override
    public Optional<UserImport> findUserById(final UUID id) {
        final var userImportEntity = repository.findById(id);

        if (userImportEntity.isEmpty()) {
            return Optional.empty();
        }

        final var userImport = userImportEntity.get();
        return Optional.of(UserImport.builder()
                .id(userImport.getId())
                .name(userImport.getName())
                .email(userImport.getEmail())
                .documentNumber(userImport.getDocumentNumber())
                .status(userImport.getStatus())
                .build());
    }

    @Override
    public List<UserImport> findAllUsers() {
        return repository.findAll().stream()
                 .map(this::mapUserImportEntityToUserImport)
                 .toList();
    }

    private UserImport mapUserImportEntityToUserImport(final UserImportEntity userImportEntity) {
        return UserImport.builder()
                .id(userImportEntity.getId())
                .name(userImportEntity.getName())
                .email(userImportEntity.getEmail())
                .documentNumber(userImportEntity.getDocumentNumber())
                .status(userImportEntity.getStatus())
                .build();
    }
}
