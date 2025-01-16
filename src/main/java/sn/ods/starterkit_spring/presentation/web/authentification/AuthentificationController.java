package sn.ods.starterkit_spring.presentation.web.authentification;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sn.ods.starterkit_spring.application.services.interfaces.authentication.AuthenticationService;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.LoginFormDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.ResetOrForgetFormDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;



@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Data
public class AuthentificationController {
    private final AuthenticationService iAuthentification;

   private   final PasswordEncoder encoder;


    @PostMapping("/login")
    public Response<Object> authenticateUser(@Valid @RequestBody LoginFormDTO loginRequest) {
        return iAuthentification.singIn(loginRequest);
    }

    @GetMapping("/refresh-token")
    public Response<Object> refreshToken(@RequestParam String token) {
        return iAuthentification.refreshToken(token);
    }

    @Operation(summary = "Endpoint pour s'authentifier à partir d'un lien mail pour une première connexion")
    @PostMapping("/signin-with-url-connexion")
    public Response<Object> authenticateUserWithFirstUrlConnexion(@Valid @RequestBody ResetOrForgetFormDTO formRequest) {

         return iAuthentification.authenticateUserWithFirstUrlConnexion(formRequest);
    }

    @Operation(summary = "Endpoint pour s'authentifier à partir d'un lien mail apres mot de passe oublié")
    @PostMapping("/signin-with-forget-password-url-connexion")
    public Response<Object> authenticateUserWithForgetPasswordUrlConnexion(@Valid @RequestBody ResetOrForgetFormDTO formRequest) {
        return iAuthentification.authenticateUserWithForgetPasswordUrlConnexion(formRequest);
    }

    @Operation(summary = "Endpoint envoie mail pour reinitialiser le mot de passe oublié")
    @GetMapping("/forgot-password")
    public Response<Object> forgotPassword(@RequestParam("login") String login) {
        return iAuthentification.reinitPassword(login);
    }

    @Operation(summary = "Endpoint pour modifier le mot de passe d'un utilisateur")
    @PostMapping("/edit-user-password")
    public Response<Object> editUserPassword(@Valid @RequestBody ResetOrForgetFormDTO req) {
        return iAuthentification.updatePasswordFromInterface(req);
    }


}
