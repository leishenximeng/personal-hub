
package com.echobye.personalhub.service;

import com.echobye.personalhub.entity.Project;
import com.echobye.personalhub.repository.ProjectRepository;

import org.springframework.stereotype.Service;
import java.util.List;

import com.echobye.personalhub.dto.CreateProjectRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import com.echobye.personalhub.dto.UpdateProjectTechStackRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.echobye.personalhub.dto.UpdateProjectInfoRequest;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    // 构造方法：注入 Repository
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // 查询所有项目
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // 创建项目
    @Transactional
    public Project createProject(CreateProjectRequest request) {

        // 1. 创建一个新的 Project 实体对象
        Project project = new Project();

        // 2. 把 DTO 的数据复制到 Entity
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(request.status());

            // 3. 设置项目技术栈
    if (request.techStack() != null) {
        project.setTechStack(
                new ArrayList<>(request.techStack())
        );
    }

        // 3. 保存到 PostgreSQL
        return projectRepository.save(project);
    }

    
@Transactional
public Project updateTechStack(
        Long projectId,
        UpdateProjectTechStackRequest request
) {

    // 1. 根据 ID 查询已有项目
    Project project = projectRepository
            .findById(projectId)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "项目不存在"
                    )
            );

    // 2. 检查请求是否包含技术栈
    if (request.techStack() == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "技术栈不能为空"
        );
    }

    // 3. 清空旧的技术栈
    project.getTechStack().clear();

    // 4. 添加新的技术栈
    project.getTechStack().addAll(
            request.techStack()
    );

    // 5. 返回修改后的 Project
    return project;
}


@Transactional
public Project updateProjectInfo(
        Long projectId,
        UpdateProjectInfoRequest request
) {

    // 1. 根据 ID 查询已有项目
    Project project = projectRepository
            .findById(projectId)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "项目不存在"
                    )
            );

    // 2. 校验名称
    if (request.name() == null
            || request.name().isBlank()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "项目名称不能为空"
        );
    }

    // 3. 校验描述和状态
    if (request.description() == null
            || request.status() == null
            || request.status().isBlank()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "项目描述和状态不能为空"
        );
    }

    // 4. 校验数据库字段长度
    String name = request.name().trim();
    String status = request.status().trim();

    if (name.length() > 120 || status.length() > 30) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "项目名称或状态超过长度限制"
        );
    }

    // 5. 修改已有 Entity 的属性
    project.setName(name);
    project.setDescription(request.description());
    project.setStatus(status);

    // 6. 返回修改后的对象
    return project;
}


}
