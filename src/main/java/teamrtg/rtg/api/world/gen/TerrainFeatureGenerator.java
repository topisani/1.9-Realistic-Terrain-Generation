package teamrtg.rtg.api.world.gen;

import net.minecraft.world.chunk.ChunkPrimer;
import teamrtg.rtg.api.world.RTGWorld;
import teamrtg.rtg.api.world.biome.TerrainFeature;

/**
 * @author topisani
 */
public class TerrainFeatureGenerator implements IWorldFeatureGenerator {

    private final TerrainFeature terrainFeature;

    public TerrainFeatureGenerator(TerrainFeature terrainFeature) {
        this.terrainFeature = terrainFeature;
    }

    @Override
    public TerrainFeature get() {
        return this.terrainFeature;
    }

    @Override
    public float terrainHeight(RTGWorld rtgWorld, int x, int y, float border, float river) {
        return 0;
    }

    @Override
    public void paintSurface(ChunkPrimer primer, int bx, int bz, int depth, float[] noise, float river, RTGWorld rtgWorld) {

    }
}
