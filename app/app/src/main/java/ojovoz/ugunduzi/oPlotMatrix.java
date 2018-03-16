package ojovoz.ugunduzi;

import android.graphics.Point;
import android.view.MotionEvent;
import java.util.ArrayList;

/**
 * Created by Eugenio on 16/03/2018.
 */
public class oPlotMatrix {

    public ArrayList<oPlot> plots;
    public oPlot currentPlot;

    matrixContent[][] matrix;
    int displayWidth;
    int displayHeight;

    int state; //0 = creating, 1 = adding data

    oPlotMatrix(){
        plots = new ArrayList<>();
        matrix = new matrixContent[4][4];
    }

    public void createMatrix(int w, int h){
        displayWidth=w;
        displayHeight=h;
        int x=0;
        int y=0;
        for(int i=0;i<4;i++){
            y=(h/4)*i;
            for(int j=0;j<4;j++){
                x=(w/4)*j;
                matrix[j][i] = new matrixContent(null,new Point(x,y));
            }
        }
    }

    public void setCurrentPlot(oPlot p){
        currentPlot = p;
    }

    public void addPlot(){
        matrixContent cell = findFirstAvailablePosition();
        if(cell.point!=null) {
            oPlot p = new oPlot(cell.point.x, cell.point.y, displayWidth / 4, displayHeight / 4);
            setCurrentPlot(p);
            plots.add(p);
            cell.plot=p;
        }
    }

    public matrixContent findFirstAvailablePosition(){
        matrixContent ret = new matrixContent(null,null);
        boolean bFound=false;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                matrixContent cell = matrix[j][i];
                if(cell.plot==null){
                    ret=cell;
                    bFound=true;
                    break;
                }
            }
            if(bFound){
                break;
            }
        }
        return ret;
    }

    public void passEvent(MotionEvent e){

    }

    public ArrayList<oPlot> getPlots(){
        return plots;
    }

    private class matrixContent {
        public oPlot plot;
        public Point point;

        matrixContent(oPlot rP, Point rPoint){
            plot=rP;
            point=rPoint;
        }
    }
}
