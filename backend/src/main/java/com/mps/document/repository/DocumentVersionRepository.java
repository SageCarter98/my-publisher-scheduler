package com.mps.document.repository;
import com.mps.document.model.DocumentVersion; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion,UUID>{ Optional<DocumentVersion> findByDocumentIdAndVersionNumber(UUID documentId,int versionNumber); List<DocumentVersion> findAllByDocumentIdOrderByVersionNumberDesc(UUID documentId); }
