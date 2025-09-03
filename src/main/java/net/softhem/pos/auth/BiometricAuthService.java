package net.softhem.pos.auth;

import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import net.softhem.pos.auth.BiometricCredential;
import net.softhem.pos.auth.User;
import net.softhem.pos.auth.BiometricCredentialRepository;
import net.softhem.pos.auth.UserRepository;
import org.springframework.stereotype.Service;
import com.yubico.webauthn.data.ByteArray;
import java.util.Optional;
import static com.yubico.webauthn.data.ByteArray.*;

@Service
@RequiredArgsConstructor
public class BiometricAuthService {

    private final UserRepository userRepository;
    private final BiometricCredentialRepository credentialRepository;
    private final RelyingParty relyingParty;

    public PublicKeyCredentialCreationOptions startRegistration(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        ByteArray id = ByteArray.fromBase64(user.getUsername());

        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getUsername())
                .displayName(user.getUsername())
                .id(id)
                .build();

        return relyingParty.startRegistration(
                StartRegistrationOptions.builder()
                        .user(userIdentity)
                        .build()
        );
    }

    public boolean finishRegistration(
            String username,
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> credential
    ) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = userOptional.get();

            ByteArray id = ByteArray.fromBase64(user.getUsername());

            RegistrationResult result = relyingParty.finishRegistration(
                    FinishRegistrationOptions.builder()
                            .request(relyingParty.startRegistration(
                                    StartRegistrationOptions.builder()
                                            .user(UserIdentity.builder()
                                                    .name(user.getUsername())
                                                    .displayName(user.getUsername())
                                                    .id(id)
                                                    .build())
                                            .build()))
                            .response(credential)
                            .build()
            );

            if (result.isUserVerified()) {
                BiometricCredential bioCredential = new BiometricCredential();
                bioCredential.setUser(user);
                bioCredential.setCredentialId(result.getKeyId().getId().getBase64());
                bioCredential.setPublicKey(result.getPublicKeyCose().getBase64());
                bioCredential.setSignatureCount(result.getSignatureCount());
                bioCredential.setRegisteredOn(java.time.LocalDateTime.now());

                credentialRepository.save(bioCredential);

                user.setBiometricEnabled(true);
                userRepository.save(user);

                return true;
            }

            return false;
        } catch (RegistrationFailedException e) {
            throw new RuntimeException("Registration failed", e);
        }
    }

    public PublicKeyCredentialRequestOptions startAuthentication(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        return relyingParty.startAssertion(
                StartAssertionOptions.builder()
                        .username(Optional.of(user.getUsername()))
                        .build()
        ).getPublicKeyCredentialRequestOptions();
    }

    public boolean finishAuthentication(
            String username,
            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> credential
    ) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = userOptional.get();

            AssertionResult result = relyingParty.finishAssertion(
                    FinishAssertionOptions.builder()
                            .request(relyingParty.startAssertion(
                                    StartAssertionOptions.builder()
                                            .username(Optional.of(user.getUsername()))
                                            .build()))
                            .response(credential)
                            .build()
            );

            if (result.isSuccess()) {
                // Update signature count
                Optional<BiometricCredential> bioCredentialOpt = credentialRepository
                        .findByCredentialId(result.getCredentialId().getBase64());

                if (bioCredentialOpt.isPresent()) {
                    BiometricCredential bioCredential = bioCredentialOpt.get();
                    bioCredential.setSignatureCount(result.getSignatureCount());
                    credentialRepository.save(bioCredential);
                }

                return true;
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

    public boolean isBiometricEnabled(String username) {
        return true;
    }

    public void removeAllBiometricCredentials(String username) {
    }

    public long getBiometricCredentialCount(String username) {
        return 2L;
    }
}
