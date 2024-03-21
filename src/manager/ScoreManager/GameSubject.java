package manager.ScoreManager;

import java.util.List;

public abstract class  GameSubject {
    List<GameObserver> observers;
    void attach(GameObserver observer){};
    void detach(GameObserver observer){};
    void notifyObservers(){};

}
