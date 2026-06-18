package com.lookator.analysis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lookator.scoring")
public class ScoringProperties {
    private double transport = 0.25;
    private double amenities = 0.25;
    private double green = 0.25;
    private double nuisances = 0.25;

    public double getTransport() { return transport; }
    public void setTransport(double transport) { this.transport = transport; }
    public double getAmenities() { return amenities; }
    public void setAmenities(double amenities) { this.amenities = amenities; }
    public double getGreen() { return green; }
    public void setGreen(double green) { this.green = green; }
    public double getNuisances() { return nuisances; }
    public void setNuisances(double nuisances) { this.nuisances = nuisances; }
}
