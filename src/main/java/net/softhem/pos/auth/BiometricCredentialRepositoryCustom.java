package net.softhem.pos.auth;

import java.util.List;

public interface BiometricCredentialRepositoryCustom {
    List<BiometricCredential> findCredentialsByUserWithDeviceType(Long userId, String deviceType);
    int bulkUpdateSignatureCount(Long userId, Long newSignatureCount);
}