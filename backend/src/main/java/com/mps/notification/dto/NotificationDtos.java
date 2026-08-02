package com.mps.notification.dto;
import java.time.Instant; import java.util.UUID;
public final class NotificationDtos { private NotificationDtos(){}
 public record NotificationView(UUID id,String type,String title,String message,String relatedEntityType,UUID relatedEntityId,boolean read,Instant createdAt){}
 public record NotificationSummary(long unreadCount){}
}
