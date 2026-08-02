package com.mps.announcement;
import com.mps.announcement.dto.AnnouncementDtos.*; import com.mps.announcement.service.AnnouncementService; import com.mps.auth.security.UserPrincipal; import com.mps.common.ApiResponse; import jakarta.validation.Valid; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/announcements") public class AnnouncementController{
 private final AnnouncementService service; public AnnouncementController(AnnouncementService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAuthority('announcements.read') or hasAuthority('announcements.manage')") public ApiResponse<List<AnnouncementView>> list(@AuthenticationPrincipal UserPrincipal p){return ApiResponse.ok("Announcements loaded.",service.list(p));}
 @PostMapping @PreAuthorize("hasAuthority('announcements.manage')") public ApiResponse<AnnouncementView> create(@AuthenticationPrincipal UserPrincipal p,@Valid @RequestBody CreateAnnouncementRequest r){return ApiResponse.ok("Announcement created.",service.create(p,r));}
 @PostMapping("/{id}/publish") @PreAuthorize("hasAuthority('announcements.manage')") public ApiResponse<AnnouncementView> publish(@AuthenticationPrincipal UserPrincipal p,@PathVariable UUID id){return ApiResponse.ok("Announcement published.",service.publish(p,id));}
 @PostMapping("/{id}/archive") @PreAuthorize("hasAuthority('announcements.manage')") public ApiResponse<AnnouncementView> archive(@AuthenticationPrincipal UserPrincipal p,@PathVariable UUID id){return ApiResponse.ok("Announcement archived.",service.archive(p,id));}
}
