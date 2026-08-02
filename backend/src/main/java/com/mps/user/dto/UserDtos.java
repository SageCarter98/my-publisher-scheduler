package com.mps.user.dto;
import com.mps.auth.model.UserStatus; import jakarta.validation.constraints.Email; import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.Size;
import java.util.Set; import java.util.UUID;
public final class UserDtos { private UserDtos(){}
 public record UserView(UUID id,String email,String firstName,String lastName,UserStatus status,UUID departmentId,UUID groupId,Set<String> roles){}
 public record CreateUserRequest(@Email @NotBlank String email,@NotBlank @Size(max=120) String firstName,@NotBlank @Size(max=120) String lastName,UUID departmentId,UUID groupId,Set<String> roles){}
 public record UpdateUserRequest(@NotBlank @Size(max=120) String firstName,@NotBlank @Size(max=120) String lastName,UUID departmentId,UUID groupId){}
 public record RoleAssignmentRequest(Set<String> roles){}
}
