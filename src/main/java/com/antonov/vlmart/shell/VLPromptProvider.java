package com.antonov.vlmart.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class VLPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString(
                "VLMart" + "=> ",
                AttributedStyle.DEFAULT
                        .background(AttributedStyle.BLACK)
                        .foreground(AttributedStyle.YELLOW)
                        .bold()
                        .italic());
    }
}
