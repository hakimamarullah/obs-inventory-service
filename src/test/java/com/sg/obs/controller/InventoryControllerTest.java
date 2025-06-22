package com.sg.obs.controller;

import com.sg.obs.dto.PageWrapper;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.inventory.CreateInventoryRequest;
import com.sg.obs.dto.inventory.InventoryInfo;
import com.sg.obs.dto.inventory.UpdateInventoryRequest;
import com.sg.obs.enums.InventoryType;
import com.sg.obs.service.InventoryService;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/v1/inventories";
    }

    @Test
    void getInventoryById_ShouldReturnSuccess() {
        // Mock service
        InventoryInfo info = createInventoryInfo(1L, 1L, "Item 1", 10, InventoryType.T);
        doReturn(ApiResponse.setSuccess(info)).when(inventoryService).getInventoryById(anyLong());

        ApiResponse<InventoryInfo> response = given().pathParam("id", 1L)
                .accept(ContentType.JSON)
                .when().get("/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(info));
        verify(inventoryService).getInventoryById(anyLong());
    }

    @Test
    void getInventoryList_ShouldReturnPagedModel() {
        // Mock service
        Page<InventoryInfo> page = new PageImpl<>(List.of(createInventoryInfo(2L, 3L, "Item 4", 1208101062, InventoryType.W)));
        doReturn(ApiResponse.setSuccess(new PagedModel<>(page))).when(inventoryService).getInventoryList(any(Pageable.class));

        // Act
        ApiResponse<PageWrapper<InventoryInfo>> response = given().accept(ContentType.JSON)
                .when().get("")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(new PageWrapper<>(new PagedModel<>(page))));
        verify(inventoryService).getInventoryList(any(Pageable.class));
    }

    @Test
    void getInventoryList_ShouldReturnEmptyResult() {
        // Mock service
        Page<InventoryInfo> page = new PageImpl<>(List.of());
        doReturn(ApiResponse.setSuccess(new PagedModel<>(page))).when(inventoryService).getInventoryList(any(Pageable.class));

        // Act
        ApiResponse<PageWrapper<InventoryInfo>> response = given().accept(ContentType.JSON)
                .when().get("")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(new PageWrapper<>(new PagedModel<>(page))));

        verify(inventoryService).getInventoryList(any(Pageable.class));
    }

    @Test
    void givenValidPayload_whenCreateInventory_thenReturnSuccess() {
        // Mock service
        InventoryInfo info = createInventoryInfo(1L, 1L, "Item 1", 10, InventoryType.T);
        doReturn(ApiResponse.setResponse(info, 201)).when(inventoryService).addInventory(any(CreateInventoryRequest.class));

        // Act
        CreateInventoryRequest request = CreateInventoryRequest.builder()
                .itemId(1L)
                .quantity(10)
                .type(InventoryType.T)
                .build();
        ApiResponse<InventoryInfo> response = given().contentType(ContentType.JSON)
                .body(request)
                .when().post("")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setResponse(info, 201));

        verify(inventoryService).addInventory(any(CreateInventoryRequest.class));
    }

    @Test
    void givenInvalidPayload_whenCreateInventory_thenReturnBadRequest() {
        CreateInventoryRequest request = CreateInventoryRequest.builder()
                .itemId(1L)
                .quantity(-1)
                .type(InventoryType.T)
                .build();

        given().contentType(ContentType.JSON)
                .body(request)
                .when().post("")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidPayload_whenUpdateInventory_thenReturnSuccess() {
        // Mock service
        InventoryInfo info = createInventoryInfo(1L, 1L, "Item 1", 10, InventoryType.T);
        doReturn(ApiResponse.setResponse(info, 200)).when(inventoryService).updateInventory(any(UpdateInventoryRequest.class));

        // Act
        UpdateInventoryRequest request = UpdateInventoryRequest.builder()
                .id(1L)
                .itemId(1L)
                .quantity(10)
                .type(InventoryType.T)
                .build();
        ApiResponse<InventoryInfo> response = given().contentType(ContentType.JSON)
                .body(request)
                .when().put("")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setResponse(info, 200));

        verify(inventoryService).updateInventory(any(UpdateInventoryRequest.class));
    }

    @Test
    void givenInvalidPayload_whenUpdateInventory_thenReturnBadRequest() {
        UpdateInventoryRequest request = UpdateInventoryRequest.builder()
                .id(1L)
                .itemId(1L)
                .quantity(-1)
                .type(InventoryType.T)
                .build();

        given().contentType(ContentType.JSON)
                .body(request)
                .when().put("")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInventoryId_whenDeleteInventory_thenReturnSuccess() {
        // Mock service
        doReturn(ApiResponse.setSuccessWithMessage("Success Delete")).when(inventoryService).deleteInventoryById(anyLong());

        // Act
        ApiResponse<String> response = given().accept(ContentType.JSON)
                .when().delete("/{id}", 1L)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccessWithMessage("Success Delete"));
    }

    private InventoryInfo createInventoryInfo(Long id, Long itemId, String itemName, Integer qty, InventoryType type) {
        return InventoryInfo.builder()
                .id(id)
                .itemId(itemId)
                .itemName(itemName)
                .quantity(qty)
                .type(type)
                .build();
    }

}