package br.com.batch.users.infrastructure.messaging;

import br.com.batch.users.domain.model.UserImport;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserImportProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(final UserImport userImport) {
        rabbitTemplate.convertAndSend("user.import.queue", userImport);
    }
}
