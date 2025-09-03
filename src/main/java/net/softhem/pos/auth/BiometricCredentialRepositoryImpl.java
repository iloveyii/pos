package net.softhem.pos.auth;

import net.softhem.pos.auth.BiometricCredential;
import net.softhem.pos.auth.BiometricCredentialRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class BiometricCredentialRepositoryImpl implements BiometricCredentialRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BiometricCredential> findCredentialsByUserWithDeviceType(Long userId, String deviceType) {
        return entityManager.createQuery(
                        "SELECT bc FROM BiometricCredential bc WHERE bc.user.id = :userId AND bc.deviceType = :deviceType",
                        BiometricCredential.class)
                .setParameter("userId", userId)
                .setParameter("deviceType", deviceType)
                .getResultList();
    }

    @Override
    public int bulkUpdateSignatureCount(Long userId, Long newSignatureCount) {
        return entityManager.createQuery(
                        "UPDATE BiometricCredential bc SET bc.signatureCount = :newSignatureCount WHERE bc.user.id = :userId")
                .setParameter("newSignatureCount", newSignatureCount)
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
