package additional;

public class Direction
{
    private int X;
    private int Y;

    public static Direction RIGHT = new Direction(0, 1);
    public static Direction LEFT = new Direction(0, -1);

    Direction(int X, int Y){
        this.X = X;
        this.Y = Y;
    }

    Direction(Successor source, Successor destination)
    {
        if(source != null) {
            X = destination.getX() - source.getX();
            Y = destination.getY() - source.getY();
            if(X > 0) X = 1;
            else if (X < 0) X = -1;
            if(Y > 0) Y = 1;
            else if (Y < 0) Y = -1;
        }
        else {
            this.X = 0;
            this.Y = 0;
        }
    }

    public Direction opposite() {
        return new Direction(-X, -Y);
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }
}