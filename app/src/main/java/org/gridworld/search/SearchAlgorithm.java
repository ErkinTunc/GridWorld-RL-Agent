package org.gridworld.search;

import org.gridworld.domain.Position;

public interface SearchAlgorithm {

    SearchResult search(Position start, Position end);
}
