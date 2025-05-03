package sn.wagaane.task_app.application.implement.shared.file;


import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;
import sn.wagaane.task_app.domain.model.utilisateur.ValidationUser;
import sn.wagaane.task_app.presentation.dto.requests.authencation.LoginFormDTO;
import sn.wagaane.task_app.presentation.dto.responses.mails.MailInfosDTO;

public interface INotificationService {
   void sendNotificationToNewUserRegistred(LoginFormDTO loginFormDTO, String action);

    void sendNotificationToNewUserRegistredByAdmin(LoginFormDTO loginFormDTO, String action);
    void sendNotificationToUserEdited(LoginFormDTO loginFormDTO, String action);
    void sendNotificationToUserForgetPassword(LoginFormDTO loginFormDTO, String action);
    void sendOtpCodeToRegisteredUser(String email);
    void sendEmail(MailInfosDTO mailInfosDTO);
    void sendNotificationStatut(Utilisateur utilisateur);

    void envoyer(ValidationUser validationUser);
}
