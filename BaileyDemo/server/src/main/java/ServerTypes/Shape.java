package ServerTypes;

//A simple class that represents a shape object on the client, x and y for position, lineNo to show which line it is apart of 
//type can be square, circle or triangle (currently removed), colour is the colour for the shape
public class Shape {

    private int x, y, lineNo;
    private String type;
    private String colour;

    public Shape(int x, int y, String type, String color, int line){
        this.x = x;
        this.y = y;
        this.type = type;
        this.colour = color;
        this.lineNo = line;
    }
    
    public Shape(){
        
    }
    
      public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}

