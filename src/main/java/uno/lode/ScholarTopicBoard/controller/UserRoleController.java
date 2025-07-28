package uno.lode.ScholarTopicBoard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import uno.lode.ScholarTopicBoard.domain.role.dto.RoleAdminResponseDTO;
import uno.lode.ScholarTopicBoard.infra.RoleConstants;
import uno.lode.ScholarTopicBoard.service.RoleService;

@Tag(name = "Roles", description = "User roles management")

@RestController
@RequestMapping("/users/roles")

@SecurityRequirement(name = "bearer-key")
@RolesAllowed(RoleConstants.ADMIN)

public class UserRoleController {

    private RoleService roleService;
    public UserRoleController(RoleService roleService) {
    	this.roleService = roleService;
    }

	@Operation(summary = "List all roles (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = RoleAdminResponseDTO.class)))),
        @ApiResponse(responseCode = "204", description = "No roles found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })

    @GetMapping
    public ResponseEntity<List<RoleAdminResponseDTO>> getAllRoles(){
    	List<RoleAdminResponseDTO> allRoles = roleService.getAllRoles();
		return allRoles.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(allRoles);
    }
}
