package ojovoz.ugunduzi;

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

    boolean requestRedraw = false;

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

    public void addPlot(int iMoveW, int iMoveH, int iResizeW, int iResizeH) {
        matrixContent cell = findFirstAvailablePosition();
        if (cell.point != null) {
            oPlot p = new oPlot(cell.point.x, cell.point.y, displayWidth / 4, displayHeight / 4);
            p.addAreas(iMoveW, iMoveH, iResizeW, iResizeH);
            setCurrentPlot(p);
            plots.add(p);
            cell.plot = p;
        }
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
                //TODO snap to grid here! (move and resize)
                currentPlot.state = 0;
                if (ghostPlot != null) {
                    ghostPlot = null;
                    requestRedraw = true;
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
}
