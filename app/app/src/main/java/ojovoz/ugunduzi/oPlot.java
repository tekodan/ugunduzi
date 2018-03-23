package ojovoz.ugunduzi;

/**
 * Created by Eugenio on 15/03/2018.
 */
public class oPlot {

    public int id;

    public int x;
    public int y;
    public float w;
    public float h;

    public int state; // 0 = default; 1 = touched; 2 = moving; 3 = resizing; 4 = editing contents

    public int iMoveX;
    public int iMoveY;
    public int iMoveW;
    public int iMoveH;
    public int iResizeX;
    public int iResizeY;
    public int iResizeW;
    public int iResizeH;
    public int iContentsX;
    public int iContentsY;
    public int iContentsW;
    public int iContentsH;

    public oCrop crop1;
    public oCrop crop2;
    public oTreatment treatment1;
    public oTreatment treatment2;

    oPlot(){
        state=0;
    }

    oPlot(int rX, int rY, int rW, int rH){
        x=rX;
        y=rY;
        w=rW;
        h=rH;

        state=0;
    }

    public void addAreas(int rIMoveW, int rIMoveH, int rIResizeW, int rIResizeH, int rIContentsW, int rIContentsH){
        iMoveW = rIMoveW;
        iMoveH = rIMoveH;
        iResizeW = rIResizeW;
        iResizeH = rIResizeH;
        iContentsW = rIContentsW;
        iContentsH = rIContentsH;
        calculateAreasXY();
    }

    public void calculateAreasXY(){
        iMoveX = (int)(x+(w/2))-(iMoveW/2);
        iMoveY = (int)(y+(h/2)-(iMoveH/2));
        iResizeX = (int)((w+x)-iResizeW-2);
        iResizeY = (int)((h+y)-iResizeH-2);
        iContentsX = x+2;
        iContentsY = y+2;
    }
}
