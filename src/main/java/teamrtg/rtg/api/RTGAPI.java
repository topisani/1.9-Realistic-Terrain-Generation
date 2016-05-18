package teamrtg.rtg.api;

import com.google.common.collect.Iterators;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import teamrtg.rtg.api.world.biome.IWorldFeature;
import teamrtg.rtg.api.world.biome.RTGBiomeBase;
import teamrtg.rtg.api.world.biome.TerrainFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * The core of the API
 * Currently mainly holds registries for biomes and terrain features
 * @author topisani
 */
public class RTGAPI {

    public static final RegistryNamespaced<ResourceLocation, TerrainFeature> TERRAIN_FEATURES = new RegistryNamespaced<>();
    public static final RegistryNamespaced<ResourceLocation, RTGBiomeBase> RTG_BIOMES = new RegistryNamespaced<>();

    public static final List<IWorldFeature> WORLD_FEATURES = new ArrayList<>();

    public static final void populateGeneratables() {
        Iterators.addAll(WORLD_FEATURES, RTGAPI.RTG_BIOMES.iterator());
        Iterators.addAll(WORLD_FEATURES, RTGAPI.TERRAIN_FEATURES.iterator());
    }
}
