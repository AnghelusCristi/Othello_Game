package game;

/**
 * Represents a player in the game.
 */
public class Player {

    private final Mark mark;
    private final String username;

    /**
     * Constructs a Player.
     * @param username the username of the player.
     * @param mark the mark of the player.
     */
    public Player(String username, Mark mark) {
        this.mark = mark;
        this.username = username;
    }

    /**
     * Returns the mark of the player.
     * @return the mark of the player.
     */
    public Mark getMark() {
        return mark;
    }

    /**
     * Returns the username of the player.
     * @return the username of the player.
     */
    public String getUsername() {
        return username;
    }
}
