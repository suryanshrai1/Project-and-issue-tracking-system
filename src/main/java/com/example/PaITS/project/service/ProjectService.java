package com.example.PaITS.project.service; // Only one package line, matching your path

import com.example.PaITS.project.entity.Project; // Fixed import to use PaITS
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {

    // FR-PROJ-001: Create project
    Project saveProject(Project project);

    // FR-PROJ-004: Admin view all
    List<Project> findAll();

    // FR-PROJ-005: Member view assigned
    List<Project> findAssignedProjects(UUID userId);

    // For GET /api/projects/{id}
    Optional<Project> findById(UUID id);

    // FR-PROJ-006: Update project details
    Project updateProject(UUID id, Project details);

    // FR-PROJ-007 & 008: Delete project and associated items
    void deleteById(UUID id);
}