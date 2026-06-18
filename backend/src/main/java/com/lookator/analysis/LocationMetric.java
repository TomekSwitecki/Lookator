package com.lookator.analysis;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class LocationMetric {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private LocationAnalysis analysis;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricType metricType;
    @Column(nullable = false)
    private double metricValue;

    protected LocationMetric() {}

    public LocationMetric(LocationAnalysis analysis, MetricType metricType, double metricValue) {
        this.analysis = analysis;
        this.metricType = metricType;
        this.metricValue = metricValue;
    }

    public UUID getId() { return id; }
    public MetricType getMetricType() { return metricType; }
    public double getMetricValue() { return metricValue; }
}
