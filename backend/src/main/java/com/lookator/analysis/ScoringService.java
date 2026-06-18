package com.lookator.analysis;

import org.springframework.stereotype.Service;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
class ScoringService {
    private final ScoringProperties weights;

    ScoringService(ScoringProperties weights) {
        this.weights = weights;
    }

    ScoreBreakdown score(Map<MetricType, RawMetric> metrics) {
        var transport = scoreAverage(metrics, List.of(MetricType.PUBLIC_TRANSPORT_STOP), true);
        var amenities = scoreAverage(metrics, List.of(MetricType.GROCERY_STORE, MetricType.SCHOOL, MetricType.KINDERGARTEN, MetricType.PHARMACY, MetricType.HEALTHCARE), true);
        var green = (scoreAverage(metrics, List.of(MetricType.PARK), true) + greenAreaScore(value(metrics, MetricType.GREEN_AREA_TOTAL))) / 2;
        var nuisances = scoreAverage(metrics, List.of(MetricType.RAILWAY_TRACK, MetricType.MAJOR_ROAD, MetricType.GAS_STATION, MetricType.INDUSTRIAL_AREA), false);
        return new ScoreBreakdown(transport, amenities, green, nuisances);
    }

    int total(ScoreBreakdown breakdown) {
        var totalWeight = weights.getTransport() + weights.getAmenities() + weights.getGreen() + weights.getNuisances();
        return clamp((int) Math.round(
                (breakdown.transport() * weights.getTransport()
                        + breakdown.amenities() * weights.getAmenities()
                        + breakdown.green() * weights.getGreen()
                        + breakdown.nuisances() * weights.getNuisances()) / totalWeight));
    }

    private int scoreAverage(Map<MetricType, RawMetric> metrics, List<MetricType> types, boolean lowerIsBetter) {
        return clamp((int) Math.round(types.stream()
                .mapToDouble(type -> distanceScore(value(metrics, type), lowerIsBetter))
                .average()
                .orElse(0)));
    }

    private double value(Map<MetricType, RawMetric> metrics, MetricType type) {
        return metrics.getOrDefault(type, new RawMetric(type, 5000)).valueMetersOrSquareMeters();
    }

    private int distanceScore(double meters, boolean lowerIsBetter) {
        var normalized = Math.max(0, Math.min(1, meters / 2000.0));
        return clamp((int) Math.round((lowerIsBetter ? 1 - normalized : normalized) * 100));
    }

    private int greenAreaScore(double squareMeters) {
        return clamp((int) Math.round(Math.min(squareMeters, 250000) / 250000.0 * 100));
    }

    private int clamp(int score) {
        return Math.max(0, Math.min(100, score));
    }
}
