package com.mps.document.repository;
import com.mps.document.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DocumentRecordRepository extends JpaRepository<DocumentRecord,UUID>{ List<DocumentRecord> findAllByOrganizationIdAndStatusOrderByCreatedAtDesc(UUID organizationId,DocumentStatus status); }
