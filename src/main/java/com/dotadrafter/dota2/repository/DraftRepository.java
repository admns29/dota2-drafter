package com.dotadrafter.dota2.repository;

import com.dotadrafter.dota2.model.DraftState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DraftRepository extends JpaRepository<DraftState, Long> {
}
