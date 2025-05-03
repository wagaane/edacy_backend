package sn.wagaane.task_app.application.interfaces.authentication;

import org.springframework.http.ResponseEntity;
import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;
import sn.wagaane.task_app.presentation.dto.requests.authencation.ForgetFormDTO;
import sn.wagaane.task_app.presentation.dto.requests.authencation.InitialAuthenticationDTO;
import sn.wagaane.task_app.presentation.dto.requests.authencation.LoginFormDTO;
import sn.wagaane.task_app.presentation.dto.requests.authencation.ResetOrForgetFormDTO;
import sn.wagaane.task_app.presentation.dto.requests.task_app.RegisterRequest;
import sn.wagaane.task_app.presentation.dto.responses.APIResponse;
import sn.wagaane.task_app.presentation.dto.responses.Response;
import sn.wagaane.task_app.presentation.dto.responses.authentication.JwtDTO;

/**
 * @author Abdou Karim CISSOKHO
 * @created 09/01/2025-16:19
 * @project starterkit-spring
 */
public interface AuthenticationService {

    JwtDTO singIn(LoginFormDTO loginFormDTO);
    Response<Object> login(LoginFormDTO loginFormDTO);
    JwtDTO refreshToken(String token);
    Response<Object> register(RegisterRequest request);
    ResponseEntity<APIResponse> authenticateUserWithFirstUrlConnexion(InitialAuthenticationDTO formRequest);
    JwtDTO authenticateUserWithForgetPasswordUrlConnexion(ForgetFormDTO formRequest);
    Utilisateur reinitPassword(String login);
    //  Response<Object> editUserInfos(EditMonCompteDTO req);
    ResponseEntity<APIResponse> updatePasswordFromInterface(ResetOrForgetFormDTO form);

    Utilisateur getCurrentConnectedUser();
}
