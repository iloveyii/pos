package net.softhem.pos.auth;

import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import lombok.RequiredArgsConstructor;
import net.softhem.pos.auth.BiometricAuthService;
import net.softhem.pos.auth.User;
import net.softhem.pos.auth.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BiometricAuthService biometricAuthService;
    private final UserRepository userRepository;

    @PostMapping("/biometric/start-registration")
    public ResponseEntity<?> startBiometricRegistration(
            @RequestParam String username
    ) {
        try {
            PublicKeyCredentialCreationOptions options =
                    biometricAuthService.startRegistration(username);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/biometric/finish-registration")
    public ResponseEntity<?> finishBiometricRegistration(
            @RequestParam String username,
            @RequestBody PublicKeyCredential credential
    ) {
        try {
            boolean success = biometricAuthService.finishRegistration(username, credential);
            if (success) {
                return ResponseEntity.ok().body(
                        Map.of("message", "Biometric registration successful")
                );
            } else {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Biometric registration failed")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/biometric/start-authentication")
    public ResponseEntity<?> startBiometricAuthentication(
            @RequestParam String username
    ) {
        try {
            // Check if user has biometric authentication enabled
            if (!biometricAuthService.isBiometricEnabled(username)) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Biometric authentication not enabled for this user")
                );
            }

            PublicKeyCredentialRequestOptions options =
                    biometricAuthService.startAuthentication(username);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/biometric/finish-authentication")
    public ResponseEntity<?> finishBiometricAuthentication(
            @RequestParam String username,
            @RequestBody PublicKeyCredential credential
    ) {
        try {
            boolean success = biometricAuthService.finishAuthentication(username, credential);
            if (success) {
                // Update last login date
                userRepository.findByUsername(username).ifPresent(user -> {
                    user.setLastLoginDate(java.time.LocalDateTime.now());
                    userRepository.save(user);
                });

                return ResponseEntity.ok().body(
                        Map.of("message", "Authentication successful", "username", username)
                );
            } else {
                return ResponseEntity.status(401).body(
                        Map.of("error", "Authentication failed")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping("/biometric/status")
    public ResponseEntity<?> getBiometricStatus(@RequestParam String username) {
        try {
            boolean isEnabled = biometricAuthService.isBiometricEnabled(username);
            long credentialCount = biometricAuthService.getBiometricCredentialCount(username);

            return ResponseEntity.ok().body(
                    Map.of(
                            "biometricEnabled", isEnabled,
                            "credentialCount", credentialCount,
                            "username", username
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/biometric/credentials")
    public ResponseEntity<?> removeBiometricCredentials(@RequestParam String username) {
        try {
            biometricAuthService.removeAllBiometricCredentials(username);
            return ResponseEntity.ok().body(
                    Map.of("message", "All biometric credentials removed for user: " + username)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/password")
    public ResponseEntity<?> passwordLogin(
            @RequestParam String username,
            @RequestParam String password
    ) {
        // Implement traditional password authentication
        // This would typically use Spring Security's authentication mechanism
        try {
            // For demonstration - in a real application, use proper password hashing and verification
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Simple password check (in real application, use password encoder)
            if (user.getPassword().equals(password)) {
                user.setLastLoginDate(java.time.LocalDateTime.now());
                userRepository.save(user);

                return ResponseEntity.ok().body(
                        Map.of("message", "Password authentication successful", "username", username)
                );
            } else {
                return ResponseEntity.status(401).body(
                        Map.of("error", "Invalid password")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}