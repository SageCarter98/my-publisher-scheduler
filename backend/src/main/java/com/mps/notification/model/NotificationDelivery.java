package com.mps.notification.model;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="notification_delivery")
public class NotificationDelivery {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="notification_id") private Notification notification;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=24) private DeliveryChannel channel;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=24) private DeliveryStatus status=DeliveryStatus.QUEUED;
 @Column(nullable=false) private int attempts;
 @Column(name="next_attempt_at",nullable=false) private Instant nextAttemptAt=Instant.now();
 @Column(name="last_attempt_at") private Instant lastAttemptAt;
 @Column(name="delivered_at") private Instant deliveredAt;
 @Column(name="failure_reason",length=1000) private String failureReason;
 @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected NotificationDelivery(){}
 public NotificationDelivery(Notification notification,DeliveryChannel channel){this.notification=notification;this.channel=channel;}
 public void delivered(){attempts++;lastAttemptAt=Instant.now();deliveredAt=lastAttemptAt;status=DeliveryStatus.DELIVERED;failureReason=null;updatedAt=lastAttemptAt;}
 public void failed(String reason,int maxAttempts){attempts++;lastAttemptAt=Instant.now();failureReason=reason;status=attempts>=maxAttempts?DeliveryStatus.FAILED:DeliveryStatus.RETRYING;nextAttemptAt=lastAttemptAt.plusSeconds(Math.min(3600,60L*(1L<<Math.min(attempts,5))));updatedAt=lastAttemptAt;}
 public UUID getId(){return id;} public Notification getNotification(){return notification;} public DeliveryChannel getChannel(){return channel;} public DeliveryStatus getStatus(){return status;} public int getAttempts(){return attempts;} public Instant getNextAttemptAt(){return nextAttemptAt;}
}
