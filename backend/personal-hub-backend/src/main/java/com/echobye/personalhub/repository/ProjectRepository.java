
package com.echobye.personalhub.repository;

import com.echobye.personalhub.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository
        extends JpaRepository<Project, Long> {
}
