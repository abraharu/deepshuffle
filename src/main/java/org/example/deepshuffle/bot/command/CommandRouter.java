package org.example.deepshuffle.bot.command;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.bot.router.CommandParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandRouter {

    private final List<CommandHandler> handlers;

    private final CommandParser commandParser;

    public void route(Update update){

        if(!update.hasMessage()){
            return;
        }

        String text = update.getMessage().getText();

        CommandContext commandContext = commandParser.parse(text);

        for (CommandHandler handler : handlers){
            if (handler.supports(commandContext.command())){

                handler.handle(update, commandContext);

                return;
            }
        }
    }
}
