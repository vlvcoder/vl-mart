package com.antonov.vlmart.controller;

import com.antonov.vlmart.service.RootService;
import com.antonov.vlmart.service.buyer.BuyerService;
import com.antonov.vlmart.service.product.MartService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
@RequestMapping("/")
@AllArgsConstructor
public class MainController {

    private final RootService rootService;

    @RequestMapping("/")
    public String root(Model model) {
        var wrap = rootService.data();
        model.addAttribute("wrap", wrap);
        return "index";
    }

    @RequestMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/hi")
    @ResponseBody
    public String hi() {
        return "VL-MART";
    }
}
