package com.jfuente040.oauth2client.client_app.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class AuthController {

    @GetMapping("/authorized")
    public Map<String, Object> authorized(@RequestParam String code) {
        // This method is called after successful authorization
        // You can process the authorization code here if needed
        return Collections.singletonMap("code", code);
    }

}
