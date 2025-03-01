package br.com.batch.users.infrastructure.user.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserImportRepository extends JpaRepository<UserImportEntity, UUID> {
}
