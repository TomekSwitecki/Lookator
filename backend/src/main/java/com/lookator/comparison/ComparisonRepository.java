package com.lookator.comparison;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComparisonRepository extends JpaRepository<Comparison, UUID> {
    List<Comparison> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<Comparison> findByIdAndUserId(UUID id, UUID userId);
}
