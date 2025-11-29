package io.github.redheadflag.engine;

import io.github.redheadflag.world.GameGrid;

public class Main {
    public static void main(String[] args) {
        System.out.println("Factotio throughput calculator");

        GameGrid grid = GameGrid.fromFile("grid.txt");

        System.out.println("The grid was built!");
        grid.display();
    }
}