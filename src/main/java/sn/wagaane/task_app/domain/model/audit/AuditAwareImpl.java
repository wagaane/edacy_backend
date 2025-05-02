package sn.wagaane.task_app.domain.model.audit;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sn.wagaane.task_app.infrastructure.config.security.services.UtilisateurPrinciple;


import java.util.Optional;

@Component
public class AuditAwareImpl implements AuditorAware<Long> {

 @Override
    public @NotNull Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UtilisateurPrinciple userPrincipal = (UtilisateurPrinciple) authentication.getPrincipal();
            return Optional.of(userPrincipal.getUtilisateurInfo().id());
        } else return Optional.empty();
    }
}
