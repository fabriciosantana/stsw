package br.edu.idp.es.stsw.pyramid.e2e;

import br.edu.idp.es.stsw.pyramid.user.User;
import br.edu.idp.es.stsw.pyramid.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CAMADA 3 DA PIRÂMIDE DE TESTES: TESTES PONTA A PONTA / REST API (TOPO DA PIRÂMIDE)
 * 
 * Características:
 * - Testam o fluxo completo da aplicação através da interface REST HTTP (Controller -> Service -> Repository -> H2).
 * - Utilizam MockMvc para disparar requisições HTTP reais (POST, GET, PUT, DELETE) e validar status HTTP, headers e corpo JSON.
 * - Foco nos fluxos críticos do usuário (Core User Journeys), mantendo menor quantidade de testes devido ao custo de execução.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Nível 3 - Teste End-to-End: REST API /api/users")
class UserManagementE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E: Deve realizar o fluxo completo de criação, busca, atualização e remoção de um usuário")
    void shouldPerformFullUserLifecycleE2E() throws Exception {
        // 1. POST /api/users - Criar Usuário
        String userJson = """
                {
                    "name": "Gabriel Santos",
                    "email": "gabriel@example.com",
                    "role": "DEVELOPER"
                }
                """;

        String responseJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Gabriel Santos")))
                .andExpect(jsonPath("$.email", is("gabriel@example.com")))
                .andExpect(jsonPath("$.role", is("DEVELOPER")))
                .andExpect(jsonPath("$.active", is(true)))
                .andReturn().getResponse().getContentAsString();

        assertThat(userRepository.count()).isEqualTo(1);

        // Extrair ID do usuário criado
        Long userId = userRepository.findByEmail("gabriel@example.com").orElseThrow().getId();

        // 2. GET /api/users/{id} - Buscar Usuário Criado
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Gabriel Santos")));

        // 3. PUT /api/users/{id} - Atualizar Nome e Role
        String updateJson = """
                {
                    "name": "Gabriel Santos Senior",
                    "email": "gabriel@example.com",
                    "role": "LEAD_DEVELOPER"
                }
                """;

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Gabriel Santos Senior")))
                .andExpect(jsonPath("$.role", is("LEAD_DEVELOPER")));

        // 4. PATCH /api/users/{id}/deactivate - Desativar Usuário
        mockMvc.perform(patch("/api/users/" + userId + "/deactivate"))
                .andExpect(status().isNoContent());

        User deactivated = userRepository.findById(userId).orElseThrow();
        assertThat(deactivated.isActive()).isFalse();

        // 5. DELETE /api/users/{id} - Deletar Usuário
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("E2E: Deve retornar BAD REQUEST (400) ao tentar criar usuário com email duplicado")
    void shouldReturnBadRequestForDuplicateEmailE2E() throws Exception {
        userRepository.save(new User("Existente", "duplicado@example.com", "USER"));

        String duplicateUserJson = """
                {
                    "name": "Novo Usuário",
                    "email": "duplicado@example.com",
                    "role": "USER"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email já cadastrado: duplicado@example.com"));
    }

    @Test
    @DisplayName("E2E: Deve listar múltiplos usuários cadastrados")
    void shouldListUsersE2E() throws Exception {
        userRepository.save(new User("User 1", "u1@example.com", "USER"));
        userRepository.save(new User("User 2", "u2@example.com", "ADMIN"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
