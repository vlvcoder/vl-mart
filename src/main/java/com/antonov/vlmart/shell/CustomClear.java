package com.antonov.vlmart.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Clear;

@ShellComponent
public class CustomClear extends Clear {
    @ShellMethod(value="Очистить экран")
    public void cls(){
        super.clear();
    }
}
