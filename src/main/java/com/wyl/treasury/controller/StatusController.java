package com.wyl.treasury.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status")
public class StatusController {
    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    @GetMapping("/isAlive")
    public String isAlive() {
        logger.debug("isAlive...");
        return "true";
    }
}
