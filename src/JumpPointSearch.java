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
                    start = new Successor(i, j);
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
                    successor.setParent(current);
                }
                else if (jumpPoints.get(existingSuccessorId).getDistance() > successor.getDistance()) {
                    jumpPoints.set(existingSuccessorId, successor);
                    successor.setParent(current);
                }
            }
            if (frontier.size() == 0)
                return null;
        }

        buildPath();
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

    private ArrayList<Successor> identifySuccessors(Successor currentNode, Successor goal)
    {
        ArrayList<Successor> successors = new ArrayList<>();
        ArrayList<Successor> neighbors = currentNode.getNeighbors();
        for(Successor neighbor: neighbors)
        {
            Direction directionToNeigh = currentNode.computeDirectionTo(neighbor);
            Successor newSuccessor = jump(neighbor, directionToNeigh, goal);
            if(newSuccessor != null) {
                Direction parentDirection = currentNode.computeDirectionTo(newSuccessor).opposite();
                newSuccessor.setParentDirection(parentDirection);
                successors.add(newSuccessor);
            }
        }
        return successors;
    }

    private Successor jump(Successor initNode, Direction direction, Successor goal)
    {
        if (initNode.equals(goal))
            return initNode;

        if (direction.getY() != 0) {
            if (initNode.prevPointHadVerticalNeighbors(direction))
                return initNode;
        }
        else if(direction.getX() != 0){
            Successor firstToTheRight = initNode.move(Direction.RIGHT);
            if (firstToTheRight.getPointType() != '#')
                if (jump(firstToTheRight, Direction.RIGHT, goal) != null)
                    return initNode;

            Successor firstToTheLeft = initNode.move(Direction.LEFT);
            if (firstToTheLeft.getPointType() != '#')
                if (jump(firstToTheLeft, Direction.LEFT, goal) != null)
                    return initNode;
        }

        Successor newPoint = initNode.move(direction);
        if (newPoint.getPointType() == '#')
            return null;

        return jump(newPoint, direction, goal);
    }
}
