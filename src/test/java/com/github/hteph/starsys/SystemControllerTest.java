package com.github.hteph.starsys;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SystemControllerTest {
    
    @Value(value="${local.server.port}")
    private int port;
    
    @Mock
    private Model model;
    
    @Autowired
    private SystemController controller;
    
    
    @Test
    void createSystem() {
        
        //uses the legacy api for simplicity
        
        var test = controller.createSystem("Random", model);
        
        assertNotNull(controller);
    }
}