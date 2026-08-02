package com.mps.user;
import com.mps.auth.security.UserPrincipal; import com.mps.common.ApiResponse; import com.mps.user.dto.UserDtos.*; import com.mps.user.service.UserAdminService;
import jakarta.validation.Valid; import org.springframework.http.ResponseEntity; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping("/api/v1/users") @PreAuthorize("hasAuthority('users.read')") public class UserAdminController {
 private final UserAdminService service; public UserAdminController(UserAdminService service){this.service=service;}
 @GetMapping public ApiResponse<List<UserView>> list(@AuthenticationPrincipal UserPrincipal p,@RequestParam(required=false) String search){return ApiResponse.ok("Users loaded.",service.list(p,search));}
 @PostMapping @PreAuthorize("hasAuthority('users.manage')") public ApiResponse<UserView> create(@AuthenticationPrincipal UserPrincipal p,@Valid @RequestBody CreateUserRequest r){return ApiResponse.ok("User created.",service.create(p,r));}
 @PutMapping("/{id}") @PreAuthorize("hasAuthority('users.manage')") public ApiResponse<UserView> update(@AuthenticationPrincipal UserPrincipal p,@PathVariable UUID id,@Valid @RequestBody UpdateUserRequest r){return ApiResponse.ok("User updated.",service.update(p,id,r));}
 @PutMapping("/{id}/roles") @PreAuthorize("hasAuthority('roles.manage')") public ApiResponse<UserView> roles(@AuthenticationPrincipal UserPrincipal p,@PathVariable UUID id,@RequestBody RoleAssignmentRequest r){return ApiResponse.ok("Roles updated.",service.assign(p,id,r));}
 @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('users.manage')") public ResponseEntity<Void> archive(@AuthenticationPrincipal UserPrincipal p,@PathVariable UUID id){service.archive(p,id);return ResponseEntity.noContent().build();}
 @PostMapping("/{id}/restore") @PreAuthorize("hasAuthority('users.manage')") public ApiResponse<UserView> restore(@AuthenticationPrincipal UserPrincipal p,@PathVariable UUID id){return ApiResponse.ok("User restored.",service.restore(p,id));}
}
