package additional;

import java.util.ArrayList;

public class Successor
{

    private int distance;
    private Direction cameFromDirection = new Direction(0, 0);
    private Successor cameFrom = null;

    private ArrayList<Successor> prunedNeighbors = new ArrayList<>();

    private static char[][] map;
    private int X;
    private int Y;

    public static void init_map(char[][] map) {
        Successor.map = map;
    }

    public Successor(int X, int Y){
        this.X = X;
        this.Y = Y;
        distance = 0;
    }

    public static Successor makeStart(int i, int j) {
        Successor successor = new Successor(i, j);
        successor.prunedNeighbors = successor.getNeighbors();
        return successor;
    }

    private Successor(int X, int Y, int distance) {
        this.X = X;
        this.Y = Y;
        this.distance = distance;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public Successor getParent() {
        return cameFrom;
    }

    public void setParent(Successor cameFrom) {
        this.cameFrom = cameFrom;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Direction getParentDirection() {
        return cameFromDirection;
    }

    public void setParentDirection(Direction parentDirection) {
        cameFromDirection = parentDirection;
    }

    public char getPointType()
    {
        try {
            return map[this.X][this.Y];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return '#';
        }
    }

    public boolean prevPointHadVerticalNeighbors(Direction direction)
    {
        Successor prevPoint = Successor.move(this, direction.opposite());
        Successor[] near = new Successor[2];

        if(prevPoint.getPointType() != '#')
        {
            near[0] = Successor.move(prevPoint, Direction.DOWN);
            near[1] = Successor.move(prevPoint, Direction.UP);
            Successor neighbor;
            boolean had = false;
            for(int i = 0; i < 2; i++) {
                neighbor = Successor.move(near[i], direction);
                if (near[i].getPointType() == '#' && neighbor.getPointType() != '#') {
                    prunedNeighbors.add(neighbor);
                    had = true;
                }
            }
            if(had) {
                Successor nextNeigh = Successor.move(this, direction);
                if(nextNeigh.getPointType() != '#')
                    prunedNeighbors.add(Successor.move(this, direction));
            }
            return had;
        }
        return false;
    }

    public Direction computeDirectionTo(Successor new_node) {
        return new Direction(this, new_node);
    }

    public static Successor move(Successor successor, Direction vector) {
        int x = successor.X + vector.getX();
        int y = successor.Y + vector.getY();
        return new Successor(x, y, successor.distance + 1);
    }

    public void moveThis(Direction vector) {
        X += vector.getX();
        Y += vector.getY();
        distance += 1;
    }

    private ArrayList<Successor> getNeighbors() {
        ArrayList<Successor> neighs = new ArrayList<>();
        int X = getX();
        int Y = getY();
        neighs.add(new Successor(X, Y-1));
        neighs.add(new Successor(X, Y+1));
        neighs.add(new Successor(X-1, Y));
        neighs.add(new Successor(X+1, Y));
        return neighs;
    }

    public ArrayList<Successor> getPrunedNeighbors()
    {
        return prunedNeighbors;
    }

    public void addNeighbor(Direction direction) {
        Successor neighbor = Successor.move(this, direction);
        if(neighbor.getPointType() != '#')
            prunedNeighbors.add(neighbor);
    }

    @Override
    public int hashCode() {
        return Integer.parseInt("" + getX() + getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return getX() == ((Successor) o).getX() && getY() == ((Successor) o).getY();

    }

    @Override
    public String toString() {
        return "" + X + " " + Y;
    }
}
