package net.softhem.pos.auth;

import lombok.RequiredArgsConstructor;
import net.softhem.pos.auth.BiometricCredential;
import net.softhem.pos.auth.User;
import net.softhem.pos.auth.BiometricCredentialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BiometricCredentialService {

    private final BiometricCredentialRepository credentialRepository;

    public Optional<BiometricCredential> findByCredentialId(String credentialId) {
        return credentialRepository.findByCredentialId(credentialId);
    }

    public List<BiometricCredential> findByUser(User user) {
        return credentialRepository.findByUser(user);
    }

    public List<BiometricCredential> findByUserId(Long userId) {
        return credentialRepository.findByUserId(userId);
    }

    public List<BiometricCredential> findByUsername(String username) {
        return credentialRepository.findByUsername(username);
    }

    public BiometricCredential saveCredential(BiometricCredential credential) {
        if (credential.getRegisteredOn() == null) {
            credential.setRegisteredOn(LocalDateTime.now());
        }
        return credentialRepository.save(credential);
    }

    @Transactional
    public void deleteByUser(User user) {
        credentialRepository.deleteByUser(user);
    }

    @Transactional
    public void deleteByIdAndUser(Long id, User user) {
        credentialRepository.deleteByIdAndUser(id, user);
    }

    public boolean userHasBiometricCredentials(User user) {
        return credentialRepository.existsByUser(user);
    }

    public boolean userHasDeviceType(User user, String deviceType) {
        return credentialRepository.existsByUserAndDeviceType(user, deviceType);
    }

    public long countUserCredentials(User user) {
        return credentialRepository.countByUser(user);
    }

    public Optional<BiometricCredential> findMostRecentCredential(User user) {
        return credentialRepository.findTopByUserOrderByRegisteredOnDesc(user);
    }

    @Transactional
    public void disableAllUserCredentials(Long userId) {
        credentialRepository.disableAllByUserId(userId);
    }

    @Transactional
    public void enableAllUserCredentials(Long userId) {
        credentialRepository.enableAllByUserId(userId);
    }

    public List<BiometricCredential> findLowUsageCredentials(Long minSignatureCount) {
        return credentialRepository.findLowUsageCredentials(minSignatureCount);
    }

    @Transactional
    public void updateSignatureCount(String credentialId, Long signatureCount) {
        credentialRepository.updateSignatureCount(credentialId, signatureCount);
    }
}