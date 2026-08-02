package com.mps.organization.repository;
import com.mps.organization.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findAllByOrganizationIdAndStatusOrderByName(UUID organizationId, String status);
    Optional<Department> findByIdAndOrganizationId(UUID id, UUID organizationId);
    boolean existsByOrganizationIdAndNameIgnoreCaseAndStatusNot(UUID organizationId, String name, String status);
}
