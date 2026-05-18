package org.example.deepshuffle.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandRouter {

    List<CommandHandler> handlers;

    public void route(Update update){
        if (update.hasMessage()){
            return;
        }

        String text = update.getMessage().getText();

        for (CommandHandler handler : handlers){
            if (handler.handle(text)){

                handler.handle(update);

                return;
            }
        }
    }
}
