package com.pisti.server.ControllerTest;

import com.pisti.server.model.GameScore;
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

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringRunner.class)
@SpringBootTest()
public class GameScoreControllerTest {

    MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void mock() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void scoreTest() throws Exception{

        Date tempDate = new Date();
        User testUser1 = User.builder().email("testuser1@mail.com").name("testuser1").password("password1").build();
        GameScore testGameScore = GameScore.builder().gameId(100).score(12).date(tempDate).userId(testUser1.getUserId()).build();

        mvc.perform(post("/api/gameScores/add").contentType(MediaType.APPLICATION_JSON)
                .content("{\"score\":\"" + testGameScore.getScore() + "\",\n" +
                        "    \"date\":\"2020-04-30\",\n" +
                        "    \"userId\":\"" + testUser1.getUserId() + "\"" + "}"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

}

