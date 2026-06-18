package com.lookator.analysis;

import com.lookator.auth.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LocationAnalysisService {
    private final LocationDataProvider dataProvider;
    private final ScoringService scoring;
    private final LocationAnalysisRepository analyses;

    public LocationAnalysisService(LocationDataProvider dataProvider, ScoringService scoring, LocationAnalysisRepository analyses) {
        this.dataProvider = dataProvider;
        this.scoring = scoring;
        this.analyses = analyses;
    }

    @Transactional
    public LocationAnalysisResponse analyze(AppUser user, AnalyzeLocationRequest request) {
        var raw = dataProvider.analyze(request);
        var breakdown = scoring.score(raw.metrics());
        var entity = new LocationAnalysis(user, raw.point().latitude(), raw.point().longitude(), scoring.total(breakdown));
        raw.metrics().values().forEach(metric -> entity.addMetric(metric.type(), metric.valueMetersOrSquareMeters()));
        return toResponse(analyses.save(entity), raw.point().address(), breakdown);
    }

    @Transactional(readOnly = true)
    public List<LocationAnalysisResponse> list(AppUser user) {
        return analyses.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(analysis -> toResponse(analysis, null, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationAnalysis owned(UUID id, AppUser user) {
        return analyses.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis was not found"));
    }

    public LocationAnalysisResponse toResponse(LocationAnalysis analysis, String address, ScoreBreakdown suppliedBreakdown) {
        var rawMetrics = new java.util.EnumMap<MetricType, RawMetric>(MetricType.class);
        analysis.getMetrics().forEach(metric -> rawMetrics.put(metric.getMetricType(), new RawMetric(metric.getMetricType(), metric.getMetricValue())));
        var breakdown = suppliedBreakdown == null ? scoring.score(rawMetrics) : suppliedBreakdown;
        var metrics = analysis.getMetrics().stream()
                .sorted(Comparator.comparing(metric -> metric.getMetricType().name()))
                .map(this::metricResponse)
                .toList();
        return new LocationAnalysisResponse(
                analysis.getId(),
                address,
                analysis.getLatitude(),
                analysis.getLongitude(),
                analysis.getScore(),
                breakdown,
                metrics,
                strengths(metrics),
                weaknesses(metrics),
                analysis.getCreatedAt());
    }

    private MetricResponse metricResponse(LocationMetric metric) {
        var type = metric.getMetricType();
        var greenTotal = type == MetricType.GREEN_AREA_TOTAL;
        return new MetricResponse(type, metric.getMetricValue(), greenTotal ? "m2" : "m", label(type), positiveWhenLower(type));
    }

    private boolean positiveWhenLower(MetricType type) {
        return switch (category(type)) {
            case TRANSPORT, AMENITIES, GREEN -> type != MetricType.GREEN_AREA_TOTAL;
            case NUISANCES -> false;
        };
    }

    private List<String> strengths(List<MetricResponse> metrics) {
        return metrics.stream()
                .filter(metric -> metric.positiveWhenLower() ? metric.value() <= 700 : metric.value() >= 1200)
                .limit(4)
                .map(metric -> metric.label() + " looks favorable")
                .toList();
    }

    private List<String> weaknesses(List<MetricResponse> metrics) {
        return metrics.stream()
                .filter(metric -> metric.positiveWhenLower() ? metric.value() > 1200 : metric.value() < 700)
                .limit(4)
                .map(metric -> metric.label() + " may need attention")
                .toList();
    }

    private String label(MetricType type) {
        return switch (type) {
            case PUBLIC_TRANSPORT_STOP -> "Public transport";
            case GROCERY_STORE -> "Grocery store";
            case SCHOOL -> "School";
            case KINDERGARTEN -> "Kindergarten";
            case PHARMACY -> "Pharmacy";
            case HEALTHCARE -> "Healthcare";
            case PARK -> "Park";
            case GREEN_AREA_TOTAL -> "Green area within 1 km";
            case RAILWAY_TRACK -> "Railway track";
            case MAJOR_ROAD -> "Major road";
            case GAS_STATION -> "Gas station";
            case INDUSTRIAL_AREA -> "Industrial area";
        };
    }

    private MetricCategory category(MetricType type) {
        return switch (type) {
            case PUBLIC_TRANSPORT_STOP -> MetricCategory.TRANSPORT;
            case GROCERY_STORE, SCHOOL, KINDERGARTEN, PHARMACY, HEALTHCARE -> MetricCategory.AMENITIES;
            case PARK, GREEN_AREA_TOTAL -> MetricCategory.GREEN;
            case RAILWAY_TRACK, MAJOR_ROAD, GAS_STATION, INDUSTRIAL_AREA -> MetricCategory.NUISANCES;
        };
    }
}
