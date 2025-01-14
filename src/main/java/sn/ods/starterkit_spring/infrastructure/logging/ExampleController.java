package sn.ods.starterkit_spring.infrastructure.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs/test")
public class ExampleController {

    @Autowired
    private ExampleService exampleService;

    @GetMapping("/test-error")
    public String testError() {
        exampleService.doSomething();
        return "Test d'erreur";
    }
}