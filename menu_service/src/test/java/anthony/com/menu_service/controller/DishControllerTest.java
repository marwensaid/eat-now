package anthony.com.menu_service.controller;

import anthony.com.menu_service.dto.DishRequest;
import anthony.com.menu_service.dto.DishResponse;
import anthony.com.menu_service.service.DishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour DishController
 */
@WebMvcTest(DishController.class)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DishService dishService;

    @Test
    void testGetAllDishes() throws Exception {
        List<DishResponse> dishes = Arrays.asList(
                DishResponse.builder()
                        .id(1L)
                        .name("Burger")
                        .price(10.0)
                        .category("PLAT")
                        .available(true)
                        .build(),
                DishResponse.builder()
                        .id(2L)
                        .name("Pizza")
                        .price(12.0)
                        .category("PLAT")
                        .available(true)
                        .build()
        );

        when(dishService.getAllDishes()).thenReturn(dishes);

        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Burger"))
                .andExpect(jsonPath("$[1].name").value("Pizza"));
    }

    @Test
    void testGetDishById() throws Exception {
        DishResponse dish = DishResponse.builder()
                .id(1L)
                .name("Burger")
                .price(10.0)
                .category("PLAT")
                .available(true)
                .build();

        when(dishService.getDishById(1L)).thenReturn(dish);

        mockMvc.perform(get("/api/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Burger"))
                .andExpect(jsonPath("$.price").value(10.0));
    }

    @Test
    void testCreateDish() throws Exception {
        DishResponse dish = DishResponse.builder()
                .id(1L)
                .name("Nouveau Plat")
                .price(15.0)
                .category("PLAT")
                .available(true)
                .build();

        when(dishService.createDish(any(DishRequest.class))).thenReturn(dish);

        String dishJson = """
            {
                "name": "Nouveau Plat",
                "description": "Description du plat",
                "price": 15.0,
                "category": "PLAT",
                "available": true
            }
            """;

        mockMvc.perform(post("/api/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dishJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nouveau Plat"));
    }
}
