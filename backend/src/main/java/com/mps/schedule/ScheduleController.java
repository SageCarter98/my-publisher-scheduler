package com.mps.schedule;

import com.mps.auth.security.UserPrincipal;
import com.mps.common.ApiResponse;
import com.mps.schedule.dto.ScheduleDtos.*;
import com.mps.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {
    private final ScheduleService service;

    public ScheduleController(ScheduleService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAuthority('schedules.read')")
    public ApiResponse<List<ScheduleView>> calendar(@AuthenticationPrincipal UserPrincipal principal,
                                                    @RequestParam Instant from, @RequestParam Instant to) {
        return ApiResponse.ok("Schedules loaded.", service.calendar(principal, from, to));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('schedules.read')")
    public ApiResponse<ScheduleView> get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.ok("Schedule loaded.", service.get(principal, id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('schedules.manage')")
    public ApiResponse<ScheduleSeriesView> create(@AuthenticationPrincipal UserPrincipal principal,
                                                  @Valid @RequestBody CreateScheduleRequest request) {
        return ApiResponse.ok("Schedule created.", service.create(principal, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('schedules.manage')")
    public ApiResponse<List<ScheduleView>> update(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID id,
                                                  @Valid @RequestBody UpdateScheduleRequest request) {
        return ApiResponse.ok("Schedule updated.", service.update(principal, id, request));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('schedules.publish')")
    public ApiResponse<List<ScheduleView>> publish(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID id,
                                                   @RequestParam(defaultValue = "SINGLE") EditScope scope) {
        return ApiResponse.ok("Schedule published.", service.publish(principal, id, scope));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('schedules.publish')")
    public ApiResponse<List<ScheduleView>> cancel(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID id,
                                                  @Valid @RequestBody CancelScheduleRequest request) {
        return ApiResponse.ok("Schedule cancelled.", service.cancel(principal, id, request));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('schedules.publish')")
    public ApiResponse<ScheduleView> complete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.ok("Schedule completed.", service.complete(principal, id));
    }
}
