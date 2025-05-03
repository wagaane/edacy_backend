package sn.wagaane.task_app.application.implement.authentication;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.wagaane.task_app.application.interfaces.authentication.AuthenticationService;
import sn.wagaane.task_app.application.implement.shared.file.INotificationService;
import sn.wagaane.task_app.domain.enums.ProfileEnum;
import sn.wagaane.task_app.domain.model.utilisateur.Menu;
import sn.wagaane.task_app.domain.model.utilisateur.Profile;
import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;
import sn.wagaane.task_app.domain.repository.utilisateur.MenuRepository;
import sn.wagaane.task_app.domain.repository.utilisateur.ProfilRepository;
import sn.wagaane.task_app.domain.repository.utilisateur.UtilisateurRepository;
import sn.wagaane.task_app.infrastructure.config.exceptions.APIException;
import sn.wagaane.task_app.infrastructure.config.security.jwt.JwtProvider;
import sn.wagaane.task_app.infrastructure.config.security.services.LoginAttemptService;
import sn.wagaane.task_app.infrastructure.config.utils.UtilityClass;
import sn.wagaane.task_app.infrastructure.config.utils.i18n.I18nTranslate;
import sn.wagaane.task_app.presentation.dto.requests.authencation.ForgetFormDTO;
import sn.wagaane.task_app.presentation.dto.requests.authencation.InitialAuthenticationDTO;
import sn.wagaane.task_app.presentation.dto.requests.authencation.LoginFormDTO;
import sn.wagaane.task_app.presentation.dto.requests.authencation.ResetOrForgetFormDTO;
import sn.wagaane.task_app.presentation.dto.requests.task_app.RegisterRequest;
import sn.wagaane.task_app.presentation.dto.responses.APIMessage;
import sn.wagaane.task_app.presentation.dto.responses.APIResponse;
import sn.wagaane.task_app.presentation.dto.responses.Response;
import sn.wagaane.task_app.presentation.dto.responses.Status;
import sn.wagaane.task_app.presentation.dto.responses.authentication.JwtDTO;
import sn.wagaane.task_app.presentation.mappers.utilisateur.UserMapperForAdminMapper;
import sn.wagaane.task_app.presentation.mappers.utilisateur.UtilisateurMapper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static sn.wagaane.task_app.infrastructure.config.utils.i18n.I18nKeys.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserMapperForAdminMapper userMapperForAdminMapper;
    private final JwtProvider jwtProvider;
    private final I18nTranslate i18nTranslat;
    private final LoginAttemptService loginAttemptService;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder encoder;
    private final INotificationService notificationService;
    private final MenuRepository menuRepository;
    private final PasswordEncoder passwordEncoder;
    private  final UtilisateurMapper utilisateurMapper;

    public static final String BEARER = "Bearer";
   // public static final String REFRESH_TOKEN = "Refresh token";
    private static final String RESET_PASSWORD = "RESET_PASSWORD";
    private final ProfilRepository profilRepository;

    @Override
    public JwtDTO singIn(LoginFormDTO loginFormDTO) {
        Authentication authentication = authenticateUser(loginFormDTO);
        if (authentication == null) {

            throw new APIException(APIMessage.LOGIN_BAD_CREDENTIALS);

        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtProvider.generateToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(jwt);

        System.out.println("user: " + userDetails.getUsername());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(userDetails.getUsername());

        Set<Menu> menues = menuRepository.findByProfiles(utilisateur.getProfiles().stream().findFirst().get());

        JwtDTO response = new JwtDTO(userDetails.getUsername(), jwt, refreshToken, BEARER, menues);
        loginAttemptService.loginSucceeded(loginFormDTO.login());
        return response;
    }
 @Override
    public Response<Object> login(LoginFormDTO loginFormDTO) {
        Authentication authentication = authenticateUser(loginFormDTO);
        if (authentication == null) {

            return Response.exception().setMessage("Information de connection invalides.");

        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtProvider.generateToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(jwt);

        System.out.println("user: " + userDetails.getUsername());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(userDetails.getUsername());

        Set<Menu> menues = menuRepository.findByProfiles(utilisateur.getProfiles().stream().findFirst().get());

        JwtDTO response = new JwtDTO(userDetails.getUsername(), jwt, refreshToken, BEARER, menues);
        loginAttemptService.loginSucceeded(loginFormDTO.login());
        return Response.ok().setPayload(response).setMessage("Connexion reussi avec succès.");
    }

    private Authentication authenticateUser(LoginFormDTO loginFormDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginFormDTO.login(), loginFormDTO.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(loginFormDTO.login());
            return null;
        }
    }

    @Override
    public JwtDTO refreshToken(String token) {
        if (!jwtProvider.validationJwtToken(token)) {
            throw new APIException(APIMessage.CONNEXION_TOKEN_INVALIDE);
        }
        String jwtRefresh = jwtProvider.generateRefreshToken(token);
        return new  JwtDTO(jwtProvider.getUserNameFromJwtToken(token), jwtRefresh, null, BEARER, null);

    }

    @Override
    @Transactional
    public Response<Object> register(RegisterRequest request) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findUtilisateurByEmail(request.email());
        if (optionalUtilisateur.isPresent()) {
            return Response.exception().setMessage("Email existe déjà.");
        }
        Utilisateur utilisateur = new Utilisateur();
        Set<Profile> profiles = new HashSet<>();
        profiles.add(profilRepository.findByCode(ProfileEnum.USER.name()).get());
        utilisateur.setProfiles(profiles);
        utilisateur.setEmail(request.email());
        utilisateur.setPassword(encoder.encode(request.password()));
        utilisateur.setNom(request.nom());
        utilisateur.setPrenom(request.prenom());
        utilisateurRepository.save(utilisateur);
        notificationService.sendOtpCodeToRegisteredUser(utilisateur.getEmail());
        return Response.ok().setMessage("Inscription effectuée avec succès.");
    }

    @Override
      @Transactional
      public ResponseEntity<APIResponse> authenticateUserWithFirstUrlConnexion(InitialAuthenticationDTO formRequest) {
        if (isValidPassword(formRequest.newPassword())) {
            Optional<Utilisateur> userOptional = utilisateurRepository.findUtilisateurByEmail(formRequest.login());
            if (userOptional.isEmpty()) {

                APIResponse response = APIResponse.error(new APIException(APIMessage.ACCOUNT_NOT_FOUND));
                return ResponseEntity.ok(response);

            }

            Utilisateur user = userOptional.get();
            if (Boolean.FALSE.equals(user.getFirstLog())) {
                APIResponse response = APIResponse.error(APIMessage.USER_COMPTE_ALREADY_CONNECTED);
                return ResponseEntity.ok(response);

            }

            if (Boolean.FALSE.equals(user.getStatus())) {
                APIResponse response = APIResponse.error(APIMessage.USER_COMPTE_DISABLED);
                return ResponseEntity.ok(response);
            }

            var passwordUpdateResponse = updatePassword(formRequest);
            if (passwordUpdateResponse.getStatus().equals(Status.OK)) {
                LoginFormDTO loginForm = new LoginFormDTO(formRequest.login(), formRequest.newPassword());
                APIResponse response = APIResponse.success(singIn(loginForm));
                return ResponseEntity.ok(response);
            }


            APIResponse response = APIResponse.success(passwordUpdateResponse);
            return ResponseEntity.ok(response);
        }
        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    @Transactional
    public JwtDTO authenticateUserWithForgetPasswordUrlConnexion(ForgetFormDTO formRequest) {
        if (!formRequest.newPassword().equals(formRequest.passwordConfirmed())) {
            throw new APIException(APIMessage.NON_MATCH_PASSWORD_CONFIRMED);
        }

        if (isValidPassword(formRequest.newPassword())) {

            Optional<Utilisateur> userOptional = utilisateurRepository.findUtilisateurByEmail(formRequest.login());
            if (userOptional.isEmpty()) {
               throw new APIException(APIMessage.ACCOUNT_NOT_FOUND);
            }

            var passwordUpdateResponse = updatePassword(formRequest);

            if (passwordUpdateResponse.getStatus().equals(Status.OK)) {

                LoginFormDTO loginForm = new LoginFormDTO(formRequest.login(), formRequest.newPassword());
                return singIn(loginForm);
            }
        }

        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    public  Utilisateur reinitPassword(String login) {
        Optional<Utilisateur> userOptional = utilisateurRepository.findUtilisateurByEmail(login);
        if (userOptional.isPresent()) {
            Utilisateur utilisateur = userOptional.get();
            utilisateur.setFirstLog(true);
            notificationService.sendNotificationToUserForgetPassword(
                    new LoginFormDTO(utilisateur.getEmail(), utilisateur.getPassword()), RESET_PASSWORD);
         return utilisateur;

        }
       throw new APIException(APIMessage.ACCOUNT_NOT_FOUND);
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponse> updatePasswordFromInterface(ResetOrForgetFormDTO form) {
        if (isValidPassword(form.newPassword())) {
            Optional<Utilisateur> userOptional = utilisateurRepository.findUtilisateurByEmail(form.login());
            if (userOptional.isPresent()) {
                Utilisateur utilisateur = userOptional.get();
                if (!encoder.matches(form.password(), utilisateur.getPassword())) {
                    throw new APIException(APIMessage.PASSWORD_OLD_PASSWORD_ARE_NOT_IDENTIQUE);
                }

                if (encoder.matches(form.newPassword(), utilisateur.getPassword())) {
                    throw new APIException(APIMessage.PASSWORD_OLD_PASSWORD_ARE_IDENTIQUE);
                }

                if (new UtilityClass.PasswordUtility().validate(form.newPassword())) {

                    System.out.println("password " + form.newPassword());

                    utilisateur.setPassword(passwordEncoder.encode(form.newPassword()));
                } else {
                    throw new APIException(APIMessage.NON_STRONG_PASSWORD);
                }

             //   utilisateur.setPassword(encoder.encode(form.newPassword()));
                Utilisateur updatedUser = utilisateurRepository.save(utilisateur);

                APIResponse response = APIResponse.success(updatedUser);
                return ResponseEntity.ok(response);
            }
            throw new APIException(APIMessage.ACCOUNT_NOT_FOUND);
        }
        throw new APIException(APIMessage.NON_STRONG_PASSWORD);
    }

    @Override
    public Utilisateur getCurrentConnectedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findUtilisateurByEmail(username).orElseThrow();
    }

    private boolean isValidPassword(String password) {
        return new UtilityClass.PasswordUtility().validate(password);
    }

    private Response<Object> updatePassword(Object form) {
        Utilisateur utilisateur = getUserFromForm(form);
        if (utilisateur != null) {



            utilisateur.setPassword(encoder.encode(getNewPassword(form)));

           // utilisateur.setFirstLog(false);
            Utilisateur updatedUser = utilisateurRepository.save(utilisateur);
            return Response.ok().setMessage(i18nTranslat.toTranslate(MOT_DE_PASSE_MODIFIER_AVEC_SUCCES))
                    .setPayload(updatedUser);
        }
        return Response.notFound().setMessage(i18nTranslat.toTranslate(UTILISATEUR_ABSENT));
    }

    private Utilisateur getUserFromForm(Object form) {
        String email = null;
        if (form instanceof InitialAuthenticationDTO) {
            email = ((InitialAuthenticationDTO) form).login();
        } else if (form instanceof ForgetFormDTO) {
            email = ((ForgetFormDTO) form).login();
        }
        return utilisateurRepository.findUtilisateurByEmail(email).orElse(null);
    }


    private String getNewPassword(Object form) {
        if (!(form instanceof InitialAuthenticationDTO || form instanceof ForgetFormDTO)) {
            return null;
        }

        String newPassword = (form instanceof InitialAuthenticationDTO)
                ? ((InitialAuthenticationDTO) form).newPassword()
                : ((ForgetFormDTO) form).newPassword();

        if (!new UtilityClass.PasswordUtility().validate(newPassword)) {
            throw new APIException(APIMessage.NON_STRONG_PASSWORD);
        }

        return newPassword;
    }
}
