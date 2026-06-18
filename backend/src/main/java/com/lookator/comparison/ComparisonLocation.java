package com.lookator.comparison;

import com.lookator.analysis.LocationAnalysis;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class ComparisonLocation {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Comparison comparison;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private LocationAnalysis analysis;

    protected ComparisonLocation() {}

    public ComparisonLocation(Comparison comparison, LocationAnalysis analysis) {
        this.comparison = comparison;
        this.analysis = analysis;
    }

    public LocationAnalysis getAnalysis() { return analysis; }
}
