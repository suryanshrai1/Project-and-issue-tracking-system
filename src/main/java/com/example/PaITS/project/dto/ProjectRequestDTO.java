package com.example.PaITS.project.dto;

import java.util.UUID;

public class ProjectRequestDTO {

    private String projectKey; // FR-PROJ-002: Unique project key
    private String name;       // FR-PROJ-003: Project name
    private String description;// FR-PROJ-003: Project description
    private UUID createdBy;    // The Admin user ID creating the project
    private boolean isActive;  // Status of the project

    // Default Constructor
    public ProjectRequestDTO() {}

    // Getters and Setters
    public String getProjectKey() { return projectKey; }
    public void setProjectKey(String projectKey) { this.projectKey = projectKey; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}