package com.mps.audit.model;
import com.mps.organization.model.Organization;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name="audit_log")
public class AuditLog {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="organization_id") private Organization organization;
 @Column(name="actor_user_id") private UUID actorUserId;
 @Column(nullable=false,length=120) private String action;
 @Column(name="entity_type",nullable=false,length=120) private String entityType;
 @Column(name="entity_id") private UUID entityId;
 @Column(nullable=false,length=32) private String outcome;
 @Column(nullable=false,columnDefinition="jsonb") @ColumnTransformer(write = "?::jsonb") private String details="{}";
 @Column(name="occurred_at",nullable=false) private Instant occurredAt=Instant.now();
 protected AuditLog(){}
 public AuditLog(Organization organization,UUID actorUserId,String action,String entityType,UUID entityId,String outcome,String details){this.organization=organization;this.actorUserId=actorUserId;this.action=action;this.entityType=entityType;this.entityId=entityId;this.outcome=outcome;this.details=details==null?"{}":details;}
 public UUID getId(){return id;} public UUID getActorUserId(){return actorUserId;} public String getAction(){return action;} public String getEntityType(){return entityType;} public UUID getEntityId(){return entityId;} public String getOutcome(){return outcome;} public String getDetails(){return details;} public Instant getOccurredAt(){return occurredAt;}
}
