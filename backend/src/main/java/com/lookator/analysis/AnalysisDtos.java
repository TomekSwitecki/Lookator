package com.lookator.analysis;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

record AnalyzeLocationRequest(String address, Double latitude, Double longitude) {}
record MetricResponse(MetricType type, double value, String unit, String label, boolean positiveWhenLower) {}
record ScoreBreakdown(int transport, int amenities, int green, int nuisances) {}
record LocationAnalysisResponse(
        UUID id,
        String address,
        double latitude,
        double longitude,
        int score,
        ScoreBreakdown breakdown,
        List<MetricResponse> metrics,
        List<String> strengths,
        List<String> weaknesses,
        Instant createdAt
) {}
record GeocodedPoint(String address, double latitude, double longitude) {}
record RawMetric(MetricType type, double valueMetersOrSquareMeters) {}
record RawAnalysisData(GeocodedPoint point, Map<MetricType, RawMetric> metrics) {}
