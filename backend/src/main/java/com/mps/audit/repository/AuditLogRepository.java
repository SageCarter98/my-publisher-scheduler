package com.mps.audit.repository;
import com.mps.audit.model.AuditLog;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> { Page<AuditLog> findAllByOrganizationId(UUID organizationId, Pageable pageable); }
