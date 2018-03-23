package ojovoz.ugunduzi;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Eugenio on 16/03/2018.
 */
public class oPlotMatrix {

    public ArrayList<oPlot> plots;
    public oPlot currentPlot;

    matrixContent[][] matrix;
    int displayWidth;
    int displayHeight;

    oPlot ghostPlot;
    int offsetX;
    int offsetY;
    int startX;
    int startY;

    int plotIndex=0;

    oPlotMatrix() {
        plots = new ArrayList<>();
        matrix = new matrixContent[4][4];
        currentPlot = new oPlot();
    }

    public void createMatrix(int w, int h) {
        displayWidth = w;
        displayHeight = h;
        int x;
        int y;
        for (int i = 0; i < 4; i++) {
            y = (h / 4) * i;
            for (int j = 0; j < 4; j++) {
                x = (w / 4) * j;
                matrix[j][i] = new matrixContent(null, new Point(x, y));
            }
        }
    }

    public void setCurrentPlot(oPlot p) {
        currentPlot = p;
    }

    public boolean addPlot(int iMoveW, int iMoveH, int iResizeW, int iResizeH, int iContentsW, int iContentsH) {
        boolean ret;
        matrixContent cell = findFirstAvailablePosition();
        if (cell.point != null) {
            oPlot p = new oPlot(cell.point.x, cell.point.y, displayWidth / 4, displayHeight / 4);
            p.addAreas(iMoveW, iMoveH, iResizeW, iResizeH, iContentsW, iContentsH);
            setCurrentPlot(p);
            p.id=plotIndex;
            plotIndex++;
            plots.add(p);
            cell.plot = p;
            ret=true;
        } else {
            ret=false;
        }
        return ret;
    }

