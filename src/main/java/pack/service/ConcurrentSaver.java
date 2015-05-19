package pack.service;

import pack.model.BaseObject;
import pack.model.Token;

/**
 * Created by ainurminibaev on 20.05.15.
 */
public interface ConcurrentSaver {
    void addToQueue(BaseObject toSave);

    void waitSaving();

    void interruptAll();

    Token findSimilar(String token);
}
