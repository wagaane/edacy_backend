package sn.ods.starterkit_spring.application.services.shared.file;


import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.LoginFormDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.mails.MailInfosDTO;

public interface INotificationService {
   void sendNotificationToNewUserRegistred(LoginFormDTO loginFormDTO, String action);

    void sendNotificationToNewUserRegistredByAdmin(LoginFormDTO loginFormDTO, String action);
    void sendNotificationToUserEdited(LoginFormDTO loginFormDTO, String action);
    void sendNotificationToUserForgetPassword(LoginFormDTO loginFormDTO, String action);
    void sendEmail(MailInfosDTO mailInfosDTO);
    void sendNotificationStatut(Utilisateur utilisateur);

}
