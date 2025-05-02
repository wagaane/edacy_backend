package sn.wagaane.task_app.presentation.dto.requests.utilisateur;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgotPasswordRequest {
   private String email;
}
