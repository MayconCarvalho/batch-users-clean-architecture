package br.com.batch.users.presentation.controller;

import br.com.batch.users.application.find_all_users.FindAllUsersInputDto;
import br.com.batch.users.application.find_all_users.FindAllUsersOutputDto;
import br.com.batch.users.application.find_all_users.FindAllUsersUseCase;
import br.com.batch.users.application.find_status_user.FindStatusUserByIdInputDto;
import br.com.batch.users.application.find_status_user.FindStatusUserByIdOutputDto;
import br.com.batch.users.application.import_list_user.ImportListUserInputDto;
import br.com.batch.users.application.import_list_user.ImportListUserOutputDto;
import br.com.batch.users.application.import_list_user.ImportListUserUseCase;
import br.com.batch.users.application.import_user.ImportUserInputDto;
import br.com.batch.users.application.import_user.ImportUserOutputDto;
import br.com.batch.users.application.import_user.ImportUserUseCase;
import br.com.batch.users.application.find_status_user.FindStatusUserByIdUseCase;
import br.com.batch.users.presentation.controller.exception.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserImportController {

    private final ImportUserUseCase importUserUseCase;
    private final FindStatusUserByIdUseCase findStatusUserByIdUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;
    private final ImportListUserUseCase importListUserUseCase;

    @Operation(summary = "Import a list of users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users imported",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImportListUserInputDto.class)))
    })
    @PostMapping("/import/batch")
    public ImportListUserOutputDto importListUser(final @RequestBody ImportListUserInputDto importListUserInputDto) {
        return importListUserUseCase.execute(importListUserInputDto);
    }

    @Operation(summary = "Import a single user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User import successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImportUserInputDto.class)))
    })
    @PostMapping("/import/user")
    public ImportUserOutputDto importUser(final @RequestBody ImportUserInputDto importUserInput) {
        return importUserUseCase.execute(importUserInput);
    }

    @Operation(summary = "Get status user imported by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status imported details",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FindStatusUserByIdOutputDto.class))),
            @ApiResponse(responseCode = "404", description = "User import not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}/status")
    public FindStatusUserByIdOutputDto findById(
            final @Parameter(description = "ID of the user import to be retrieved", required = true)
            @PathVariable UUID id) {
        final var findUserById = FindStatusUserByIdInputDto.builder()
                .id(id)
                .build();

        return findStatusUserByIdUseCase.execute(findUserById);
    }

    @Operation(summary = "Get all user imported")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all user imports")
    })
    @GetMapping
    public List<FindAllUsersOutputDto> findAll() {
        final var findAllUsersInputDto = FindAllUsersInputDto.builder().build();
        return findAllUsersUseCase.execute(findAllUsersInputDto);
    }

}
