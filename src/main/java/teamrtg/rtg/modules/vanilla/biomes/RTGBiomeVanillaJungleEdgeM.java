package teamrtg.rtg.modules.vanilla.biomes;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import teamrtg.rtg.api.config.BiomeConfig;
import teamrtg.rtg.api.tools.deco.DecoFallenTree;
import teamrtg.rtg.api.tools.deco.DecoFallenTree.LogCondition;
import teamrtg.rtg.api.util.BiomeUtils;
import teamrtg.rtg.api.world.RTGWorld;
import teamrtg.rtg.api.world.biome.TerrainBase;
import teamrtg.rtg.api.world.biome.deco.DecoBaseBiomeDecorations;
import teamrtg.rtg.api.world.biome.surface.part.CliffSelector;
import teamrtg.rtg.api.world.biome.surface.part.DepthSelector;
import teamrtg.rtg.api.world.biome.surface.part.SurfacePart;
import teamrtg.rtg.modules.vanilla.RTGBiomeVanillaBase;

public class RTGBiomeVanillaJungleEdgeM extends RTGBiomeVanillaBase {
    public static BiomeGenBase standardBiome = Biomes.JUNGLE_EDGE;
    public static BiomeGenBase mutationBiome = BiomeGenBase.getBiome(BiomeUtils.getId(standardBiome) + MUTATION_ADDEND);

    public RTGBiomeVanillaJungleEdgeM() {

        super(
                mutationBiome,
            Biomes.RIVER
        );
        this.noLakes = true;
    }

    @Override
    public TerrainBase initTerrain() {
        return new TerrainBase() {
            @Override
            public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
                return terrainGrasslandMountains(x, y, rtgWorld.simplex, rtgWorld.cell, river, 4f, 80f, 68f);
            }
        };
    }

    @Override
    public SurfacePart initSurface() {
        SurfacePart surface = new SurfacePart();
        surface.add(new CliffSelector(1.4f)
            .add(new DepthSelector(0, 1)
                .add(PARTS.STONE_OR_COBBLE)))
            .add(PARTS.STONE);
        surface.add(PARTS.surfaceGeneric());
        return surface;
    }

    @Override
    public void initDecos() {
        DecoBaseBiomeDecorations decoBaseBiomeDecorations = new DecoBaseBiomeDecorations();
        this.addDeco(decoBaseBiomeDecorations);

        DecoFallenTree decoFallenTree = new DecoFallenTree();
        decoFallenTree.loops = 1;
        decoFallenTree.distribution.noiseDivisor = 100f;
        decoFallenTree.distribution.noiseFactor = 6f;
        decoFallenTree.distribution.noiseAddend = 0.8f;
        decoFallenTree.logCondition = LogCondition.NOISE_GREATER_AND_RANDOM_CHANCE;
        decoFallenTree.logConditionNoise = 0f;
        decoFallenTree.logConditionChance = 6;
        decoFallenTree.maxY = 110;
        decoFallenTree.logBlock = Blocks.LOG;
        decoFallenTree.logMeta = (byte) 3;
        decoFallenTree.leavesBlock = Blocks.LEAVES;
        decoFallenTree.leavesMeta = (byte) -1;
        decoFallenTree.minSize = 3;
        decoFallenTree.maxSize = 6;
        this.addDeco(decoFallenTree);
    }

    @Override
    public void initConfig() {
        this.config.SCATTERED_FEATURE.setDefault(BiomeConfig.FeatureType.JUNGLE_TEMPLE.name());
    }
}
