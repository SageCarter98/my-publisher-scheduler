package com.mps.settings.model;
import com.mps.organization.model.Organization; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="system_settings",uniqueConstraints=@UniqueConstraint(name="uk_system_settings_org_key",columnNames={"organization_id","setting_key"}))
public class SystemSetting {
 @Id private UUID id; @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="organization_id") private Organization organization;
 @Column(name="setting_key",nullable=false,length=120) private String key; @Column(name="setting_value",nullable=false,columnDefinition="text") private String value;
 @Column(name="value_type",nullable=false,length=30) private String valueType; @Column(length=500) private String description; @Column(name="updated_by") private UUID updatedBy;
 @Column(name="created_at",nullable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt; @Version private long version;
 protected SystemSetting(){} public SystemSetting(Organization o,String k,String v,String t,String d,UUID actor){id=UUID.randomUUID();organization=o;key=k;value=v;valueType=t;description=d;updatedBy=actor;createdAt=updatedAt=Instant.now();}
 public void update(String v,String t,String d,UUID actor){value=v;valueType=t;description=d;updatedBy=actor;updatedAt=Instant.now();}
 public UUID getId(){return id;} public String getKey(){return key;} public String getValue(){return value;} public String getValueType(){return valueType;} public String getDescription(){return description;} public Instant getUpdatedAt(){return updatedAt;} public UUID getUpdatedBy(){return updatedBy;}
}
