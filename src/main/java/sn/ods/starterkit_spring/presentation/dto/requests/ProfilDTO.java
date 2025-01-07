package sn.ods.starterkit_spring.presentation.dto.requests;


import java.util.Set;


public record ProfilDTO(Long id, String code, String label, Set<MenuDTO> menu) {
}

