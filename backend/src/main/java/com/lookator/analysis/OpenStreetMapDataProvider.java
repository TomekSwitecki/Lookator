package com.lookator.analysis;

import org.springframework.stereotype.Component;
import java.util.EnumMap;

@Component
public class OpenStreetMapDataProvider implements LocationDataProvider {
    @Override
    public RawAnalysisData analyze(AnalyzeLocationRequest request) {
        if (request.latitude() == null || request.longitude() == null) {
            throw new IllegalArgumentException("Latitude and longitude are required for the MVP scaffold");
        }
        var point = new GeocodedPoint(
                request.address() == null || request.address().isBlank() ? "Selected point" : request.address(),
                request.latitude(),
                request.longitude());
        var metrics = new EnumMap<MetricType, RawMetric>(MetricType.class);
        metrics.put(MetricType.PUBLIC_TRANSPORT_STOP, new RawMetric(MetricType.PUBLIC_TRANSPORT_STOP, 420));
        metrics.put(MetricType.GROCERY_STORE, new RawMetric(MetricType.GROCERY_STORE, 650));
        metrics.put(MetricType.SCHOOL, new RawMetric(MetricType.SCHOOL, 900));
        metrics.put(MetricType.KINDERGARTEN, new RawMetric(MetricType.KINDERGARTEN, 760));
        metrics.put(MetricType.PHARMACY, new RawMetric(MetricType.PHARMACY, 580));
        metrics.put(MetricType.HEALTHCARE, new RawMetric(MetricType.HEALTHCARE, 1200));
        metrics.put(MetricType.PARK, new RawMetric(MetricType.PARK, 500));
        metrics.put(MetricType.GREEN_AREA_TOTAL, new RawMetric(MetricType.GREEN_AREA_TOTAL, 185000));
        metrics.put(MetricType.RAILWAY_TRACK, new RawMetric(MetricType.RAILWAY_TRACK, 1500));
        metrics.put(MetricType.MAJOR_ROAD, new RawMetric(MetricType.MAJOR_ROAD, 700));
        metrics.put(MetricType.GAS_STATION, new RawMetric(MetricType.GAS_STATION, 980));
        metrics.put(MetricType.INDUSTRIAL_AREA, new RawMetric(MetricType.INDUSTRIAL_AREA, 2100));
        return new RawAnalysisData(point, metrics);
    }
}
