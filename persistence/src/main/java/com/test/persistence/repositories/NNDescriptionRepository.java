package com.test.persistence.repositories;

import com.test.persistence.entities.NNDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NNDescriptionRepository extends JpaRepository<NNDescription, Long> {
}
