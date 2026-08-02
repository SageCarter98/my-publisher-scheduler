package com.mps.announcement.dto;
import com.mps.announcement.model.*; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.UUID;
public final class AnnouncementDtos{private AnnouncementDtos(){}
 public record CreateAnnouncementRequest(@NotBlank @Size(max=200) String title,@NotBlank @Size(max=10000) String content,@NotNull AudienceType audienceType,UUID audienceReferenceId,Instant publishAt,Instant expiresAt){}
 public record AnnouncementView(UUID id,String title,String content,AudienceType audienceType,UUID audienceReferenceId,AnnouncementStatus status,Instant publishAt,Instant expiresAt,Instant publishedAt,Instant createdAt){}
}
