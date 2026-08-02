package com.mps.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
public class SystemInfoController {
    @GetMapping("/info")
    ApiResponse<Map<String, String>> info() {
        return ApiResponse.ok("MPS backend is running.", Map.of(
                "name", "My Publisher Scheduler",
                "version", "0.1.0-SNAPSHOT"
        ));
    }
}
