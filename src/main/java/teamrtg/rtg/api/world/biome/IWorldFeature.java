package teamrtg.rtg.api.world.biome;

import teamrtg.rtg.api.config.BiomeConfig;
import teamrtg.rtg.api.mods.RTGSupport;
import teamrtg.rtg.api.world.gen.IWorldFeatureGenerator;

/**
 * Collective wrapper for Biomes and TerrainFeatures
 * @author topisani
 */
public interface IWorldFeature extends IHasDecos, IHasSurface, IHasTerrain {

    void initConfig();

    BiomeConfig getConfig();

    RTGSupport getMod();

    String getName();

    IWorldFeatureGenerator getGenerator();

    IWorldFeatureGenerator initGenerator();
}
