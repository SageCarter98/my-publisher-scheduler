package com.mps.organization.repository;

import com.mps.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByNameIgnoreCase(String name);
}
