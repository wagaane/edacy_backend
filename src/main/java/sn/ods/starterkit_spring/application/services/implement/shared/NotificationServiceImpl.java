package sn.ods.starterkit_spring.application.services.implement.shared;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sn.ods.starterkit_spring.application.services.shared.file.INotificationService;
import sn.ods.starterkit_spring.domain.model.utilisateur.Utilisateur;
import sn.ods.starterkit_spring.infrastructure.config.security.jwt.JwtProvider;
import sn.ods.starterkit_spring.infrastructure.mails.MailService;
import sn.ods.starterkit_spring.presentation.dto.requests.authencation.LoginFormDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.mails.MailConnexionInfosDTO;
import sn.ods.starterkit_spring.presentation.dto.responses.mails.MailInfosDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {
    private static final String RESET_PASSWORD_LINK = "reset-password";
    private static final String FORGET_PASSWORD_LINK = "forgot-password";
    private static final String TOKEN_NAME_IN_MAIL_NOTIFICATION = "token";
    private static final String INSCRIPTION = "Inscription";
    private static final String Statut = "Activation/Désactivation compte";
    private static final String CREATION_COMPTE = "Création compte";
    private static final String MODIFICATION_COMPTE = "Modification compte";
    private static final String REINITIALISATION_MOT_DE_PASSE = "Réinitialisation mot de passe";
    private static final String NOM_ORGANISATION = "ODS StarterKit V1";
    private final JwtProvider jwtProvider;
    private final MailService mailService;
    private static final String SUIVI_PERMUTATION = "Suivi demande de permutation";

    @Value("${app.url.front}")
    private String appUrlFront;

    @Value("${url.logo.starterkit}")
    private String urlLogoStarterKit;

    @Override
    public void sendNotificationToNewUserRegistred(LoginFormDTO loginFormDTO, String action) {
        String lien = getLinkWithToken(loginFormDTO, action);
        String textMessage = """
                Bienvenue dans la plateforme MFPAI <span>....</span>.\s
                Merci de cliquer <a href=%s style="color: #3498DB;">ici</a> pour activer votre compte.
               SIGRH, vous remercie de votre confiance.
                """.formatted(lien);

        MailInfosDTO mailInfosDTO = new MailInfosDTO(null, textMessage, INSCRIPTION, null, loginFormDTO.login());
        sendEmail(mailInfosDTO);
    }


    public void sendNotificationStatut(Utilisateur utilisateur) {
        System.out.println("##############");
        String textMessage = """
               Bonjour votre compte viens d'être %s  <span>....</span>.\s
            
                """.formatted(utilisateur.getStatus());

        MailInfosDTO mailInfosDTO = new MailInfosDTO(null, textMessage, Statut, null, utilisateur.getEmail());
        sendEmail(mailInfosDTO);
    }

    @Override
    public void sendNotificationDemandeurPermutation(LoginFormDTO loginFormDTO, String traitant, long id, String statut) {
        System.out.println("## les infos "+ loginFormDTO.login()+ " ++++++ "+ traitant+" ----- "+statut);
        String textMessage = """
                 Ministère de la formation Professionnelle\s
                 Votre demande de permutation <strong> N° %d </strong> a été %s par %s.
                 """.formatted(id,statut,traitant);

        MailInfosDTO mailInfosDTO = new MailInfosDTO(null, textMessage, SUIVI_PERMUTATION, null, loginFormDTO.login());
        sendEmail(mailInfosDTO);
    }

    @Override
    public void sendNotificationMailOS(LoginFormDTO loginFormDTO, String file) {
        System.out.println("## send mail notif les infos "+ loginFormDTO.login()+ " ++++++ "+ file+" ----- ");
        String textMessage = """
                 Ministère de la formation Professionnelle\s
                 Nous vous envoyons en PJ l'ordre de service concernant les permutations.
                 """;

        MailInfosDTO mailInfosDTO = new MailInfosDTO(null, textMessage, INSCRIPTION, null, loginFormDTO.login());
        sendEmailOS(mailInfosDTO, file);
    }

    @Override
    public void sendNotificationToNewUserRegistredByAdmin(LoginFormDTO loginFormDTO, String action) {
        String lien = getLinkWithToken(loginFormDTO, action);
        String textMessage = """
                Votre compte utilisateur a été créé.
                Merci de cliquer <a href=%s style="color: #3498DB;">ici</a> pour activer votre compte.
                """.formatted(lien);
        MailInfosDTO mailInfosDTO = new MailInfosDTO(null, textMessage, CREATION_COMPTE, null, loginFormDTO.login());
        sendEmail(mailInfosDTO);
    }

    @Override
    public void sendNotificationToUserEdited(LoginFormDTO loginFormDTO, String action) {
        String lien = getLinkWithToken(loginFormDTO, action);
        String textMessage = """
                Votre compte utilisateur a été modifié.
                Merci de cliquer <a href=%s style="color: #3498DB;">ici</a> pour l'activer à nouveau.
                """.formatted(lien);

        MailInfosDTO mailInfosDTO = new MailInfosDTO(null, textMessage, MODIFICATION_COMPTE, null, loginFormDTO.login());
        sendEmail(mailInfosDTO);
    }

    @Override
    public void sendNotificationToUserForgetPassword(LoginFormDTO loginFormDTO, String action) {
        String lien = getLinkWithToken(loginFormDTO, action);
        String textMessage = """
                Votre mot de passe a été réinitialisé avec succès.
                Merci de cliquer <a href=%s style="color: #3498DB;">ici</a> pour choisir un nouveau mot de passe.
                """.formatted(lien);

        MailInfosDTO mailInfosDTO = new MailInfosDTO(null, textMessage, REINITIALISATION_MOT_DE_PASSE, null, loginFormDTO.login());
        sendEmail(mailInfosDTO);
    }

    @Override
    public void sendEmail(MailInfosDTO mailInfosDTO) {
        System.out.println("### send mail fonction");
        MailInfosDTO mailInfos = new MailInfosDTO(mailInfosDTO.id(), mailInfosDTO.originalText(), mailInfosDTO.subject(), getHtmlMessage(mailInfosDTO.originalText(), urlLogoStarterKit,NOM_ORGANISATION), mailInfosDTO.destinataire());


        mailService.sendMail(mailInfos);
    }

    @Override
    public void sendEmailOS(MailInfosDTO mailInfosDTO, String os) {
        System.out.println("### send mail fonction notif IA IEF ETAB");
        MailInfosDTO mailInfos = new MailInfosDTO(mailInfosDTO.id(), mailInfosDTO.originalText(), mailInfosDTO.subject(), getHtmlMessage(mailInfosDTO.originalText(), urlLogoStarterKit,NOM_ORGANISATION), mailInfosDTO.destinataire());
        mailService.sendMailOS(mailInfos,os);
    }

    private String getLinkWithToken(LoginFormDTO loginFormDTO, String action) {
        MailConnexionInfosDTO infos = new MailConnexionInfosDTO(loginFormDTO.login(), loginFormDTO.password(), action);
        String mailToken = jwtProvider.generateJwtMailToken(infos);
        return appUrlFront + "/" + RESET_PASSWORD_LINK + "?" + TOKEN_NAME_IN_MAIL_NOTIFICATION + "=" + mailToken;
    }

    private String getHtmlMessage(String text, String logo,String nom_organisation) {

        return """
                <!doctype html>
                  <html lang="fr">
                      <head>
                          <title> Hello world </title>
                          <meta http-equiv="X-UA-Compatible" content="IE=edge">
                          <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                          <meta name="viewport" content="width=device-width, initial-scale=1">
                      </head>
                      <body style="word-spacing:normal;">
                          <p>Bonjour,</p></br>
                          %s </br></br></br>
                          <img alt height="auto" width='auto' src="%s" style="border:0;display:block;outline:none;text-decoration:none;font-size:13px;  display: block; margin-left: auto;margin-right: auto;" /></br></br>
                          <div style="font-family:Roboto, Helvetica, sans-serif;font-size:18px;font-weight:500;line-height:24px;text-align:center;color:blue;">%s</div>
                      </body>
                  </html>
                  """.formatted(text, logo, nom_organisation);
    }
}
