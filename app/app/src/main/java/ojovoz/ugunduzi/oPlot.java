package ojovoz.ugunduzi;

/**
 * Created by Eugenio on 15/03/2018.
 */
public class oPlot {

    public int x;
    public int y;
    public float w;
    public float h;

    public oPlotCanMove plotCanMove;
    public int state; // 0 = default; 1 = touched; 2 = moving; 3 = resizing

    public int iMoveX;
    public int iMoveY;
    public int iMoveW;
    public int iMoveH;
    public int iResizeX;
    public int iResizeY;
    public int iResizeW;
    public int iResizeH;

    oPlot(){
        plotCanMove = new oPlotCanMove();
        state=0;
    }

    oPlot(int rX, int rY, int rW, int rH){
        x=rX;
        y=rY;
        w=rW;
        h=rH;

        plotCanMove = new oPlotCanMove();

        state=0;
    }

    public void addAreas(int rIMoveW, int rIMoveH, int rIResizeW, int rIResizeH){
        iMoveW = rIMoveW;
        iMoveH = rIMoveH;
        iResizeW = rIResizeW;
        iResizeH = rIResizeH;
        calculateAreasXY();
    }

    public void calculateAreasXY(){
        iMoveX = (int)(x+(w/2))-(iMoveW/2);
        iMoveY = (int)(y+(h/2)-(iMoveH/2));
        iResizeX = (int)((w+x)-iResizeW-2);
        iResizeY = (int)((h+y)-iResizeH-2);
    }
}
