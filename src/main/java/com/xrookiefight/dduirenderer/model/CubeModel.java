package com.xrookiefight.dduirenderer.model;

public class CubeModel implements WireframeModel {

    private static final double[][] VERTICES = {
            {  0.25,  0.25,  0.25 },
            { -0.25,  0.25,  0.25 },
            { -0.25, -0.25,  0.25 },
            {  0.25, -0.25,  0.25 },

            {  0.25,  0.25, -0.25 },
            { -0.25,  0.25, -0.25 },
            { -0.25, -0.25, -0.25 },
            {  0.25, -0.25, -0.25 },
    };

    private static final int[][] INDICES = {
            {0, 1, 2, 3}, // front face
            {4, 5, 6, 7}, // back face
            {0, 4},       // connecting edges
            {1, 5},
            {2, 6},
            {3, 7}
    };

    @Override
    public double[][] getVertices() {
        return VERTICES;
    }

    @Override
    public int[][] getIndices() {
        return INDICES;
    }
}