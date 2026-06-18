package com.lookator.comparison;

import com.lookator.analysis.LocationAnalysis;
import com.lookator.analysis.LocationAnalysisService;
import com.lookator.auth.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

@Service
public class ComparisonService {
    private final ComparisonRepository comparisons;
    private final LocationAnalysisService analyses;

    public ComparisonService(ComparisonRepository comparisons, LocationAnalysisService analyses) {
        this.comparisons = comparisons;
        this.analyses = analyses;
    }

    @Transactional
    public ComparisonResponse create(AppUser user, CreateComparisonRequest request) {
        if (request.analysisIds() == null || request.analysisIds().size() < 2) {
            throw new IllegalArgumentException("Select at least two analyses to compare");
        }
        var comparison = new Comparison(user);
        request.analysisIds().stream()
                .distinct()
                .map(id -> analyses.owned(id, user))
                .map(analysis -> new ComparisonLocation(comparison, analysis))
                .forEach(comparison::addLocation);
        return toResponse(comparisons.save(comparison));
    }

    @Transactional(readOnly = true)
    public List<ComparisonResponse> list(AppUser user) {
        return comparisons.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private ComparisonResponse toResponse(Comparison comparison) {
        var locations = comparison.getLocations().stream()
                .map(ComparisonLocation::getAnalysis)
                .sorted(Comparator.comparing(LocationAnalysis::getScore).reversed())
                .map(this::toComparedLocation)
                .toList();
        return new ComparisonResponse(comparison.getId(), locations, keyDifferences(locations), comparison.getCreatedAt());
    }

    private ComparedLocation toComparedLocation(LocationAnalysis analysis) {
        return new ComparedLocation(
                analysis.getId(),
                analysis.getLatitude(),
                analysis.getLongitude(),
                analysis.getScore(),
                scoreStrengths(analysis.getScore()),
                scoreWeaknesses(analysis.getScore()));
    }

    private List<String> scoreStrengths(int score) {
        if (score >= 80) {
            return List.of("Strong overall location score", "Balanced access, green areas, and nuisance profile");
        }
        if (score >= 65) {
            return List.of("Solid overall score", "Worth comparing against stronger alternatives");
        }
        return List.of("Has enough data for a side-by-side decision");
    }

    private List<String> scoreWeaknesses(int score) {
        if (score < 60) {
            return List.of("Lower score than typical MVP target", "Review amenities and nuisance distances carefully");
        }
        if (score < 80) {
            return List.of("Not the strongest option in this comparison");
        }
        return List.of("No major score-level weakness");
    }

    private List<String> keyDifferences(List<ComparedLocation> locations) {
        if (locations.size() < 2) {
            return List.of();
        }
        var best = locations.getFirst();
        var weakest = locations.getLast();
        var gap = best.score() - weakest.score();
        return List.of(
                "Best score: " + best.score() + " vs lowest score: " + weakest.score(),
                gap >= 15 ? "The locations differ materially on area quality" : "The compared locations are relatively close",
                "Review strengths and weaknesses before treating the score as decisive");
    }
}
