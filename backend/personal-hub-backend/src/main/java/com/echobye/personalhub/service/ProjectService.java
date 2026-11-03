
package com.echobye.personalhub.service;

import com.echobye.personalhub.entity.Project;
import com.echobye.personalhub.repository.ProjectRepository;

import org.springframework.stereotype.Service;
import java.util.List;

import com.echobye.personalhub.dto.CreateProjectRequest;
import org.springframework.transaction.annotation.Transactional;

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

        // 3. 保存到 PostgreSQL
        return projectRepository.save(project);
    }

}
