package com.lookator.comparison;

import com.lookator.auth.AppUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comparisons")
public class ComparisonController {
    private final ComparisonService service;

    public ComparisonController(ComparisonService service) {
        this.service = service;
    }

    @PostMapping
    ComparisonResponse create(@AuthenticationPrincipal AppUser user, @RequestBody CreateComparisonRequest request) {
        return service.create(user, request);
    }

    @GetMapping
    List<ComparisonResponse> list(@AuthenticationPrincipal AppUser user) {
        return service.list(user);
    }
}
