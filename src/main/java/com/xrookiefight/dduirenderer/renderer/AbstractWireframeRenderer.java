package com.xrookiefight.dduirenderer.renderer;

import cn.nukkit.Player;
import cn.nukkit.ddui.CustomForm;
import cn.nukkit.ddui.Observable;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import com.xrookiefight.dduirenderer.model.WireframeModel;

public abstract class AbstractWireframeRenderer implements Renderer {

    protected String getTitle()   { return "3D"; }

    protected int    getWidth()   { return 24; }

    protected int    getHeight()  { return 16; }

    protected double getCameraZ() { return 0.6; }

    protected String getColor1()  { return "□"; }

    protected String getColor2()  { return "■"; }

    private static final int    PERIOD_TICKS = 1;
    private static final double DELTA_TIME   = 1.0 / 20.0;

    protected abstract WireframeModel getModel();

    private final PluginBase plugin;
    private final WireframeModel     model;
    private final Observable<String> mapObservable;
    private final String[][]         mapBuffer;

    private double angle  = 0.0;
    private int    taskId = -1;

    protected AbstractWireframeRenderer(PluginBase plugin) {
        this.plugin        = plugin;
        this.model         = getModel();
        this.mapObservable = new Observable<>("");
        this.mapBuffer     = new String[getHeight()][getWidth()];
        clearScreen();
        startRenderLoop();
    }

    @Override
    public void show(Player player) {
        new CustomForm(getTitle())
                .label(mapObservable)
                .show(player);
    }

    @Override
    public void stop() {
        if (taskId != -1) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    protected void drawFrame(double angle) {
        for (int[] face : model.getIndices()) {
            for (int i = 0; i < face.length; i++) {
                double[] v1 = model.getVertices()[face[i]];
                double[] v2 = model.getVertices()[face[(i + 1) % face.length]];

                double[] rv1 = rotateXY(rotateXZ(v1, angle), angle);
                double[] rv2 = rotateXY(rotateXZ(v2, angle), angle);

                line(rv1, rv2);
            }
        }
    }

    private void startRenderLoop() {
        taskId = plugin.getServer().getScheduler()
                .scheduleRepeatingTask(plugin, new Task() {
                    @Override
                    public void onRun(int currentTick) {
                        clearScreen();
                        angle += (Math.PI * DELTA_TIME) / 10.0;
                        drawFrame(angle);

                        int h = getHeight(), w = getWidth();
                        StringBuilder sb = new StringBuilder(h * (w + 1));
                        for (int row = 0; row < h; row++) {
                            for (int col = 0; col < w; col++) {
                                sb.append(mapBuffer[row][col]);
                            }
                            if (row < h - 1) sb.append('\n');
                        }
                        mapObservable.setValue(sb.toString());
                    }
                }, PERIOD_TICKS).getTaskId();
    }

    private void clearScreen() {
        String empty = getColor1();
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                mapBuffer[row][col] = empty;
            }
        }
    }

    protected void setPixel(int x, int y) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
            mapBuffer[y][x] = getColor2();
        }
    }

    protected int[] screenProject(double[] v) {
        double zOffset = v[2] + getCameraZ();
        int sx = (int) Math.floor(((v[0] / zOffset + 1.0) / 2.0) * getWidth());
        int sy = (int) Math.floor(((1.0 - v[1] / zOffset) / 2.0) * getHeight());
        return new int[]{ sx, sy };
    }

    protected void line(double[] v1, double[] v2) {
        int[] p1 = screenProject(v1);
        int[] p2 = screenProject(v2);

        int x0 = p1[0], y0 = p1[1];
        int x1 = p2[0], y1 = p2[1];

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            setPixel(x0, y0);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 <  dx) { err += dx; y0 += sy; }
        }
    }

    protected double[] rotateXZ(double[] v, double a) {
        double cos = Math.cos(a), sin = Math.sin(a);
        return new double[]{ v[0] * cos - v[2] * sin, v[1], v[0] * sin + v[2] * cos };
    }

    protected double[] rotateXY(double[] v, double a) {
        double cos = Math.cos(a), sin = Math.sin(a);
        return new double[]{ v[0] * cos - v[1] * sin, v[0] * sin + v[1] * cos, v[2] };
    }
}