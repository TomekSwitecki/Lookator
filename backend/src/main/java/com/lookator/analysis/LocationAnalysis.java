package com.lookator.analysis;

import com.lookator.auth.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class LocationAnalysis {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AppUser user;
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;
    @Column(nullable = false)
    private int score;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocationMetric> metrics = new ArrayList<>();

    protected LocationAnalysis() {}

    public LocationAnalysis(AppUser user, double latitude, double longitude, int score) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.score = score;
    }

    public void addMetric(MetricType type, double value) {
        metrics.add(new LocationMetric(this, type, value));
    }

    public UUID getId() { return id; }
    public AppUser getUser() { return user; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getScore() { return score; }
    public Instant getCreatedAt() { return createdAt; }
    public List<LocationMetric> getMetrics() { return metrics; }
}
