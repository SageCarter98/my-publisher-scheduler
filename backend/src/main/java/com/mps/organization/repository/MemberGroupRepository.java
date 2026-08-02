package com.mps.organization.repository;
import com.mps.organization.model.MemberGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface MemberGroupRepository extends JpaRepository<MemberGroup, UUID> {
    List<MemberGroup> findAllByOrganizationIdAndStatusOrderByName(UUID organizationId, String status);
    Optional<MemberGroup> findByIdAndOrganizationId(UUID id, UUID organizationId);
    boolean existsByOrganizationIdAndNameIgnoreCaseAndStatusNot(UUID organizationId, String name, String status);
}
