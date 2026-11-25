package org.gridworld.pathfinding;
import org.gridworld.domain.GridMap;
import org.gridworld.domain.Position;

public class AStarPathfinder implements SearchAlgotihm {
    
    GridMap gridMap;
    

    AStarPathfinder(GridMap gridMap) {
        this.gridMap = gridMap;
    }


    @Override
    public SearchResult search(Position start, Position end) {
        // Implement A* pathfinding algorithm here
        // This is a placeholder for the actual implementation
        return new SearchResult(false, null, null, 0, 0, 0);
    }




}
