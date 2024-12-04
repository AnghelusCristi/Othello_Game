package game;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This interface holds the groundwork of BoardConfigurations.
 * This includes a hashmap with all the starting indexes for the marks on the board.
 * This lets you create different starting positions.
 */
public interface BoardConfiguration {

    /**
     * Returns the configuration
     * @return the configuration
     */
    HashMap<Mark, ArrayList<Integer>> getConfig();
}
