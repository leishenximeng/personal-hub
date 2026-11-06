
package com.echobye.personalhub.dto;

public record UpdateProjectInfoRequest(
        String name,
        String description,
        String status
) {
}
