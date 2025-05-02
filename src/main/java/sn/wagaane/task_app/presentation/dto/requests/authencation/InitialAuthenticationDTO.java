package sn.wagaane.task_app.presentation.dto.requests.authencation;

import jakarta.validation.constraints.NotBlank;

import static sn.wagaane.task_app.infrastructure.config.utils.i18n.I18nKeys.*;


public record InitialAuthenticationDTO(
        @NotBlank(message = EMAIL_OBLIGATOIRE)
        String login,
        @NotBlank(message = CONNEXION_LOGIN_NEW_PASSWORD_NON_VIDE)
        String newPassword,
        String passwordConfirmed) {


}
