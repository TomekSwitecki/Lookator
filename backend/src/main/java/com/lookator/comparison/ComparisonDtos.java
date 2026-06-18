package com.lookator.comparison;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

record CreateComparisonRequest(List<UUID> analysisIds) {}
record ComparedLocation(UUID analysisId, double latitude, double longitude, int score, List<String> strengths, List<String> weaknesses) {}
record ComparisonResponse(UUID id, List<ComparedLocation> locations, List<String> keyDifferences, Instant createdAt) {}
