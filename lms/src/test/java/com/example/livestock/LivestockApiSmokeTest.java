package com.example.livestock;

import com.example.livestock.security.CustomUserDetailsService;
import com.example.livestock.security.JwtService;
import com.example.livestock.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:livestock-test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.mail.host=localhost",
        "spring.mail.port=1025"
})
@AutoConfigureMockMvc
class LivestockApiSmokeTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Test
    void adminGetEndpointsDoNotReturnServerError() throws Exception {
        String token = adminToken();

        mockMvc.perform(post("/api/admin/owners")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "user": {
                                    "fullName": "Smoke Owner",
                                    "email": "smoke.owner@example.com",
                                    "phoneNumber": "+263771111111",
                                    "password": "Password123!",
                                    "role": "OWNER"
                                  },
                                  "nationalId": "SMOKE-001",
                                  "address": "Smoke Farm"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/admin/owners").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/animals").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/audit-logs").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private String adminToken() {
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername("admin@livestock.local");
        return jwtService.generateToken(principal);
    }
}
