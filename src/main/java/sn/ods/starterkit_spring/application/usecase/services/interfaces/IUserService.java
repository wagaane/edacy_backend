package sn.ods.starterkit_spring.application.usecase.services.interfaces;

import sn.ods.starterkit_spring.domain.model.Utilisateur;

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
    Utilisateur createUser(String email, String password, String firstName , String phoneNumber) ;

}