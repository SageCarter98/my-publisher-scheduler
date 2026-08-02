package com.mps.document.dto;
import com.mps.document.model.*; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*;
public final class DocumentDtos { private DocumentDtos(){}
 public record DocumentView(UUID id,String title,String category,DocumentAudienceType audienceType,UUID audienceReferenceId,DocumentStatus status,int currentVersion,Instant createdAt){}
 public record VersionView(UUID id,int versionNumber,String originalFilename,String contentType,long sizeBytes,String sha256,String versionNotes,Instant uploadedAt){}
 public record DocumentDetail(DocumentView document,List<VersionView> versions){}
 public record ArchiveRequest(@Size(max=500) String reason){}
}
