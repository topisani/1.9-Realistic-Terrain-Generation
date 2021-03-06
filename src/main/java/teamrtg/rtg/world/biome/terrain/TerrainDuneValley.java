package teamrtg.rtg.world.biome.terrain;

import teamrtg.rtg.util.noise.CellNoise;
import teamrtg.rtg.util.noise.OpenSimplexNoise;

public class TerrainDuneValley extends TerrainBase {
    private float valley;

    public TerrainDuneValley(float valleySize) {
        valley = valleySize;
    }

    @Override
    public float generateNoise(OpenSimplexNoise simplex, CellNoise cell, int x, int y, float border, float river) {
        return terrainDuneValley(x, y, simplex, cell, river, valley, 65f, 70f);
    }
}
