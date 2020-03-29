package com.example.chessboardgame;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

class ShortestPath {
    // Below arrays details all 8 possible movements
    // for a knight
    private static int[] row = {2, 2, -2, -2, 1, 1, -1, -1};
    private static int[] col = {-1, 1, 1, -1, 2, -2, 2, -2};

    static ArrayList<Cell> cellPath = new ArrayList<>();

    // Check if (x, y) is valid chess board coordinates
    // Note that a knight cannot go out of the chessboard
    private static boolean valid(int x, int y, int N) {
        return x >= 0 && y >= 0 && x < N && y < N;
    }

    static void createPath(Cell cell) {
        if (cell == null) {
            return;
        }
        createPath(cell.getParent());
        cellPath.add(cell);
    }

    // Find minimum number of steps taken by the knight
    // from source to reach destination using BFS
    public static Cell BFS(Cell src, Cell dest, int N) {
        // set to check if matrix cell is visited before or not
        Set<Cell> visited = new HashSet<>();

        // create a queue and enqueue first node
        Queue<Cell> q = new ArrayDeque<>();
        q.add(src);

        // run till queue is not empty
        while (!q.isEmpty()) {
            // pop front node from queue and process it
            Cell cell = q.poll();

            int x = cell.getX();
            int y = cell.getY();
            int dist = cell.getDist();

            // if destination is reached, return distance
            if (x == dest.getX() && y == dest.getY())
                return cell;

            // Skip if location is visited before
            if (!visited.contains(cell)) {
                // mark current node as visited
                visited.add(cell);

                // check for all 8 possible movements for a knight
                // and enqueue each valid movement into the queue
                for (int i = 0; i < 8; ++i) {
                    // Get the new valid position of Knight from current
                    // position on chessboard and enqueue it in the
                    // queue with +1 distance
                    int x1 = x + row[i];
                    int y1 = y + col[i];

                    if (valid(x1, y1, N))
                        q.add(new Cell(x1, y1, dist + 1, cell));
                }
            }
        }

        // return INFINITY if path is not possible
        return null;
    }
}