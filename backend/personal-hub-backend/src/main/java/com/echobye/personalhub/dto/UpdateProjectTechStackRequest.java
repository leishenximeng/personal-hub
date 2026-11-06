
package com.echobye.personalhub.dto;

import java.util.List;

public record UpdateProjectTechStackRequest(
        List<String> techStack
) {
}
