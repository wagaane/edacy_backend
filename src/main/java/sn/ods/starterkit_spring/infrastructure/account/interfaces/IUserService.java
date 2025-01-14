package sn.ods.starterkit_spring.infrastructure.account.interfaces;

/**
 * @author Abdou Karim CISSOKHO
 * @created 07/01/2025-13:01
 * @project starterkit-spring
 */

public interface IUserService {
    void generatePasswordResetToken(String email) ;
    void resetPassword(String token, String newPassword) ;
    void invalidateToken(String token) ;
    void sendPasswordResetEmail(String email) ;
}