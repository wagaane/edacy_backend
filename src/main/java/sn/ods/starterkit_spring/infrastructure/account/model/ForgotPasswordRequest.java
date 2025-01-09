package sn.ods.starterkit_spring.infrastructure.account.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgotPasswordRequest {
   private String email;
}
