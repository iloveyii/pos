package net.softhem.pos.auth;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import net.softhem.pos.auth.BiometricCredentialRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class WebAuthnConfig {

    @Value("${webauthn.relying-party.id:localhost}")
    private String relyingPartyId;

    @Value("${webauthn.relying-party.name:Premium POS}")
    private String relyingPartyName;

    @Value("${webauthn.relying-party.origin:https://localhost:8080}")
    private String relyingPartyOrigin;

    // private final BiometricCredentialRepository credentialRepository;
    private final WebAuthnCredentialRepository credentialRepository;

    public WebAuthnConfig(WebAuthnCredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Bean
    public RelyingParty relyingParty() {
        RelyingPartyIdentity identity = RelyingPartyIdentity.builder()
                .id(relyingPartyId)
                .name(relyingPartyName)
                .build();

        return RelyingParty.builder()
                .identity(identity)
                .credentialRepository(credentialRepository)
                .origins(Collections.singleton(relyingPartyOrigin))
                .build();
    }
}