    public matrixContent findFirstAvailablePosition() {
        matrixContent ret = new matrixContent(null, null);
        boolean bFound = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrixContent cell = matrix[j][i];
                if (cell.plot == null) {
                    ret = cell;
                    bFound = true;
                    break;
                }
            }
            if (bFound) {
                break;
            }
        }
        return ret;
    }

    public boolean passEvent(MotionEvent e) {
        if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
            currentPlot = getTouchedPlot((int) e.getX(), (int) e.getY());
            if (currentPlot != null) {
                if (currentPlot.state == 2 || currentPlot.state == 3) {
                    ghostPlot = new oPlot(currentPlot.x, currentPlot.y, (int) currentPlot.w, (int) currentPlot.h);
                    offsetX = (int) (e.getX() - ghostPlot.x);
                    offsetY = (int) (e.getY() - ghostPlot.y);
                    startX = (int) e.getX();
                    startY = (int) e.getY();
                    return true;
                } else {
                    ghostPlot = null;
                    return false;
                }
            } else {
                ghostPlot = null;
                return false;
            }

        } else if (e.getActionMasked() == MotionEvent.ACTION_UP) {
            if (currentPlot != null) {
                if (ghostPlot != null) {
                    snapToGrid();
                    currentPlot.state = 0;
                    ghostPlot = null;
                }
                return true;
            } else {
                return false;
            }
        } else if (e.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (currentPlot != null && ghostPlot != null) {
                if (currentPlot.state == 2) {
                    moveGhostPlot((int) e.getX(), (int) e.getY(), (int) ghostPlot.w, (int) ghostPlot.h);
                } else if (currentPlot.state == 3) {
                    resizeGhostPlot((int) e.getX(), (int) e.getY(), (int) ghostPlot.w, (int) ghostPlot.h);
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public oPlot getTouchedPlot(int x, int y) {
        oPlot ret = null;
        if (currentPlot != null) {
            currentPlot.state = 0;
        }
        Iterator<oPlot> iterator = plots.iterator();
        while (iterator.hasNext()) {
            oPlot plot = iterator.next();
            if (isWithin(plot, x, y)) {
                if (isMoving(plot, x, y)) {
                    plot.state = 2;
                } else if (isResizing(plot, x, y)) {
                    plot.state = 3;
                } else if (isEditing(plot, x, y)) {
                    plot.state = 4;
                } else {
                    plot.state = 1;
                }
                ret = plot;
                break;
            }
        }
        return ret;
    }

    public boolean isWithin(oPlot p, int x, int y) {
        boolean ret = false;
        if (x > p.x && x < (p.x + p.w) && y > p.y && y < (p.y + p.h)) {
            ret = true;
        }
        return ret;
    }

    public boolean isMoving(oPlot p, int x, int y) {
        boolean ret = false;
        if (x > p.iMoveX && x < (p.iMoveX + p.iMoveW) && y > p.iMoveY && y < (p.iMoveY + p.iMoveH)) {
            ret = true;
        }
        return ret;
    }

    public boolean isResizing(oPlot p, int x, int y) {
        boolean ret = false;
        if (x > p.iResizeX && x < (p.iResizeX + p.iResizeW) && y > p.iResizeY && y < (p.iResizeY + p.iResizeH)) {
            ret = true;
        }
        return ret;
    }

    public boolean isEditing(oPlot p, int x, int y){
        boolean ret = false;
        if (x > p.iContentsX && x < (p.iContentsX + p.iContentsW) && y > p.iContentsY && y < (p.iContentsY + p.iContentsH)) {
            ret = true;
        }
        return ret;
    }

    public void moveGhostPlot(int x, int y, int w, int h) {
        if ((x - offsetX) >= 0 && ((x - offsetX) + w) < displayWidth) {
            ghostPlot.x = x - offsetX;
        } else {
            offsetX = x - ghostPlot.x;
        }
        if ((y - offsetY) >= 0 && ((y - offsetY) + h) < displayHeight) {
            ghostPlot.y = y - offsetY;
        } else {
            offsetY = y - ghostPlot.y;
        }
        if (!isWithin(ghostPlot, x, y)) {
            currentPlot.state = 0;
            ghostPlot = null;
        }
    }

    public void resizeGhostPlot(int x, int y, int w, int h) {
        int varX = startX - x;
        int varY = startY - y;
        if ((w - varX) >= (displayWidth / 4) && (w - varX) < displayWidth) {
            ghostPlot.w = w - varX;
            startX = x;
        }
        if ((h - varY) >= (displayHeight / 4) && (h - varY) < displayHeight) {
            ghostPlot.h = h - varY;
            startY = y;
        }
    }

    public void snapToGrid() {
        float destX = ghostPlot.x;
        float destY = ghostPlot.y;
        float destW = ghostPlot.w;
        float destH = ghostPlot.h;

        int closestX = 0;
        int matrixX = 0;
        int matrixY = 0;
        int closestY = 0;
        double minDist = 9999;

        for (int ix = 0; ix < displayWidth; ix += (displayWidth / 4)) {
            for (int iy = 0; iy < displayHeight; iy += (displayHeight / 4)) {
                double dist = Math.hypot(destX - ix, destY - iy);
                if (dist < minDist) {
                    minDist = dist;
                    closestX = ix;
                    closestY = iy;
                    matrixX = Math.round(ix / (displayWidth / 4));
                    matrixY = Math.round(iy / (displayHeight / 4));
                }
            }
        }

        if (fitInMatrix(matrixX, matrixY, destW, destH)) {
            currentPlot.x = closestX;
            currentPlot.y = closestY;
            currentPlot.w = Math.round(destW / (displayWidth / 4)) * (displayWidth / 4);
            currentPlot.h = Math.round(destH / (displayHeight / 4)) * (displayHeight / 4);
            currentPlot.calculateAreasXY();
        }

    }

    public boolean fitInMatrix(int matrixX, int matrixY, float destW, float destH) {
        boolean ret = true;
        int matrixX2 = matrixX + Math.round(destW / (displayWidth / 4));
        int matrixY2 = matrixY + Math.round(destH / (displayHeight / 4));
        for (int y = matrixY; y < matrixY2; y++) {
            if (ret) {
                for (int x = matrixX; x < matrixX2; x++) {
                    matrixContent mc = matrix[x][y];
                    if (mc.plot != null && mc.plot != currentPlot) {
                        ret = false;
                        break;
                    }
                }
            }
        }
        if (ret) {
            deletePlotFromMatrix(currentPlot);
            addPlotToMatrix(currentPlot, matrixX, matrixY, matrixX2, matrixY2);
        }
        return ret;
    }

    public void deletePlotFromMatrix(oPlot p) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                matrixContent mc = matrix[x][y];
                if (mc.plot == p) {
                    mc.plot = null;
                }
            }
        }
    }

    public void addPlotToMatrix(oPlot p, int x1, int y1, int x2, int y2) {
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                matrixContent mc = matrix[x][y];
                mc.plot = p;
            }
        }
    }

    public boolean deletePlot(){
        boolean ret=true;
        if(plots.size()>1){
            plots.remove(currentPlot);
            deletePlotFromMatrix(currentPlot);
            currentPlot=null;
        } else {
            ret=false;
        }
        return ret;
    }

    public ArrayList<oPlot> getPlots() {
        return plots;
    }

    private class matrixContent {
        public oPlot plot;
        public Point point;

        matrixContent(oPlot rP, Point rPoint) {
            plot = rP;
            point = rPoint;
        }
    }

    public String toString(){
        String ret="";
        boolean bFound;
        Iterator<oPlot> iterator = plots.iterator();
        while (iterator.hasNext()) {
            oPlot plot = iterator.next();
            String plotString=String.valueOf(plot.id) + ";";
            bFound=false;
            for (int y = 0; y < 4; y++) {
                if(!bFound) {
                    for (int x = 0; x < 4; x++) {
                        matrixContent mc = matrix[x][y];
                        if (mc.plot == plot) {
                            plotString = plotString + String.valueOf(x) + ";" + String.valueOf(y) + ";";
                            bFound=true;
                            break;
                        }
                    }
                } else {
                    break;
                }
            }
            plotString = plotString + String.valueOf(Math.round(plot.w/(displayWidth/4))) + ";" + String.valueOf(Math.round(plot.h/(displayHeight/4))) + ";";
            String crop1 = (plot.crop1!=null) ? String.valueOf(plot.crop1.id) : "0";
            String crop2 = (plot.crop2!=null) ? String.valueOf(plot.crop2.id) : "0";
            String treatment1 = (plot.treatment1!=null) ? String.valueOf(plot.treatment1.id) : "0";
            String treatment2 = (plot.treatment2!=null) ? String.valueOf(plot.treatment2.id) : "0";
            plotString = plotString + crop1 + ";" + crop2 + ";" + treatment1 + ";" + treatment2;
            if(ret.isEmpty()){
                ret=plotString;
            } else {
                ret=ret + ";" + plotString;
            }
        }
        return ret;
    }
}
