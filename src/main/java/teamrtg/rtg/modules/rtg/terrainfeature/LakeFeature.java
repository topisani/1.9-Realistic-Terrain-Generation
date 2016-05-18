package teamrtg.rtg.modules.rtg.terrainfeature;

import net.minecraft.util.ResourceLocation;
import teamrtg.rtg.api.mods.Mods;
import teamrtg.rtg.api.world.biome.TerrainBase;
import teamrtg.rtg.api.world.biome.TerrainFeature;
import teamrtg.rtg.api.world.biome.deco.DecoBase;
import teamrtg.rtg.api.world.gen.IWorldFeatureGenerator;

import java.util.ArrayList;

/**
 * @author topisani
 */
public class LakeFeature extends TerrainFeature {

    public LakeFeature() {
        super(Mods.RTG, new ResourceLocation("rtg", "lake"));
    }

    @Override
    public TerrainBase initTerrain() {
        return null;
    }

    @Override
    public ArrayList<DecoBase> getDecos() {
        return null;
    }

    @Override
    public IWorldFeatureGenerator getGenerator() {
        return null;
    }

    @Override
    public IWorldFeatureGenerator initGenerator() {
        return null;
    }
}
