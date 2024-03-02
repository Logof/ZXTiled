package org.github.logof.zxtiled.mapeditor.gui.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class LineArrow {

    private Point beginPoint;
    private Point endPoint;

    int beginX;
    int beginY;
    int endX;
    int endY;
    Color color;
    int thickness;

    public LineArrow(int beginX, int beginY, int endX, int endY, Color color, int thickness) {
        super();
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;
        this.thickness = thickness;
    }

    public void draw(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();

        graphics2D.setColor(color);
        //        graphics2D.setStroke(new BasicStroke(thickness));
        graphics2D.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0F, (float[]) null, 0.0F));
        graphics2D.drawLine(beginX, beginY, endX, endY);
        drawArrowHead(graphics2D);
        graphics2D.dispose();
    }

    private void drawArrowHead(Graphics2D graphics2D) {
        double angle = Math.atan2(endY - beginY, endX - beginX);
        AffineTransform transform = graphics2D.getTransform();
        transform.translate(endX, endY);
        transform.rotate(angle - Math.PI / 2d);
        graphics2D.setTransform(transform);

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, 0);
        arrowHead.addPoint(-3 * thickness, -8 * thickness);
        arrowHead.addPoint(3 * thickness, -8 * thickness);
        graphics2D.fill(arrowHead);
    }
}
