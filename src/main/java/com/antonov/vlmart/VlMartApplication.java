package com.antonov.vlmart;

import com.antonov.vlmart.service.RootService;
import com.antonov.vlmart.shell.FunCommands;
import com.antonov.vlmart.shell.ShellCommands;
import com.antonov.vlmart.shell.TrainingCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class VlMartApplication {

    public static void main(String[] args) {
        SpringApplication.run(VlMartApplication.class, args);
    }

    @Autowired
    private RootService rootService;

    @Autowired
    private ShellCommands shellCommands;

    @Autowired
    private TrainingCommands trainingCommands;

    @Autowired
    private FunCommands funCommands;

    @PostConstruct
    private void initData() {
        rootService.initData();

        shellCommands.logo();
        System.out.println(shellCommands.easyStatus());

//        trainingCommands.multiply_logo();
//        System.out.println(trainingCommands.multi());

//        System.out.println(funCommands.ruflag());

//        System.out.println("\u001B[31m");
//        System.out.println("red");
//        System.out.println("\u001B[0m");
//        System.out.println("reset");

    }
}
