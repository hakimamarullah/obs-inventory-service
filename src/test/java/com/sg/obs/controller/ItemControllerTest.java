package com.sg.obs.controller;

import com.sg.obs.dto.PageWrapper;
import com.sg.obs.dto.ApiResponse;
import com.sg.obs.dto.item.CreateItemRequest;
import com.sg.obs.dto.item.ItemInfo;
import com.sg.obs.dto.item.UpdateItemRequest;
import com.sg.obs.service.ItemService;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {


    @LocalServerPort
    private int port;

    @MockitoBean
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/v1/items";
    }

    @Test
    void getItems_ShouldReturnPagedItems() {
        // Given
        ItemInfo item1 = createItemInfo(1L, "Item 1", 20.0);
        ItemInfo item2 = createItemInfo(2L, "Item 2", 15.0);
        List<ItemInfo> items = Arrays.asList(item1, item2);

        Page<ItemInfo> page = new PageImpl<>(items, PageRequest.of(0, 10), 2);
        PageWrapper<ItemInfo> pagedModel = new PageWrapper<>(page);
        ApiResponse<PageWrapper<ItemInfo>> apiResponse = ApiResponse.setSuccess(pagedModel);

        doReturn(apiResponse).when(itemService).getItemsList(any(Pageable.class), any());

        // When
        ApiResponse<PageWrapper<ItemInfo>> response = given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .contentType(ContentType.JSON)
                .when()
                .get("")
                .as(new TypeRef<>() {
                });

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData().getContent()).hasSize(2);
        assertThat(response.getData().getContent().getFirst().getId()).isEqualTo(1L);
        assertThat(response.getData().getContent().get(0).getName()).isEqualTo("Item 1");
        assertThat(response.getData().getContent().get(0).getPrice()).isEqualTo(20.0);
        assertThat(response.getData().getContent().get(1).getId()).isEqualTo(2L);
        assertThat(response.getData().getContent().get(1).getName()).isEqualTo("Item 2");
        assertThat(response.getData().getContent().get(1).getPrice()).isEqualTo(15.0);
    }

    @Test
    void getItemById_ShouldReturnItem_WhenItemExists() {
        // Given
        Long itemId = 1L;
        ItemInfo itemInfo = createItemInfo(itemId, "Test Item", 35.0);
        doReturn(ApiResponse.setSuccess(itemInfo)).when(itemService).getItemById(anyLong());

        // When
        ApiResponse<ItemInfo> response = given().pathParam("id", itemId)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(itemInfo));
    }

    @Test
    void addItem_ShouldCreateItem_WhenValidRequest() {
        // Given
        CreateItemRequest createRequest = CreateItemRequest.builder()
                .name("New Item")
                .price(25.2)
                .build();

        ItemInfo createdItem = createItemInfo(1L, createRequest.getName(), createRequest.getPrice());
        doReturn(ApiResponse.setResponse(createdItem, 201)).when(itemService).addItem(any(CreateItemRequest.class));

        // When
        ApiResponse<ItemInfo> response = given().contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .as(new TypeRef<>() {

                });


        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setResponse(createdItem, 201));
    }

    @Test
    void addItem_ShouldReturnBadRequest_WhenInvalidRequest() {
        // Given - empty request body or invalid data
        CreateItemRequest invalidRequest = CreateItemRequest.builder()
                .name("") // assuming name cannot be empty
                .build();

        // When & Then
        given().body(invalidRequest).contentType(ContentType.JSON)
                .when().post("")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void updateItem_ShouldUpdateItem_WhenValidRequest() {
        // Given
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("Updated Item")
                .price(25.2)
                .build();

        ItemInfo updatedItem = createItemInfo(1L, "Updated Item", 25.2);
        doReturn(ApiResponse.setSuccess(updatedItem)).when(itemService).updateItem(any(UpdateItemRequest.class));

        // When
        ApiResponse<ItemInfo> response = given().contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .as(new TypeRef<>() {

                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(updatedItem));
    }

    @Test
    void updateItem_ShouldReturnBadRequest_WhenInvalidRequest() {
        // Given - request without required fields
        UpdateItemRequest invalidRequest = UpdateItemRequest.builder()
                .name("") // assuming validation fails for empty name
                .build();

        // When & Then
        given().body(invalidRequest).contentType(ContentType.JSON)
                .when().put("")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void deleteItem_ShouldDeleteItem_WhenItemExists() {
        // Given
        Long itemId = 1L;
        String successMessage = "Item deleted successfully";
        doReturn(ApiResponse.setSuccessWithMessage(successMessage)).when(itemService).deleteItemById(itemId);

        // When
        ApiResponse<String> response = given().pathParam("id", itemId)
                .accept(ContentType.JSON)
                .when().delete("/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .as(new TypeRef<>() {

                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccessWithMessage(successMessage));
    }

    @Test
    void getItems_ShouldHandleEmptyResult() {
        // Given
        Page<ItemInfo> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        PagedModel<ItemInfo> emptyPagedModel = new PagedModel<>(emptyPage);

        doReturn(ApiResponse.setSuccess(emptyPagedModel)).when(itemService).getItemsList(any(Pageable.class), any());

        // When
        ApiResponse<PageWrapper<ItemInfo>> response = given().queryParam("page", 0)
                .queryParam("size", 10)
                .contentType(ContentType.JSON)
                .when()
                .get("")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .response()
                .as(new TypeRef<>() {

                });

        // Then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(ApiResponse.setSuccess(new PageWrapper<>(emptyPagedModel)));
    }

    @Test
    void addItem_ShouldReturnBadRequest_WhenMissingRequestBody() {
        // When & Then
        given().contentType(ContentType.JSON)
                .when()
                .post("")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    void updateItem_ShouldReturnBadRequest_WhenMissingRequestBody() {
        // When & Then
        given().contentType(ContentType.JSON)
                .when()
                .put("")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    // Helper method to create ItemInfo objects
    private ItemInfo createItemInfo(Long id, String name, double price) {
        return ItemInfo.builder()
                .id(id)
                .name(name)
                .price(price)
                .build();
    }
}