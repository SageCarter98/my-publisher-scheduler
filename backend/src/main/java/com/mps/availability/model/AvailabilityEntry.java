package com.mps.availability.model;
import com.mps.auth.model.AppUser; import com.mps.organization.model.Organization; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="availability_entry")
public class AvailabilityEntry {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="organization_id") private Organization organization;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id") private AppUser user;
 @Column(name="start_at",nullable=false) private Instant startAt; @Column(name="end_at",nullable=false) private Instant endAt;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private AvailabilityStatus status;
 @Column(length=500) private String notes; @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now(); @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
 protected AvailabilityEntry(){} public AvailabilityEntry(Organization o,AppUser u,Instant s,Instant e,AvailabilityStatus st,String n){organization=o;user=u;startAt=s;endAt=e;status=st;notes=n==null?null:n.trim();}
 public UUID getId(){return id;} public AppUser getUser(){return user;} public Instant getStartAt(){return startAt;} public Instant getEndAt(){return endAt;} public AvailabilityStatus getStatus(){return status;} public String getNotes(){return notes;}
}
