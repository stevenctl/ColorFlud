package co.sugarware.colorflud;


import com.badlogic.gdx.graphics.Color;

public enum TileColor {
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    PURPLE,
    NONE;

    private static final TileColor[] playableValues = new TileColor[]{
            RED,
            ORANGE,
            YELLOW,
            GREEN,
            BLUE,
            PURPLE
    };

    public Color getColor(){
        switch(this.name().toCharArray()[0]){
            case 'R':
                return Color.RED;
            case 'B':
                return Color.BLUE;
            case 'G':
                return Color.GREEN;
            case 'Y':
                return Color.YELLOW;
            case 'O':
                return Color.ORANGE;
            case 'P':
                return Color.PURPLE;
            case 'N':
                return Color.CLEAR;
            default:
                throw new IllegalArgumentException(this.name() + "is not a valid TileColor");
        }
    }

    public static TileColor[] playableValues(){
        return playableValues;
    }
}
