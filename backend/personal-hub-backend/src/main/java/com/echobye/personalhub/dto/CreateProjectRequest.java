
package com.echobye.personalhub.dto;

public record CreateProjectRequest(
        String name,
        String description,
        String status
) {
}
