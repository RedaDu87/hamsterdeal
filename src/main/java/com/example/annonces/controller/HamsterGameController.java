package com.example.annonces.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HamsterGameController {

    /**
     * Page du mini-jeu Hamster 2D
     */
    @GetMapping("/hamster-game")
    public String hamsterGame() {
        return "hamster-game";
    }
}
