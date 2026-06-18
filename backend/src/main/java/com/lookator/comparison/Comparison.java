package com.lookator.comparison;

import com.lookator.auth.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Comparison {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AppUser user;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "comparison", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComparisonLocation> locations = new ArrayList<>();

    protected Comparison() {}

    public Comparison(AppUser user) {
        this.user = user;
    }

    public void addLocation(ComparisonLocation location) {
        locations.add(location);
    }

    public UUID getId() { return id; }
    public AppUser getUser() { return user; }
    public Instant getCreatedAt() { return createdAt; }
    public List<ComparisonLocation> getLocations() { return locations; }
}
