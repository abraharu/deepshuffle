package org.example.deepshuffle.service;

import org.example.deepshuffle.bot.state.UserState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserStateService {

    private final Map<Long, UserState> userStates = new HashMap<>();

    public void setState(Long userId, UserState state){
        userStates.put(userId, state);
    }

    public UserState getState(Long userId){
        return userStates.getOrDefault(userId, UserState.IDLE);
    }

    public void clearState(Long userId){
        userStates.remove(userId);
    }
}
