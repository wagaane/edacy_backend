package sn.ods.starterkit_spring.infrastructure.account.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
