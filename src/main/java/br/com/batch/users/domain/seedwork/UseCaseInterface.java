package br.com.batch.users.domain.seedwork;

public interface UseCaseInterface<I, O> {

    O execute(I input);
}
