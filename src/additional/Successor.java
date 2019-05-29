package additional;

import java.util.ArrayList;

public class Successor
{
    private int distance;
    private Direction cameFromDirection = new Direction(0, 0);
    private Successor cameFrom = null;

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
        Successor prevPoint = move(direction.opposite());
        Successor[] near = new Successor[2];

        if(prevPoint.getPointType() != '#')
        {
            near[0] = prevPoint.move(new Direction(1, 0));
            near[1] = prevPoint.move(new Direction(-1, 0));

            for(int i = 0; i < 2; i++)
                if (near[i].getPointType() == '#' && near[i].move(direction).getPointType() != '#') {
                    return true;
                }
        }
        return false;
    }

    public Direction computeDirectionTo(Successor new_node) {
        return new Direction(this, new_node);
    }

    public Successor move(Direction vector) {
        int x = this.X + vector.getX();
        int y = this.Y + vector.getY();
        return new Successor(x, y, distance + 1);
    }

    public ArrayList<Successor> getNeighbors()
    {
        ArrayList<Successor> neighs = new ArrayList<>();
        Successor[] possibleNeighs = new Successor[4];
        int X = getX();
        int Y = getY();
        possibleNeighs[0] = new Successor(X, Y-1);
        possibleNeighs[1] = new Successor(X, Y+1);
        possibleNeighs[2] = new Successor(X-1, Y);
        possibleNeighs[3] = new Successor(X+1, Y);
        for(Successor neigh: possibleNeighs)
            if(neigh.getPointType() != '#' && !neigh.equals(move(cameFromDirection))) {
                neigh.distance = distance + 1;
                neighs.add(neigh);
            }
        return neighs;
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
}
