package teamrtg.rtg.api.mods;

import teamrtg.rtg.api.RTGAPI;
import teamrtg.rtg.api.util.BiomeUtils;
import teamrtg.rtg.api.world.biome.RTGBiomeBase;

import java.util.ArrayList;

/**
 * Holds all of a mods biomes as public final fields
 * @author topisani
 */
public class ModBiomes {

    private ArrayList<RTGBiomeBase> biomes = new ArrayList<>();

    public void initBiomes() {}

    protected RTGBiomeBase addBiome(RTGBiomeBase biome) {
        biomes.add(biome);
        RTGAPI.RTG_BIOMES.register(biome.getID(), BiomeUtils.getLocForBiome(biome.getBiome()), biome);
        return biome;
    }

    public RTGBiomeBase[] getBiomes() {
        return biomes.toArray(new RTGBiomeBase[biomes.size()]);
    }
}
