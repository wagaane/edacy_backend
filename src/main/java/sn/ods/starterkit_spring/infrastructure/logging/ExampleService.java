package sn.ods.starterkit_spring.infrastructure.logging;

import org.springframework.stereotype.Service;

@Service
public class ExampleService {

    public void doSomething() {
        // Simuler une erreur
        throw new RuntimeException("Une erreur s'est produite dans ExampleService");
    }
}