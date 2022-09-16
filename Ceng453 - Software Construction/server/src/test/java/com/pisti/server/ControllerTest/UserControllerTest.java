package com.pisti.server.ControllerTest;

import com.pisti.server.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void mock() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void userTest() throws  Exception{
        User testUser1 = User.builder().email("testuser1@mail.com").name("testuser1").password("password1").build();

        mvc.perform(post("/api/users/add").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + testUser1.getName() + "\",\n" +
                        "    \"password\":\"" + testUser1.getPassword() + "\",\n" +
                        "    \"email\":\"" + testUser1.getEmail() + "\"" + "}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}