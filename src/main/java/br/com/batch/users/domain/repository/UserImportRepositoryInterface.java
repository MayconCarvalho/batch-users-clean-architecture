package br.com.batch.users.domain.repository;

import br.com.batch.users.domain.model.UserImport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserImportRepositoryInterface {

    UserImport importUser(UserImport userImport);
    void updateStatusUser(UserImport userImport);
    Optional<UserImport> findUserById(UUID id);
    List<UserImport> findAllUsers();

}
