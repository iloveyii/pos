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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "credential_id", nullable = false, length = 512)
    private String credentialId;

    @Column(name = "public_key", nullable = false, length = 2000)
    private String publicKey;

    @Column(name = "signature_count")
    private Long signatureCount = 0L;

    @Column(name = "registered_on")
    private LocalDateTime registeredOn;

    @Column(name = "device_type", length = 50)
    private String deviceType; // FINGERPRINT, FACIAL, IRIS, etc.

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "aaguid", length = 36)
    private String aaguid; // Authenticator Attestation GUID

    @Column(name = "attestation_type", length = 50)
    private String attestationType;

    @Column(name = "transports", length = 100)
    private String transports; // USB, NFC, BLE, etc.

    @PrePersist
    protected void onCreate() {
        if (registeredOn == null) {
            registeredOn = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUsed = LocalDateTime.now();
    }
}