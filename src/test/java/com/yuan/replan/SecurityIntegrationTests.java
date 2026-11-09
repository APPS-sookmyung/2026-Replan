package com.yuan.replan;

import com.yuan.replan.entity.*;
import com.yuan.replan.repository.*;
import com.yuan.replan.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired DiaryRepository diaries;
    @Autowired TodoRepository todos;
    @Autowired KeywordRepository keywords;
    @Autowired PlanRepository plans;
    @Autowired PlanItemRepository items;
    @Autowired GoogleUserService googleUsers;
    @MockitoBean GeminiService gemini;
    User alice;
    User bob;

    @BeforeEach
    void setup() {
        items.deleteAll(); plans.deleteAll(); diaries.deleteAll(); keywords.deleteAll(); todos.deleteAll(); users.deleteAll();
        alice = users.save(new User("alice-sub", "alice@example.test", "Alice"));
        bob = users.save(new User("bob-sub", "bob@example.test", "Bob"));
    }

    private OidcLoginRequestPostProcessor login(String sub) {
        return oidcLogin().idToken(token -> token.subject(sub));
    }

    @Test
    void anonymousCannotReadRecordsOrUseFeatures() throws Exception {
        for (String path : List.of("/diary", "/todo", "/plans", "/todo/1/plan", "/api/csrf")) {
            mvc.perform(get(path)).andExpect(status().isUnauthorized());
        }
        for (String path : List.of("/home.html", "/diary.html", "/todo.html", "/records.html")) {
            mvc.perform(get(path)).andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/index.html"));
        }
        mvc.perform(post("/diary").param("content", "blocked").with(csrf())).andExpect(status().isUnauthorized());
        mvc.perform(get("/index.html")).andExpect(status().isOk());
        mvc.perform(get("/oauth2/authorization/google")).andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("https://accounts.google.com/")));
        verifyNoInteractions(gemini);
    }

    @Test
    void csrfRequiredAndSessionTokenWorks() throws Exception {
        mvc.perform(post("/diary").with(login("alice-sub")).param("content", "blocked"))
                .andExpect(status().isForbidden());
        var result = mvc.perform(get("/api/csrf").with(login("alice-sub")))
                .andExpect(status().isOk()).andReturn();
        var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(result.getResponse().getContentAsString());
        mvc.perform(post("/diary").session((org.springframework.mock.web.MockHttpSession) result.getRequest().getSession())
                        .header(json.get("headerName").asText(), json.get("token").asText())
                        .param("content", "my diary"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").value("my diary"))
                .andExpect(jsonPath("$.user").doesNotExist());
        assertEquals(alice.getId(), diaries.findAll().get(0).getUser().getId());
    }

    @Test
    void recordsAndAiInputsAreIsolatedAndForeignIdsAreRejected() throws Exception {
        Diary own = new Diary("Alice diary"); own.setUser(alice); diaries.save(own);
        Diary foreign = new Diary("Bob diary"); foreign.setUser(bob); diaries.save(foreign);
        diaries.save(new Diary("legacy diary"));
        Keyword ownKeyword = new Keyword("alice-keyword"); ownKeyword.setUser(alice); keywords.save(ownKeyword);
        Keyword foreignKeyword = new Keyword("bob-keyword"); foreignKeyword.setUser(bob); keywords.save(foreignKeyword);
        Todo todo = new Todo("Alice todo", LocalDateTime.now().plusDays(1), "info"); todo.setUser(alice); todos.save(todo);
        Todo foreignTodo = new Todo("Bob todo", LocalDateTime.now().plusDays(1), "info"); foreignTodo.setUser(bob); todos.save(foreignTodo);
        Plan foreignPlan = plans.save(new Plan(foreignTodo, "private"));
        mvc.perform(get("/diary").with(login("alice-sub"))).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Alice diary"));
        mvc.perform(get("/todo").with(login("alice-sub"))).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/plans").with(login("alice-sub"))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete("/diary/" + foreign.getId()).with(login("alice-sub")).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(delete("/plans/" + foreignPlan.getId()).with(login("alice-sub")).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(get("/todo/" + foreignTodo.getId() + "/plan").with(login("alice-sub"))).andExpect(status().isNotFound());
        mvc.perform(post("/todo/" + foreignTodo.getId() + "/plan").with(login("alice-sub")).with(csrf())).andExpect(status().isNotFound());
        verifyNoInteractions(gemini);
        String planJson = """
                {"reason":"test","items":[{"content":"step","startTime":"2026-11-11T09:00:00",
                "endTime":"2026-11-11T09:30:00","durationMinutes":30}]}
                """;
        when(gemini.generatePlan(anyString(), any(), anyString(), anyString())).thenReturn(planJson);
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/todo/" + todo.getId() + "/plan").with(login("alice-sub")).with(csrf()))
                    .andExpect(status().isOk());
        }
        verify(gemini, times(2)).generatePlan(eq("Alice todo"), any(), eq("info"),
                argThat(value -> value.contains("alice-keyword") && !value.contains("bob-keyword")));
        mvc.perform(get("/todo/" + todo.getId() + "/plan").with(login("alice-sub")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(1));
        Plan ownPlan = plans.findByTodoIdAndTodoUserId(todo.getId(), alice.getId()).orElseThrow();
        mvc.perform(delete("/plans/" + ownPlan.getId()).with(login("alice-sub")).with(csrf())).andExpect(status().isOk());
        assertEquals(0, items.count());
        mvc.perform(delete("/diary/" + own.getId()).with(login("alice-sub")).with(csrf())).andExpect(status().isOk());
        assertTrue(diaries.existsById(foreign.getId()));
        assertTrue(plans.existsById(foreignPlan.getId()));
    }

    @Test
    void savesTodoAndAnalyzedKeywordsForCurrentUser() throws Exception {
        mvc.perform(post("/todo").with(login("alice-sub")).with(csrf()).param("title", "task")
                .param("deadline", "2026-11-11T12:00:00").param("additionalInfo", "info"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("task"));
        when(gemini.analyzeDiary("diary")).thenReturn("focus, fatigue");
        mvc.perform(post("/diary/analyze").with(login("alice-sub")).with(csrf()).param("content", "diary"))
                .andExpect(status().isOk()).andExpect(content().string("focus, fatigue"));
        assertEquals(2, keywords.findAllByUserId(alice.getId()).size());
        assertEquals(0, keywords.findAllByUserId(bob.getId()).size());
        assertEquals(1, todos.findAllByUserId(alice.getId()).size());
    }

    @Test
    void sameGoogleSubjectReusesUserEvenWhenEmailChanges() {
        var token = new OidcIdToken("test", Instant.now(), Instant.now().plusSeconds(60),
                Map.of("sub", "alice-sub", "email", "new@example.test", "name", "New name"));
        var principal = new DefaultOidcUser(List.of(new SimpleGrantedAuthority("ROLE_USER")), token);
        assertEquals(alice.getId(), googleUsers.saveProfile(principal).getId());
        assertEquals(alice.getId(), googleUsers.saveProfile(principal).getId());
        assertEquals(2, users.count());
        assertEquals("new@example.test", users.findByGoogleSub("alice-sub").orElseThrow().getEmail());
    }

    @Test
    void logoutRequiresCsrfAndInvalidatesSession() throws Exception {
        var result = mvc.perform(get("/api/csrf").with(login("alice-sub"))).andReturn();
        var session = (org.springframework.mock.web.MockHttpSession) result.getRequest().getSession();
        mvc.perform(post("/logout").session(session).with(login("alice-sub"))).andExpect(status().isForbidden());
        mvc.perform(post("/logout").session(session).with(login("alice-sub")).with(csrf()))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/index.html?logout"));
        assertTrue(session.isInvalid());
        mvc.perform(get("/diary")).andExpect(status().isUnauthorized());
    }
}
