package com.mps.notification;
import com.mps.auth.security.UserPrincipal; import com.mps.common.ApiResponse; import com.mps.notification.dto.NotificationDtos.*; import com.mps.notification.service.NotificationService; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/notifications") public class NotificationController{
 private final NotificationService service; public NotificationController(NotificationService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAuthority('notifications.read')") public ApiResponse<List<NotificationView>> list(@AuthenticationPrincipal UserPrincipal p){return ApiResponse.ok("Notifications loaded.",service.list(p));}
 @GetMapping("/summary") @PreAuthorize("hasAuthority('notifications.read')") public ApiResponse<NotificationSummary> summary(@AuthenticationPrincipal UserPrincipal p){return ApiResponse.ok("Notification summary loaded.",service.summary(p));}
 @PutMapping("/{id}/read") @PreAuthorize("hasAuthority('notifications.read')") public ApiResponse<NotificationView> read(@AuthenticationPrincipal UserPrincipal p,@PathVariable UUID id){return ApiResponse.ok("Notification marked read.",service.markRead(p,id));}
}
