package com.pisti.client.service;

import com.pisti.server.model.User;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class ClientService {

    private String applicationURL = "http://144.122.71.168:8083/server18/api";
    private RestTemplate restTemplate = new RestTemplate();

    public boolean register(User user){

        String userEmail = user.getEmail();
        String userName = user.getName();
        String password = user.getPassword();
        String json = new JSONObject()
                .put("name", userName)
                .put("email", userEmail)
                .put("password", password).toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.exchange(applicationURL + "/users/add", HttpMethod.POST, entity, String.class);
        return true;
    }

    public Boolean login(User user){
        String userName = user.getName();
        String password = user.getPassword();
        String json = new JSONObject()
                .put("name", userName)
                .put("password", password).toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(applicationURL + "/users/find", HttpMethod.POST, entity, String.class);
        return Objects.equals(response.getBody(), "true");
    }
}
