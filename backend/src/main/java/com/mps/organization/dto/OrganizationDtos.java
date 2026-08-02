package com.mps.organization.dto;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.Size;
import java.util.UUID;
public final class OrganizationDtos { private OrganizationDtos(){}
 public record OrganizationView(UUID id,String name,String timezone,String status){}
 public record UpdateOrganizationRequest(@NotBlank @Size(max=200) String name,@NotBlank @Size(max=64) String timezone){}
 public record UnitRequest(@NotBlank @Size(max=160) String name,@Size(max=500) String description,UUID departmentId){}
 public record UnitView(UUID id,String name,String description,String status,UUID departmentId){}
}
