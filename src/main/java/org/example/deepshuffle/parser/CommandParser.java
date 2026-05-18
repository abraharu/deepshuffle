package org.example.deepshuffle.parser;

import org.example.deepshuffle.model.CommandContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CommandParser {

    public CommandContext parse(String message){

        String[] parts = message.split(" ");

        String command = parts[0];

        List<String> arguments = Arrays.stream(parts)
                                        .skip(1)
                                        .toList();

        return new CommandContext(command, arguments);
    }
}
