package br.com.batch.users.infrastructure.messaging;

import br.com.batch.users.domain.model.ImportStatus;
import br.com.batch.users.domain.model.UserImport;
import br.com.batch.users.infrastructure.database.postgres.UserImportRepositoryInterfaceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserImportConsumer {

    private final UserImportRepositoryInterfaceImpl repository;

    @RabbitListener(queues = "user.import.queue")
    public void processUserImport(final UserImport userImport) {
        log.info("Processing rabbitMQ: {}", userImport);
        try {
            userImport.setStatus(ImportStatus.PROCESSING);
            repository.updateStatusUser(userImport);
            log.info("User import processing id '{}'", userImport.getId());

            // Simulate processing time
            Thread.sleep(10000);

            userImport.setStatus(ImportStatus.COMPLETED);
            log.info("User import completed id '{}'", userImport.getId());
        } catch (InterruptedException e) {
            log.error("Error processing user import id '{}'", userImport.getId(), e);
            userImport.setStatus(ImportStatus.FAILED);
            Thread.currentThread().interrupt();
        } finally {
            repository.updateStatusUser(userImport);
            log.info("User import saved id '{}'", userImport.getId());
        }
    }
}
