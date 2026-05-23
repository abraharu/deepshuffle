package org.example.deepshuffle.bot.command;

import java.util.List;

public record CommandContext(String command, List<String> arguments) {
}
