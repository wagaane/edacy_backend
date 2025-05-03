package sn.wagaane.task_app.presentation.web.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.wagaane.task_app.application.services.otp.OtpService;
import sn.wagaane.task_app.domain.model.utilisateur.Utilisateur;
import sn.wagaane.task_app.domain.repository.IUtilisateurRepository;
import sn.wagaane.task_app.presentation.dto.requests.task_app.OtpRequest;
import sn.wagaane.task_app.presentation.dto.responses.Response;

import java.util.Optional;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public String generate(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        // Here you can send the OTP by email or SMS
        return "OTP sent: " + otp; // Don't return this in production!
    }

    @PostMapping("/validate")
    public ResponseEntity<Response<Object>> validate(@RequestBody OtpRequest otpRequest) {
        boolean isValid = otpService.validateOtp(otpRequest.email(), otpRequest.otp());

        return isValid ? ResponseEntity.ok(Response.ok().setMessage("Votre compte a été activé  avec succès.")) : ResponseEntity.ok(Response.exception().setMessage("Code OTP invalide."));
    }
}
