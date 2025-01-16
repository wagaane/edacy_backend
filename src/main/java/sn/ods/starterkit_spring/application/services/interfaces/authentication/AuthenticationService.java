package sn.ods.starterkit_spring.application.services.interfaces.authentication;

import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.LoginFormDTO;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.ResetOrForgetFormDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.Response;

/**
 * @author Abdou Karim CISSOKHO
 * @created 09/01/2025-16:19
 * @project starterkit-spring
 */
public interface AuthenticationService {


    Response<Object> singIn(LoginFormDTO loginFormDTO);
    Response<Object> refreshToken(String token);
    Response<Object> authenticateUserWithFirstUrlConnexion(ResetOrForgetFormDTO formRequest);
    Response<Object> authenticateUserWithForgetPasswordUrlConnexion(ResetOrForgetFormDTO formRequest);
    Response<Object> reinitPassword(String login);
    //  Response<Object> editUserInfos(EditMonCompteDTO req);
    Response<Object> updatePasswordFromInterface(ResetOrForgetFormDTO form);

    Utilisateur getCurrentConnectedUser();
}
