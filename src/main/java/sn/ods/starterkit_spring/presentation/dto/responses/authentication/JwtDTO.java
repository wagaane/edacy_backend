package sn.ods.starterkit_spring.presentation.dto.responses.authentication;


import sn.ods.starterkit_spring.domain.model.utilisateur.Menu;

import java.util.Set;

public record JwtDTO(String username, String token, String  refreshToken, String type, Set<Menu> menus) {
}
