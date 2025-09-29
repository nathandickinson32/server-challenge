package dto;

import java.util.Random;

public class GameState {

    private int target;
    private int attemptsLeft;

    public GameState() {
        this.target = new Random().nextInt(100) + 1;
        this.attemptsLeft = 7;
    }

    public GameState(int target, int attemptsLeft) {
        this.target = target;
        this.attemptsLeft = attemptsLeft;
    }

    public int getTarget() {
        return target;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public void decrementAttempts() {
        attemptsLeft--;
    }
}