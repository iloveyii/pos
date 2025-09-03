package net.softhem.pos.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;

import java.time.LocalDateTime;

@Entity
@Table(name = "biometric_credentials")
@Getter
@Setter
public class BiometricCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String credentialId;

    @Column(nullable = false, length = 2000)
    private String publicKey;

    private Long signatureCount;

    private LocalDateTime registeredOn;

    private String deviceType; // FINGERPRINT, FACIAL, IRIS

    @Column(nullable = false)
    private boolean enabled = true;
}
