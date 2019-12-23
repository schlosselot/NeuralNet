package Enum;

public enum Actions {
    moveUp,
    moveDown,
    moveLeft,
    moveRight,
    fire,
    none;


    public static Actions fromInt(int x){
        switch(x){
            case 0:
                return moveUp;
            case 1:
                return moveDown;
            case 2:
                return moveLeft;
            case 3:
                return moveRight;
            case 4:
                return fire;
            case 5:
                return none;
        }
        return null;
    }

    public static int getSize(){return 6;}
}
