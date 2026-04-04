package com.example.PaITS.project.controller;

import com.example.PaITS.project.entity.Project;
import com.example.PaITS.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*") // Allows communication with your Frontend
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // FR-PROJ-001: Admin users shall be able to create new projects
    // POST /api/projects
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project created = projectService.saveProject(project);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // FR-PROJ-004 & FR-PROJ-005: List all projects (Admin) / assigned projects (Member)
    // GET /api/projects?role=ADMIN or /api/projects?role=MEMBER&userId=...
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(
            @RequestParam String role,
            @RequestParam(required = false) UUID userId) {

        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.ok(projectService.findAll());
        } else {
            // Member users shall only view projects they are assigned to/created
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(projectService.findAssignedProjects(userId));
        }
    }

    // GET /api/projects/{id} - Get specific project by ID
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        return projectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // FR-PROJ-006: Admin users shall be able to update project details
    // PUT /api/projects/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable UUID id,
            @RequestBody Project projectDetails) {

        Project updated = projectService.updateProject(id, projectDetails);
        return ResponseEntity.ok(updated);
    }

    // FR-PROJ-007: Admin users shall be able to delete projects
    // FR-PROJ-008: Deleting a project shall also delete all associated issues
    // DELETE /api/projects/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}