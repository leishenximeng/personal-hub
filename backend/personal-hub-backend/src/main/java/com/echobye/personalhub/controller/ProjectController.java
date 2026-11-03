
package com.echobye.personalhub.controller;

import com.echobye.personalhub.dto.CreateProjectRequest;
import com.echobye.personalhub.entity.Project;
import com.echobye.personalhub.service.ProjectService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    // 构造器注入 Service
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 查询所有项目
    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

        // 创建新项目
    @PostMapping
    public Project createProject(
            @RequestBody CreateProjectRequest request
    ) {
        return projectService.createProject(request);
    }
}
