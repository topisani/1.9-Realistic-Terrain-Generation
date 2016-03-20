package rtg.world.gen.terrain.buildcraft;

import rtg.util.CellNoise;
import rtg.util.OpenSimplexNoise;
import rtg.world.gen.terrain.TerrainBase;

public class TerrainBCDesertOilField extends TerrainBase {

    public TerrainBCDesertOilField() {

    }

    @Override
    public float generateNoise(OpenSimplexNoise simplex, CellNoise cell, int x, int y, float border, float river) {
        return terrainPolar(x, y, simplex, river);
    }
}
