package com.example.PaITS.project.service; // Only one package line

import com.example.PaITS.project.entity.Project;          // Fixed import
import com.example.PaITS.project.repository.ProjectRepository; // Fixed import
// import com.example.PaITS.project.service.ProjectService; // Not needed if in same package

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public List<Project> findAssignedProjects(UUID userId) {
        // Logic: Returns projects created by this user
        return projectRepository.findByCreatedBy(userId);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return projectRepository.findById(id);
    }

    @Override
    @Transactional
    public Project updateProject(UUID id, Project details) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        // Update fields based on FR-PROJ-006
        existingProject.setName(details.getName());
        existingProject.setDescription(details.getDescription());
        existingProject.setProjectKey(details.getProjectKey());
        existingProject.setActive(details.isActive());

        return projectRepository.save(existingProject);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!projectRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Project not found");
        }
        projectRepository.deleteById(id);
    }
}