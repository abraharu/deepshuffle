package org.example.deepshuffle.parser;

import org.example.deepshuffle.model.CommandContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CommandParser {

    public CommandContext parse(String message){

        List<String> parts = Arrays.stream(message.trim().split("\\s+")).filter(s -> !s.isEmpty()).toList();

        String command = parts.getFirst();

        List<String> arguments = parts.stream().skip(1).toList();

        return new CommandContext(command, arguments);
    }
}
