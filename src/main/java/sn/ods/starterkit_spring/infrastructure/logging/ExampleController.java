package sn.ods.starterkit_spring.infrastructure.logging;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @GetMapping("/account/logoutt")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        // Simuler une erreur si l'en-tête "Authorization" est manquant
        //return "Logout successful";
        throw new RuntimeException("Required request header 'Authorization' is not present");

    }
}