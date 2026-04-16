package com.example.PaITS.project.controller;

import com.example.PaITS.project.dto.ProjectRequestDTO;
import com.example.PaITS.project.dto.ProjectResponseDTO;
import com.example.PaITS.project.service.ProjectService;
import com.example.PaITS.user.entity.User;
import com.example.PaITS.user.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody ProjectRequestDTO request,
            Authentication authentication) {

        User user = getCurrentUser(authentication);
        ProjectResponseDTO created = projectService.saveProject(request, user.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(Authentication authentication) {
        User user = getCurrentUser(authentication);

        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.ok(projectService.findAll());
        } else {
            return ResponseEntity.ok(projectService.findAssignedProjects(user.getId()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(
            @PathVariable UUID id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        return ResponseEntity.ok(projectService.findById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectRequestDTO projectDetails,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        ProjectResponseDTO updated = projectService.updateProject(id, projectDetails, currentUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable UUID id,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        projectService.deleteProject(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<ProjectResponseDTO> addMember(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        ProjectResponseDTO updated = projectService.addMember(id, userId, currentUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ProjectResponseDTO> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        ProjectResponseDTO updated = projectService.removeMember(id, userId, currentUser);
        return ResponseEntity.ok(updated);
    }
}