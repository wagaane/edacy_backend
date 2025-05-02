package sn.wagaane.task_app.presentation.dto.requests.utilisateur;


import java.util.Set;


public record ProfilDTO(Long id, String code, String label, Set<MenuDTO> menu) {
}

