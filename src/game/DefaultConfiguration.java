package game;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents the default configuration of the board (the initial state of the board).
 */
public class DefaultConfiguration implements BoardConfiguration {
    private final HashMap<Mark, ArrayList<Integer>> config;

    /**
     * Constructor for the DefaultConfiguration class.
     */
    public DefaultConfiguration() {
        config = new HashMap<>();
        ArrayList<Integer> white = new ArrayList<>();
        white.add(27);
        white.add(36);
        ArrayList<Integer> black = new ArrayList<>();
        black.add(28);
        black.add(35);
        config.put(Mark.WHITE, white);
        config.put(Mark.BLACK, black);
    }

    @Override
    public HashMap<Mark, ArrayList<Integer>> getConfig() {
        return config;
    }
}
