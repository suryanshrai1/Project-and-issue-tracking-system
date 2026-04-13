package com.example.PaITS.issue.service;

import com.example.PaITS.issue.dto.IssueRequestDTO;
import com.example.PaITS.issue.dto.IssueResponseDTO;
import com.example.PaITS.issue.dto.IssueStatusUpdateDTO;
import com.example.PaITS.issue.entity.Issue;
import com.example.PaITS.issue.entity.IssueStatus;
import com.example.PaITS.issue.repository.IssueRepository;
import com.example.PaITS.project.entity.Project;
import com.example.PaITS.project.repository.ProjectRepository;
import com.example.PaITS.user.entity.User;
import com.example.PaITS.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public IssueResponseDTO createIssue(UUID projectId, UUID reporterId, IssueRequestDTO request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
        }

        // Increment sequence and generate key
        int nextSequence = project.getIssueSequence() + 1;
        project.setIssueSequence(nextSequence);
        projectRepository.save(project);

        String issueKey = project.getProjectKey() + "-" + nextSequence;

        Issue issue = Issue.builder()
                .project(project)
                .issueKey(issueKey)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(IssueStatus.OPEN)
                .priority(request.getPriority())
                .issueType(request.getIssueType())
                .reporter(reporter)
                .assignee(assignee)
                .build();

        return mapToResponseDTO(issueRepository.save(issue));
    }

    @Override
    public List<IssueResponseDTO> getIssuesByProject(UUID projectId) {
        return issueRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IssueResponseDTO getIssueById(UUID id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        return mapToResponseDTO(issue);
    }

    @Override
    @Transactional
    public IssueResponseDTO updateIssue(UUID id, IssueRequestDTO request) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setPriority(request.getPriority());
        issue.setIssueType(request.getIssueType());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            issue.setAssignee(assignee);
        } else {
            issue.setAssignee(null);
        }

        return mapToResponseDTO(issueRepository.save(issue));
    }

    @Override
    @Transactional
    public void deleteIssue(UUID id) {
        if (!issueRepository.existsById(id)) {
            throw new RuntimeException("Issue not found");
        }
        issueRepository.deleteById(id);
    }

    @Override
    @Transactional
    public IssueResponseDTO updateStatus(UUID id, IssueStatusUpdateDTO statusUpdate) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        validateTransition(issue.getStatus(), statusUpdate.getStatus());
        issue.setStatus(statusUpdate.getStatus());

        return mapToResponseDTO(issueRepository.save(issue));
    }

    @Override
    @Transactional
    public IssueResponseDTO assignIssue(UUID id, UUID assigneeId) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));
        
        issue.setAssignee(assignee);

        return mapToResponseDTO(issueRepository.save(issue));
    }

    private void validateTransition(IssueStatus current, IssueStatus next) {
        if (current == next) return;

        boolean valid = false;
        switch (current) {
            case OPEN:
                valid = (next == IssueStatus.IN_PROGRESS);
                break;
            case IN_PROGRESS:
                valid = (next == IssueStatus.DONE || next == IssueStatus.OPEN);
                break;
            case DONE:
                valid = (next == IssueStatus.IN_PROGRESS); // Reopen
                break;
        }

        if (!valid) {
            throw new RuntimeException("Invalid status transition from " + current + " to " + next);
        }
    }

    private IssueResponseDTO mapToResponseDTO(Issue issue) {
        return IssueResponseDTO.builder()
                .id(issue.getId())
                .issueKey(issue.getIssueKey())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .status(issue.getStatus())
                .priority(issue.getPriority())
                .issueType(issue.getIssueType())
                .projectId(issue.getProject().getId())
                .reporterId(issue.getReporter().getId())
                .assigneeId(issue.getAssignee() != null ? issue.getAssignee().getId() : null)
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();
    }
}
