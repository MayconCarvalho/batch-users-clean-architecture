package br.com.batch.users.integration;

import br.com.batch.users.application.import_list_user.UserInputDto;
import br.com.batch.users.application.import_list_user.UserOutputDto;
import br.com.batch.users.application.import_user.ImportUserInputDto;
import br.com.batch.users.integration.config.IntegrationTestsConfig;
import br.com.batch.users.presentation.controller.exception.ErrorResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImportBatchUserListUseCaseTest extends IntegrationTestsConfig {

    private final String prefixJsonRequest = "/json/request/%s";
    private final String prefixJsonResponse = "/json/response/%s";

    @Test
    @DisplayName("Should import a list of users")
    void shouldImportAListOfUsers() throws IOException {
        final String requestBatchUserList = prefixJsonRequest.formatted("batch-list-user.json");
        final String responseBatchUserList = prefixJsonResponse.formatted("batch-list-user-mixed.json");

        final var usersRequest = readJsonListFile(requestBatchUserList, new TypeReference<List<UserInputDto>>() {});
        final var usersResponse = readJsonListFile(responseBatchUserList, new TypeReference<List<UserOutputDto>>() {});

        final var response = given()
                .contentType(ContentType.JSON)
                .when()
                .body(usersRequest)
                .post("/api/v1/users/import/batch")
                .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .extract()
                .response();

        final var responseUsers = response.jsonPath().getList(".", UserOutputDto.class);

        assertNotNull(responseUsers);
        assertEquals(usersResponse.size(), responseUsers.size());

        final var expectedEmails = usersResponse.stream().map(UserOutputDto::getEmail).toList();
        final var responseEmails = responseUsers.stream().map(UserOutputDto::getEmail).toList();
        assertEquals(expectedEmails, responseEmails);

        final var expectedStatus = usersResponse.stream().map(UserOutputDto::getStatus).toList();
        final var responseStatus = responseUsers.stream().map(UserOutputDto::getStatus).toList();
        assertEquals(expectedStatus, responseStatus);
    }

    @Test
    @DisplayName("Should create a single user successfully")
    void shouldCreateASingleUserSuccessfully() throws IOException {
        final String requestSingleUser = prefixJsonRequest.formatted("single-user.json");
        final String responseSingleUser = prefixJsonResponse.formatted("single-user-success.json");

        final var usersRequest = readJsonObjectFile(requestSingleUser, new TypeReference<ImportUserInputDto>() {});
        final var usersResponse = readJsonObjectFile(responseSingleUser, new TypeReference<UserOutputDto>() {});

        final var response = given()
                .contentType(ContentType.JSON)
                .when()
                .body(usersRequest)
                .post("/api/v1/users/import/user")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();

        final var responseUser = response.jsonPath().getObject(".", UserOutputDto.class);

        assertNotNull(responseUser);
        assertEquals(usersResponse.getEmail(), responseUser.getEmail());
        assertEquals(usersResponse.getStatus(), responseUser.getStatus());
    }

    @Test
    @DisplayName("Should return an error when importing a single user with invalid email")
    void shouldReturnAnErrorWhenImportingASingleUserWithValidEmail() throws IOException {
        final String requestSingleUser = prefixJsonRequest.formatted("single-user-invalid-email.json");
        final String responseSingleUser = prefixJsonResponse.formatted("single-user-error-exception.json");

        final var usersRequest = readJsonObjectFile(requestSingleUser, new TypeReference<ImportUserInputDto>() {});
        final var usersResponse = readJsonObjectFile(responseSingleUser, new TypeReference<ErrorResponseDto>() {});

        final var response = given()
                .contentType(ContentType.JSON)
                .when()
                .body(usersRequest)
                .post("/api/v1/users/import/user")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .response();

        final var responseError = response.jsonPath().getObject(".", ErrorResponseDto.class);

        assertNotNull(responseError);
        assertEquals(usersResponse.getStatus(), responseError.getStatus());
        assertEquals(usersResponse.getMessage(), responseError.getMessage());
    }

}
