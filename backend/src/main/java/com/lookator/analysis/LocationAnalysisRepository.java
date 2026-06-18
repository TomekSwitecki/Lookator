package com.lookator.analysis;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationAnalysisRepository extends JpaRepository<LocationAnalysis, UUID> {
    List<LocationAnalysis> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<LocationAnalysis> findByIdAndUserId(UUID id, UUID userId);
}
