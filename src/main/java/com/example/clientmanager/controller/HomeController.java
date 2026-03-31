package com.example.clientmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller per servir les pàgines principals amb suport multiidioma
 */
@Controller
public class HomeController {

    /**
     * Serveix la pàgina principal (index) amb suport multiidioma
     * 
     * @return la vista index (index.html amb i18n)
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Serveix el formulari de creació de client amb suport multiidioma
     * 
     * @return la vista client-form (client-form.html amb i18n)
     */
    @GetMapping("/client-form")
    public String clientForm() {
        return "client-form";
    }
}

