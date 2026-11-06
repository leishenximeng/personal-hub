
package com.echobye.personalhub.controller;

import com.echobye.personalhub.dto.CreateProjectRequest;
import com.echobye.personalhub.dto.UpdateProjectTechStackRequest;
import com.echobye.personalhub.entity.Project;
import com.echobye.personalhub.service.ProjectService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.echobye.personalhub.dto.UpdateProjectInfoRequest;



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

    
@PatchMapping("/{projectId}/tech-stack")
public Project updateTechStack(
        @PathVariable Long projectId,
        @RequestBody UpdateProjectTechStackRequest request
) {
    return projectService.updateTechStack(
            projectId,
            request
    );
}

    
/**
 * 修改项目基本信息
 */
@PatchMapping("/{projectId}/info")
public Project updateProjectInfo(
        @PathVariable Long projectId,
        @RequestBody UpdateProjectInfoRequest request
) {
    return projectService.updateProjectInfo(
            projectId,
            request
    );
}


}
