package com.mps.notification.model;
import com.mps.auth.model.AppUser; import com.mps.organization.model.Organization; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="notification")
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organization_id") private Organization organization;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="recipient_id") private AppUser recipient;
 @Column(name="notification_type",nullable=false,length=64) private String type;
 @Column(nullable=false,length=200) private String title;
 @Column(nullable=false,length=2000) private String message;
 @Column(name="related_entity_type",length=64) private String relatedEntityType;
 @Column(name="related_entity_id") private UUID relatedEntityId;
 @Column(name="read_at") private Instant readAt;
 @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 protected Notification(){}
 public Notification(Organization org,AppUser recipient,String type,String title,String message,String entityType,UUID entityId){this.organization=org;this.recipient=recipient;this.type=type;this.title=title;this.message=message;this.relatedEntityType=entityType;this.relatedEntityId=entityId;}
 public void markRead(){if(readAt==null)readAt=Instant.now();}
 public UUID getId(){return id;} public AppUser getRecipient(){return recipient;} public String getType(){return type;} public String getTitle(){return title;} public String getMessage(){return message;} public String getRelatedEntityType(){return relatedEntityType;} public UUID getRelatedEntityId(){return relatedEntityId;} public Instant getReadAt(){return readAt;} public Instant getCreatedAt(){return createdAt;}
}
