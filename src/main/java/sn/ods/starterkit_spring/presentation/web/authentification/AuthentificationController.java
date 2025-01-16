package sn.ods.starterkit_spring.presentation.web.authentification;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sn.ods.starterkit_spring.application.services.interfaces.authentication.AuthenticationService;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.ForgetFormDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.InitialAuthenticationDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.LoginFormDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.ResetOrForgetFormDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.APIResponse;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;
import sn.ods.starterkit_spring.presentation.dto.responses.authentication.JwtDTO;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Data
public class AuthentificationController {
    private final AuthenticationService iAuthentification;

   private   final PasswordEncoder encoder;


    @PostMapping("/login")
    public  ResponseEntity<APIResponse> authenticateUser(@Valid @RequestBody LoginFormDTO loginRequest) {

        APIResponse response = APIResponse
                .success(iAuthentification.singIn(loginRequest));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<APIResponse> refreshToken(@RequestParam String token) {
        APIResponse response = APIResponse
                .success(iAuthentification.refreshToken(token));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Endpoint pour s'authentifier à partir d'un lien mail pour une première connexion")
    @PostMapping("/signin-with-url-connexion")
    public ResponseEntity<APIResponse>  authenticateUserWithFirstUrlConnexion(@Valid @RequestBody InitialAuthenticationDTO formRequest) {

         return iAuthentification.authenticateUserWithFirstUrlConnexion(formRequest);
    }

    @Operation(summary = "Endpoint pour s'authentifier à partir d'un lien mail apres mot de passe oublié")
    @PostMapping("/signin-with-forget-password-url-connexion")
    public ResponseEntity<APIResponse> authenticateUserWithForgetPasswordUrlConnexion(@Valid @RequestBody ForgetFormDTO formRequest) {
        JwtDTO jwtDTO = iAuthentification.authenticateUserWithForgetPasswordUrlConnexion(formRequest);

        APIResponse response = APIResponse.success(jwtDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Endpoint envoie mail pour reinitialiser le mot de passe oublié")
    @GetMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestParam("login") String login) {


        return  ResponseEntity.ok(APIResponse.success(iAuthentification.reinitPassword(login)));
    }

    @Operation(summary = "Endpoint pour modifier le mot de passe d'un utilisateur")
    @PostMapping("/edit-user-password")
    public ResponseEntity<APIResponse> editUserPassword(@Valid @RequestBody ResetOrForgetFormDTO req) {
        return iAuthentification.updatePasswordFromInterface(req);
    }


}
