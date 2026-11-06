
package com.echobye.personalhub.dto;

import java.util.List;

public record CreateProjectRequest(
        String name,
        String description,
        String status,
        List<String> techStack
) {
}
