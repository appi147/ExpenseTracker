package com.appi147.expensetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FallbackController {

    @GetMapping(value = "/{path:[^\\.]*}")
    public String forwardReactRoutes() {
        return "forward:/index.html";
    }
}
