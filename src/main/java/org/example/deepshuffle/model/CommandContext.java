package org.example.deepshuffle.model;

import java.util.List;

public record CommandContext(String command, List<String> arguments) {
}