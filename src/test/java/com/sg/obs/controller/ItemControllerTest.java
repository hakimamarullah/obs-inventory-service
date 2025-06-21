package com.sg.obs.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost:";
    }

    @Test
    void getItems_ShouldReturnPagedItems() throws Exception {
        // Given

        ApiResponse<PagedModel<ItemInfo>> res = given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .contentType(ContentType.JSON)
                .when()
                .get("/v1/items")
                .as(new TypeRef<>() {
                });
        ItemInfo item1 = createItemInfo(1L, "Item 1", 20.0);
        ItemInfo item2 = createItemInfo(2L, "Item 2", 15.0);
        List<ItemInfo> items = Arrays.asList(item1, item2);

        Page<ItemInfo> page = new PageImpl<>(items, PageRequest.of(0, 10), 2);
        PagedModel<ItemInfo> pagedModel = new PagedModel<>(page);
        ApiResponse<PagedModel<ItemInfo>> apiResponse = ApiResponse.setSuccess(pagedModel);

        doReturn(apiResponse).when(itemService).getItemsList(any(Pageable.class));

        // When
        MvcResult result = mockMvc.perform(get("/v1/items")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<PagedModel<ItemInfo>> response = objectMapper.readValue(responseBody,
                new TypeReference<>() {
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
    void getItemById_ShouldReturnItem_WhenItemExists() throws Exception {
        // Given
        Long itemId = 1L;
        ItemInfo itemInfo = createItemInfo(itemId, "Test Item", 35.0);
        doReturn(ApiResponse.setSuccess(itemInfo)).when(itemService).getItemById(itemId);

        // When
        MvcResult result = mockMvc.perform(get("/v1/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<ItemInfo> response = objectMapper.readValue(responseBody,
                new TypeReference<>() {
                });

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData().getId()).isEqualTo(itemId);
        assertThat(response.getData().getName()).isEqualTo("Test Item");
        assertThat(response.getData().getPrice()).isEqualTo(35.0);
    }

    @Test
    void addItem_ShouldCreateItem_WhenValidRequest() throws Exception {
        // Given
        CreateItemRequest createRequest = CreateItemRequest.builder()
                .name("New Item")
                .price(25.2)
                .build();

        ItemInfo createdItem = createItemInfo(1L, createRequest.getName(), createRequest.getPrice());
        doReturn(ApiResponse.setSuccess(createdItem, 201)).when(itemService).addItem(any(CreateItemRequest.class));

        // When
        MvcResult result = mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<ItemInfo> response = objectMapper.readValue(responseBody,
                new TypeReference<>() {
                });

        assertThat(response.getCode()).isEqualTo(201);
        assertThat(response.getData().getId()).isEqualTo(1L);
        assertThat(response.getData().getName()).isEqualTo("New Item");
        assertThat(response.getData().getPrice()).isEqualTo(25.2);
    }

    @Test
    void addItem_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given - empty request body or invalid data
        CreateItemRequest invalidRequest = CreateItemRequest.builder()
                .name("") // assuming name cannot be empty
                .build();

        // When & Then
        mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ShouldUpdateItem_WhenValidRequest() throws Exception {
        // Given
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("Updated Item")
                .price(25.2)
                .build();

        ItemInfo updatedItem = createItemInfo(1L, "Updated Item", 25.2);
        doReturn(ApiResponse.setSuccess(updatedItem)).when(itemService).updateItem(any(UpdateItemRequest.class));

        // When
        MvcResult result = mockMvc.perform(put("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<ItemInfo> response = objectMapper.readValue(responseBody,
                new TypeReference<>() {
                });

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData().getId()).isEqualTo(1L);
        assertThat(response.getData().getName()).isEqualTo("Updated Item");
        assertThat(response.getData().getPrice()).isEqualTo(25.2);
    }

    @Test
    void updateItem_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Given - request without required fields
        UpdateItemRequest invalidRequest = UpdateItemRequest.builder()
                .name("") // assuming validation fails for empty name
                .build();

        // When & Then
        mockMvc.perform(put("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteItem_ShouldDeleteItem_WhenItemExists() throws Exception {
        // Given
        Long itemId = 1L;
        String successMessage = "Item deleted successfully";
        doReturn(ApiResponse.setSuccess(successMessage)).when(itemService).deleteItemById(itemId);

        // When
        MvcResult result = mockMvc.perform(delete("/v1/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<String> response = objectMapper.readValue(responseBody,
                new TypeReference<>() {
                });

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo(successMessage);
    }

    @Test
    void getItems_ShouldHandleEmptyResult() throws Exception {
        // Given
        Page<ItemInfo> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        PagedModel<ItemInfo> emptyPagedModel = new PagedModel<>(emptyPage);

        doReturn(ApiResponse.setSuccess(emptyPagedModel)).when(itemService).getItemsList(any(Pageable.class));

        // When
        MvcResult result = mockMvc.perform(get("/v1/items")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<PagedModel<ItemInfo>> response = objectMapper.readValue(responseBody,
                new TypeReference<>() {
                });

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData().getContent()).isEmpty();
    }

    @Test
    void addItem_ShouldReturnBadRequest_WhenMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ShouldReturnBadRequest_WhenMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(put("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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