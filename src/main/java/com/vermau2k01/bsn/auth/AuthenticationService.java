package com.vermau2k01.bsn.auth;


import com.vermau2k01.bsn.email.EmailService;
import com.vermau2k01.bsn.email.EmailTemplateName;
import com.vermau2k01.bsn.forgotPassword.PasswordResetToken;
import com.vermau2k01.bsn.forgotPassword.PasswordResetTokenRepository;
import com.vermau2k01.bsn.security.JwtService;
import com.vermau2k01.bsn.token.Token;
import com.vermau2k01.bsn.token.TokenRepository;
import com.vermau2k01.bsn.user.RoleRepository;
import com.vermau2k01.bsn.user.UserRepository;
import com.vermau2k01.bsn.user.Users;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Value("${application.mailing.frontend.reset-url}")
    private String resetUrl;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegistrationRequest request) throws MessagingException {

        var userRole  = roleRepository.findByRole("USER")
                .orElseThrow(()->
                        new IllegalStateException("Role USER was not initalized"));

        var user = Users
                .builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .passcode(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .role(List.of(userRole))
                .build();

        userRepository.save(user);
        // Send validation Email
        sendValidationEmail(user);

    }

    private void sendValidationEmail(Users user) throws MessagingException {

        var newToken = generateAndSaveActivationToken(user);
        //send email
        emailService.sendEmail(user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,newToken,
                "Account Activation");

    }

    private String generateAndSaveActivationToken(Users user) {
        String generatedToken = generatedActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generatedActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse autheticate(AuthenticationRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String,Object>();
        var user = ((Users)auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(claims,user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }


    public void activateAccount(String token) throws MessagingException {

        Token savedToken = tokenRepository.findByToken(token).orElseThrow(()->new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation Token expired. A new token has been send");
        }
        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(()->new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public void requestPasswordReset(PasswordResetRequest request) throws MessagingException {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetToken resetToken = generateAndSavePasswordResetToken(user);

        emailService.sendEmail(user.getEmail(),
                user.getFullName(),
                EmailTemplateName.RESET_PASSWORD,
                resetUrl, resetToken.getToken(),
                "Password Reset Request");
    }

    private PasswordResetToken generateAndSavePasswordResetToken(Users user) {
        String token = generatedActivationCode();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        passwordResetTokenRepository.save(resetToken);
        return resetToken;
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(resetToken.getExpiredAt())) {
            throw new RuntimeException("Token expired");
        }

        Users user = userRepository.findById(resetToken.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasscode(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetToken.setValidatedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);
    }
}
