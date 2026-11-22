package com.trofinder.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trofinder.config.CorsConfig;
import com.trofinder.config.JacksonConfig;
import com.trofinder.config.SecurityConfig;
import com.trofinder.config.UploadConfig;
import com.trofinder.controller.AmenityController;
import com.trofinder.controller.RoomController;
import com.trofinder.controller.admin.AddressImportController;
import com.trofinder.dto.response.AmenityResponse;
import com.trofinder.exception.GlobalExceptionHandler;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.security.jwt.JwtAuthenticationFilter;
import com.trofinder.security.jwt.JwtService;
import com.trofinder.service.importdata.AddressImportService;
import com.trofinder.service.interfaces.AmenityService;
import com.trofinder.service.interfaces.RoomService;

@WebMvcTest(controllers = {
        AmenityController.class,
        RoomController.class,
        AddressImportController.class
})
@Import({
        SecurityConfig.class,
        CorsConfig.class,
        JacksonConfig.class,
        UploadConfig.class,
        GlobalExceptionHandler.class,
        JwtAuthenticationFilter.class
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AmenityService amenityService;

    @MockBean
    private RoomService roomService;

    @MockBean
    private AddressImportService addressImportService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    // ========================================================================
    // 1. PUBLIC ENDPOINTS (GUEST ACCESS)
    // ========================================================================

    @Test
    @WithAnonymousUser
    @DisplayName("Guest can access GET /api/amenities without token")
    void testGuestAccessAmenities_Success() throws Exception {
        when(amenityService.getAll()).thenReturn(List.of(
                AmenityResponse.builder().id(1L).name("Wifi").icon("wifi").build()
        ));

        mockMvc.perform(get("/api/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Wifi"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Guest calling non-existent amenity returns 404 with standard ApiResponse JSON")
    void testGuestAccessAmenityNotFound_Returns404Json() throws Exception {
        when(amenityService.getById(999L)).thenThrow(new ResourceNotFoundException("Không tìm thấy tiện ích"));

        mockMvc.perform(get("/api/amenities/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Không tìm thấy tiện ích"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Guest can access GET /api/rooms without token")
    void testGuestAccessRooms_Success() throws Exception {
        when(roomService.getAll(any())).thenReturn(org.springframework.data.domain.Page.empty());

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ========================================================================
    // 2. PROTECTED ENDPOINTS FOR GUEST (SHOULD BE REJECTED WITH 401 JSON)
    // ========================================================================

    @Test
    @WithAnonymousUser
    @DisplayName("Guest calling POST /api/rooms is rejected with 401 JSON")
    void testGuestPostRoom_Returns401() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Phòng test\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Yêu cầu xác thực tài khoản để truy cập tài nguyên này."));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Guest calling DELETE /api/rooms/1 is rejected with 401 JSON")
    void testGuestDeleteRoom_Returns401() throws Exception {
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Yêu cầu xác thực tài khoản để truy cập tài nguyên này."));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Guest calling POST /api/admin/address/import is rejected with 401 JSON")
    void testGuestCallAdminEndpoint_Returns401() throws Exception {
        mockMvc.perform(post("/api/admin/address/import"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========================================================================
    // 3. ROLE-BASED ACCESS CONTROL (TENANT vs LANDLORD vs ADMIN)
    // ========================================================================

    @Test
    @WithMockUser(username = "tenant_user", roles = {"TENANT"})
    @DisplayName("TENANT role calling /api/admin/** is rejected with 403 JSON")
    void testTenantCallAdmin_Returns403() throws Exception {
        mockMvc.perform(post("/api/admin/address/import"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bạn không có quyền thực hiện hành động này."));
    }

    @Test
    @WithMockUser(username = "landlord_user", roles = {"LANDLORD"})
    @DisplayName("LANDLORD role calling /api/admin/** is rejected with 403 JSON")
    void testLandlordCallAdmin_Returns403() throws Exception {
        mockMvc.perform(post("/api/admin/address/import"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bạn không có quyền thực hiện hành động này."));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = {"ADMIN"})
    @DisplayName("ADMIN role calling /api/admin/address/import is allowed (200 OK)")
    void testAdminCallAdmin_Success() throws Exception {
        mockMvc.perform(post("/api/admin/address/import"))
                .andExpect(status().isOk());
    }

    // ========================================================================
    // 4. CORS PREFLIGHT VERIFICATION
    // ========================================================================

    @Test
    @DisplayName("CORS Preflight OPTIONS for Vite (http://localhost:5173) is accepted with headers")
    void testCorsPreflightForVite_Success() throws Exception {
        mockMvc.perform(options("/api/rooms")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }
}
