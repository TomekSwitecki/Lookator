package com.lookator.analysis;

import com.lookator.auth.AppUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analyses")
public class LocationAnalysisController {
    private final LocationAnalysisService service;

    public LocationAnalysisController(LocationAnalysisService service) {
        this.service = service;
    }

    @PostMapping
    LocationAnalysisResponse analyze(@AuthenticationPrincipal AppUser user, @RequestBody AnalyzeLocationRequest request) {
        return service.analyze(user, request);
    }

    @GetMapping
    List<LocationAnalysisResponse> list(@AuthenticationPrincipal AppUser user) {
        return service.list(user);
    }

    @GetMapping("/{id}")
    LocationAnalysisResponse get(@AuthenticationPrincipal AppUser user, @PathVariable UUID id) {
        return service.toResponse(service.owned(id, user), null, null);
    }
}
