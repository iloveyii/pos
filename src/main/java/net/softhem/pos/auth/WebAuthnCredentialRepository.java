package net.softhem.pos.auth;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import lombok.RequiredArgsConstructor;
import net.softhem.pos.auth.BiometricCredential;
import net.softhem.pos.auth.User;
import net.softhem.pos.auth.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebAuthnCredentialRepository implements CredentialRepository {

    private final net.softhem.pos.auth.BiometricCredentialRepository biometricCredentialRepository;
    private final UserRepository userRepository;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        return biometricCredentialRepository.findByUsername(username).stream()
                .map(credential -> PublicKeyCredentialDescriptor.builder()
                        .id(new ByteArray(java.util.Base64.getDecoder().decode(credential.getCredentialId())))
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new ByteArray(user.getUsername().getBytes()));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        String username = new String(userHandle.getBytes());
        return userRepository.findByUsername(username)
                .map(User::getUsername);
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        String credentialIdString = java.util.Base64.getEncoder().encodeToString(credentialId.getBytes());

        return biometricCredentialRepository.findByCredentialId(credentialIdString)
                .map(credential -> {
                    String username = new String(userHandle.getBytes());
                    return userRepository.findByUsername(username)
                            .map(user -> RegisteredCredential.builder()
                                    .credentialId(credentialId)
                                    .userHandle(new ByteArray(user.getUsername().getBytes()))
                                    .publicKeyCose(new ByteArray(java.util.Base64.getDecoder().decode(credential.getPublicKey())))
                                    .signatureCount(credential.getSignatureCount())
                                    .build())
                            .orElse(null);
                });
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        String credentialIdString = java.util.Base64.getEncoder().encodeToString(credentialId.getBytes());

        return biometricCredentialRepository.findByCredentialId(credentialIdString)
                .stream()
                .map(credential -> {
                    return userRepository.findById(credential.getUser().getId())
                            .map(user -> RegisteredCredential.builder()
                                    .credentialId(credentialId)
                                    .userHandle(new ByteArray(user.getUsername().getBytes()))
                                    .publicKeyCose(new ByteArray(java.util.Base64.getDecoder().decode(credential.getPublicKey())))
                                    .signatureCount(credential.getSignatureCount())
                                    .build())
                            .orElse(null);
                })
                .collect(Collectors.toSet());
    }
}