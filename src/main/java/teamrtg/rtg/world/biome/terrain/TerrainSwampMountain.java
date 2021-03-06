package teamrtg.rtg.world.biome.terrain;

import teamrtg.rtg.util.noise.CellNoise;
import teamrtg.rtg.util.noise.OpenSimplexNoise;

public class TerrainSwampMountain extends TerrainBase {
    private float heigth;
    private float width;

    public TerrainSwampMountain(float mountainHeight, float mountainWidth) {
        heigth = mountainHeight;
        width = mountainWidth;
    }

    @Override
    public float generateNoise(OpenSimplexNoise simplex, CellNoise cell, int x, int y, float border, float river) {
        return terrainSwampMountain(x, y, simplex, cell, river, width, heigth, 150f, 32f, 56f);
    }
}
