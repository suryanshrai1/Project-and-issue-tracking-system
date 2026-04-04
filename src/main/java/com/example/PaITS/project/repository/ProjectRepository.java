package com.example.PaITS.project.repository;

import com.example.PaITS.project.entity.Project; // Fixed: changed 'demo' to 'PaITS'
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    // FR-PROJ-005: Custom finder to show projects created by a specific user
    List<Project> findByCreatedBy(UUID createdBy);
}