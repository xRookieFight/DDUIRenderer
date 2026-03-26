# DDUIRenderer

> Real-time ASCII 3D wireframe rendering inside a DDUI screen.

> Originally inspired from jeanmajid's https://github.com/jeanmajid/MCPE-DDUIRenderer.

---

## How it works

Each tick the renderer clears a 2D character buffer, rotates every edge of the model, runs [Bresenham's line algorithm](https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm) to rasterise it, then flattens the buffer to a newline-separated string and pushes it through an DDUI `Observable`.

The projection is a simple perspective divide with no matrices:
```
screenX = ((x / (z + cameraZ) + 1) / 2) * width
screenY = ((1 - y / (z + cameraZ)) / 2) * height
```

---

## Quick start

### Importing the library

```kts
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.xRookieFight:DDUIRenderer:latest")
}
```

```java
Renderer renderer = new DDUIRenderer(plugin, new CubeModel());
renderer.show(player);

// Stop when done
renderer.stop();
```

---

## Custom models

Implement `WireframeModel` - two methods, nothing else required.

```java
public class PyramidModel implements WireframeModel {

    private static final double[][] VERTICES = {
        {  0,    0.5,  0   },  // 0 - apex
        { -0.5, -0.5, -0.5 },  // 1
        {  0.5, -0.5, -0.5 },  // 2
        {  0.5, -0.5,  0.5 },  // 3
        { -0.5, -0.5,  0.5 },  // 4
    };

    private static final int[][] INDICES = {
        { 1, 2, 3, 4 },        // base
        { 0, 1, 2 },           // front face
        { 0, 2, 3 },           // right face
        { 0, 3, 4 },           // back face
        { 0, 4, 1 },           // left face
    };

    @Override public double[][] getVertices() { return VERTICES; }
    @Override public int[][]    getIndices()  { return INDICES;  }
}
```

Then pass it to the renderer:

```java
Renderer renderer = new DDUIRenderer(plugin, new PyramidModel());
renderer.show(player);
```

---

## Custom renderers

Extend `AbstractWireframeRenderer` to change any part of the pipeline.

```java
public class MyRenderer extends AbstractWireframeRenderer {

    public MyRenderer(Plugin plugin) { super(plugin); }

    @Override protected WireframeModel getModel()  { return new PyramidModel(); }

    @Override protected String getTitle()   { return "My Renderer"; }
    @Override protected int    getWidth()   { return 32; }
    @Override protected int    getHeight()  { return 20; }
    @Override protected double getCameraZ() { return 0.8; }
    @Override protected String getColor1()  { return "·"; }
    @Override protected String getColor2()  { return "█"; }

    @Override
    protected void drawFrame(double angle) {
        for (int[] face : getModel().getIndices()) {
            for (int i = 0; i < face.length; i++) {
                double[] v1 = getModel().getVertices()[face[i]];
                double[] v2 = getModel().getVertices()[face[(i + 1) % face.length]];
                line(rotateXZ(v1, angle), rotateXZ(v2, angle)); // XZ only
            }
        }
    }
}
```

---

## License
[MIT License](./LICENSE)