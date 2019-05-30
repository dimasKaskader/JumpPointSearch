import additional.Direction;
import additional.Successor;

import java.util.*;

public class JumpPointSearch implements Navigator {

    private Successor goal;
    private Successor start;
    private ArrayList<Successor> jumpPoints = new ArrayList<>();
    private static char[][] outputMap;

    private void init(char[][] map)
    {
        outputMap = map.clone();
        Successor.init_map(outputMap);
        int width = outputMap.length - 1;
        int height = outputMap[0].length - 1;
        for(int i = 0; i <= width; i++)
            for(int j = 0; j <= height; j++)
            {
                if(outputMap[i][j] == '@')
                    start = Successor.makeStart(i, j);
                else if(outputMap[i][j] == 'X')
                    goal = new Successor(i, j);
            }
    }

    private class PriorityComparator implements Comparator<Successor>
    {
        public int compare(Successor o1, Successor o2)
        {
            return (heuristic(o1, goal) + o1.getDistance()) - (heuristic(o2, goal) + o2.getDistance());
        }
        private int heuristic(Successor a, Successor b)
        {
            return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
        }
    }

    public char[][] searchRoute(char[][] map)
    {
        init(map);

        PriorityQueue<Successor> frontier = new PriorityQueue<>(10, new PriorityComparator());

        frontier.add(start);
        jumpPoints.add(start);

        Successor current;
        while(true) {
            current = frontier.poll();
            if(current.equals(goal)) {
                goal = current;
                break;
            }

            ArrayList<Successor> successors = identifySuccessors(current, goal);
            for(Successor successor: successors)
            {
                int existingSuccessorId = jumpPoints.indexOf(successor);
                if(existingSuccessorId < 0) {
                    frontier.add(successor);
                    jumpPoints.add(successor);
                }
                else if (jumpPoints.get(existingSuccessorId).getDistance() > successor.getDistance()) {
                    jumpPoints.set(existingSuccessorId, successor);
                }
            }
            if (frontier.size() == 0)
                return null;
        }

        buildPath();

        /*for(Successor jump: jumpPoints) {
            outputMap[jump.getX()][jump.getY()] = 'J';
            for(Successor neigh: jump.getPrunedNeighbors()) {
                outputMap[neigh.getX()][neigh.getY()] = 'n';
            }
        }*/

        return outputMap;
    }

    private void buildPath() {
        Successor current, next;
        current = goal;
        while (current != start) {
            next = current.getParent();
            fillMap(next.getX(), current.getX(), 0, next.getY());
            fillMap(next.getY(), current.getY(), 1, next.getX());
            current = next;
        }
        outputMap[current.getX()][current.getY()] = '@';
        outputMap[goal.getX()][goal.getY()] = 'X';
    }

    private void fillMap(int from, int to, int axis, int otherAxisValue) {
        int temp;
        if(from > to) {
            temp = from;
            from = to;
            to = temp;
        }
        if(axis == 0) {
            for (int i = from; i <= to; i++)
                outputMap[i][otherAxisValue] = '+';
        }
        else {
            for (int i = from; i <= to; i++)
                outputMap[otherAxisValue][i] = '+';
        }
    }

    private ArrayList<Successor> identifySuccessors(Successor currentNode, Successor goal) {
        ArrayList<Successor> successors = new ArrayList<>();
        ArrayList<Successor> neighbors = currentNode.getPrunedNeighbors();

        for (Successor neighbor : neighbors) {
            Direction directionToNeigh = currentNode.computeDirectionTo(neighbor);
            Successor[] newSuccessors = null;

            if (directionToNeigh.isVertical())
                newSuccessors = verticalJump(neighbor, directionToNeigh, goal);
            else {
                Successor newSuccessor = horizontalJump(neighbor, directionToNeigh, goal);
                if (newSuccessor != null)
                    newSuccessors = new Successor[]{newSuccessor};
            }

            if (newSuccessors != null) {
                Direction parentDirection;
                Successor parent = currentNode;
                for(Successor successor: newSuccessors) {
                    parentDirection = successor.computeDirectionTo(currentNode);
                    successor.setParentDirection(parentDirection);
                    successor.setParent(parent);
                    successors.add(successor);
                    parent = successor;
                }
            }
        }
        return successors;
    }

    private Successor horizontalJump(Successor initNode, Direction direction, Successor goal)
    {
        if (initNode.equals(goal))
            return initNode;

        if (initNode.prevPointHadVerticalNeighbors(direction))
            return initNode;

        initNode.moveThis(direction);
        if (initNode.getPointType() == '#')
            return null;

        return horizontalJump(initNode, direction, goal);
    }

    private Successor[] verticalJump(Successor initNode, Direction direction, Successor goal)
    {
        if (initNode.equals(goal))
            return new Successor[] {initNode};

        Successor firstToTheRight = Successor.move(initNode, Direction.RIGHT);
        if (firstToTheRight.getPointType() != '#') {
            Successor rightJump = horizontalJump(firstToTheRight, Direction.RIGHT, goal);
            if (rightJump != null) {
                initNode.addNeighbor(direction);
                initNode.addNeighbor(Direction.LEFT);
                return new Successor[]{initNode, rightJump};
            }
        }

        Successor firstToTheLeft = Successor.move(initNode, Direction.LEFT);
        if (firstToTheLeft.getPointType() != '#') {
            Successor leftJump = horizontalJump(firstToTheLeft, Direction.LEFT, goal);
            if (leftJump != null) {
                initNode.addNeighbor(direction);
                return new Successor[]{initNode, leftJump};
            }
        }

        initNode.moveThis(direction);
        if (initNode.getPointType() == '#')
            return null;

        return verticalJump(initNode, direction, goal);
    }
}
