package net.softhem.pos.auth;

import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import lombok.RequiredArgsConstructor;
import net.softhem.pos.auth.BiometricAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BiometricAuthService biometricAuthService;

    @PostMapping("/biometric/start-registration")
    public ResponseEntity<PublicKeyCredentialCreationOptions> startBiometricRegistration(
            @RequestParam String username
    ) {
        try {
            PublicKeyCredentialCreationOptions options =
                    biometricAuthService.startRegistration(username);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/biometric/finish-registration")
    public ResponseEntity<?> finishBiometricRegistration(
            @RequestParam String username,
            @RequestBody PublicKeyCredential credential
    ) {
        try {
            boolean success = biometricAuthService.finishRegistration(username, credential);
            return success ?
                    ResponseEntity.ok().build() :
                    ResponseEntity.badRequest().body("Registration failed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/biometric/start-authentication")
    public ResponseEntity<PublicKeyCredentialRequestOptions> startBiometricAuthentication(
            @RequestParam String username
    ) {
        try {
            PublicKeyCredentialRequestOptions options =
                    biometricAuthService.startAuthentication(username);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/biometric/finish-authentication")
    public ResponseEntity<?> finishBiometricAuthentication(
            @RequestParam String username,
            @RequestBody PublicKeyCredential credential
    ) {
        try {
            boolean success = biometricAuthService.finishAuthentication(username, credential);
            return success ?
                    ResponseEntity.ok().build() :
                    ResponseEntity.status(401).body("Authentication failed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password")
    public ResponseEntity<?> passwordLogin(
            @RequestParam String username,
            @RequestParam String password
    ) {
        // Implement traditional password authentication
        // This would typically use Spring Security's authentication mechanism
        return ResponseEntity.ok().build();
    }
}
