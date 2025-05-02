package sn.wagaane.task_app.presentation.dto.responses.authentication;


import sn.wagaane.task_app.domain.model.utilisateur.Menu;

import java.util.Set;

public record JwtDTO(String username, String token, String  refreshToken, String type, Set<Menu> menus) {
}
