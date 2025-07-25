package br.com.batch.users.infrastructure.database.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserImportRepository extends JpaRepository<UserImportEntity, UUID> {
}
