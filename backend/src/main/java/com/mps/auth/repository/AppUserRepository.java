package com.mps.auth.repository;
import com.mps.auth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findFirstByEmailIgnoreCase(String email);
    Optional<AppUser> findByIdAndOrganizationId(UUID id, UUID organizationId);
    List<AppUser> findAllByOrganizationIdOrderByLastNameAscFirstNameAsc(UUID organizationId);
    boolean existsByOrganizationIdAndEmailIgnoreCase(UUID organizationId, String email);
}
