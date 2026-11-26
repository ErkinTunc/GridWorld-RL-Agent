package org.gridworld.search;

/**
 * 
 * SearchAlgorithm is an interface that defines the contract for search algorithms in the GridWorld environment.
 * Implementing classes must provide an implementation for the search method, which takes a SearchProblem as input and returns a SearchResult.
 * We wanted to keep the interface simple and focused on the core functionality of search algorithms, 
 * allowing for flexibility in the implementation of different search strategies.(Inspired from the Strategy design pattern )
 */
public interface SearchAlgorithm {

    SearchResult search(SearchProblem problem);
}