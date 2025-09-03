package net.softhem.pos.auth;

import net.softhem.pos.auth.BiometricCredential;
import net.softhem.pos.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BiometricCredentialRepository extends JpaRepository<BiometricCredential, Long> {

    /**
     * Find a biometric credential by its credential ID
     * @param credentialId The base64 encoded credential ID
     * @return Optional containing the credential if found
     */
    Optional<BiometricCredential> findByCredentialId(String credentialId);

    /**
     * Find all biometric credentials for a specific user
     * @param user The user entity
     * @return List of biometric credentials for the user
     */
    List<BiometricCredential> findByUser(User user);

    /**
     * Find all biometric credentials for a user by user ID
     * @param userId The user ID
     * @return List of biometric credentials for the user
     */
    List<BiometricCredential> findByUserId(Long userId);

    /**
     * Find all biometric credentials for a user by username
     * @param username The username
     * @return List of biometric credentials for the user
     */
    @Query("SELECT bc FROM BiometricCredential bc WHERE bc.user.username = :username")
    List<BiometricCredential> findByUsername(@Param("username") String username);

    /**
     * Find all enabled biometric credentials for a user
     * @param user The user entity
     * @return List of enabled biometric credentials
     */
    List<BiometricCredential> findByUserAndEnabledTrue(User user);

    /**
     * Find biometric credentials by device type
     * @param deviceType The device type (FINGERPRINT, FACIAL, IRIS)
     * @return List of credentials for the specified device type
     */
    List<BiometricCredential> findByDeviceType(String deviceType);

    /**
     * Find biometric credentials by device type for a specific user
     * @param user The user entity
     * @param deviceType The device type
     * @return List of credentials for the user and device type
     */
    List<BiometricCredential> findByUserAndDeviceType(User user, String deviceType);

    /**
     * Check if a user has any biometric credentials registered
     * @param user The user entity
     * @return true if the user has at least one credential
     */
    boolean existsByUser(User user);

    /**
     * Check if a user has biometric credentials of a specific type
     * @param user The user entity
     * @param deviceType The device type
     * @return true if the user has credentials of the specified type
     */
    boolean existsByUserAndDeviceType(User user, String deviceType);

    /**
     * Count the number of biometric credentials for a user
     * @param user The user entity
     * @return The count of credentials
     */
    long countByUser(User user);

    /**
     * Delete all biometric credentials for a user
     * @param user The user entity
     */
    void deleteByUser(User user);

    /**
     * Delete a specific credential by its ID and user
     * @param id The credential ID
     * @param user The user entity
     */
    void deleteByIdAndUser(Long id, User user);

    /**
     * Find the most recently registered credential for a user
     * @param user The user entity
     * @return Optional containing the most recent credential
     */
    @Query("SELECT bc FROM BiometricCredential bc WHERE bc.user = :user ORDER BY bc.registeredOn DESC")
    Optional<BiometricCredential> findTopByUserOrderByRegisteredOnDesc(@Param("user") User user);

    /**
     * Find credentials that haven't been used recently (based on signature count)
     * @param minSignatureCount The minimum signature count threshold
     * @return List of credentials with low usage
     */
    @Query("SELECT bc FROM BiometricCredential bc WHERE bc.signatureCount < :minSignatureCount")
    List<BiometricCredential> findLowUsageCredentials(@Param("minSignatureCount") Long minSignatureCount);

    /**
     * Update the signature count for a credential
     * @param credentialId The credential ID
     * @param signatureCount The new signature count
     */
    @Query("UPDATE BiometricCredential bc SET bc.signatureCount = :signatureCount WHERE bc.credentialId = :credentialId")
    void updateSignatureCount(@Param("credentialId") String credentialId, @Param("signatureCount") Long signatureCount);

    /**
     * Disable all credentials for a user
     * @param userId The user ID
     */
    @Query("UPDATE BiometricCredential bc SET bc.enabled = false WHERE bc.user.id = :userId")
    void disableAllByUserId(@Param("userId") Long userId);

    /**
     * Enable all credentials for a user
     * @param userId The user ID
     */
    @Query("UPDATE BiometricCredential bc SET bc.enabled = true WHERE bc.user.id = :userId")
    void enableAllByUserId(@Param("userId") Long userId);


}
