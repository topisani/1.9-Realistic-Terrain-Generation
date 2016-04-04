package rtg.world.biome.realistic.vanilla;

import java.util.Random;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.util.noise.CellNoise;
import rtg.util.noise.OpenSimplexNoise;
import rtg.world.biome.deco.DecoBoulder;
import rtg.world.biome.deco.DecoCactus;
import rtg.world.biome.deco.DecoDeadBush;
import rtg.world.biome.deco.DecoGrassDoubleTallgrass;
import rtg.world.biome.deco.DecoReed;
import rtg.world.biome.deco.DecoShrub;
import rtg.world.biome.deco.DecoTree;
import rtg.world.biome.deco.DecoTree.TreeCondition;
import rtg.world.biome.deco.DecoTree.TreeType;
import rtg.world.biome.realistic.RealisticBiomeBase;
import rtg.world.gen.surface.SurfaceBase;
import rtg.world.gen.surface.SurfaceRiverOasis;
import rtg.world.gen.surface.vanilla.SurfaceVanillaMesaBryce;
import rtg.world.gen.terrain.TerrainBase;

public class RealisticBiomeVanillaMesaBryce extends RealisticBiomeVanillaBase {
    public static BiomeGenBase standardBiome = Biomes.mesa;
    public static BiomeGenBase mutationBiome = BiomeGenBase.getBiome(RealisticBiomeBase.getIdForBiome(standardBiome) + MUTATION_ADDEND);

    public RealisticBiomeVanillaMesaBryce() {

        super(
                mutationBiome,
                Biomes.river
        );

        initProperties();
        initDecos();
    }

    @Override
        protected SurfaceBase initSurface() {
        return new SurfaceVanillaMesaBryce(this, 0);
    }

    @Override
    protected TerrainBase initTerrain() {
        return new TerrainBase() {
            @Override
            public float generateNoise(OpenSimplexNoise simplex, CellNoise cell, int x, int y, float border, float river) {
                return terrainBryce(x, y, simplex, river, 20f, border);
            }
        };
    }

    @Override
    public void rReplace(ChunkPrimer primer, int i, int j, int x, int y, int depth, World world, Random rand,
                         OpenSimplexNoise simplex, CellNoise cell, float[] noise, float river, BiomeGenBase[] base) {

        this.getSurface().paintTerrain(primer, i, j, x, y, depth, world, rand, simplex, cell, noise, river, base);

        SurfaceBase riverSurface = new SurfaceRiverOasis(this);
        riverSurface.paintTerrain(primer, i, j, x, y, depth, world, rand, simplex, cell, noise, river, base);
    }

    @Override
    protected void initProperties()
    {

    }

    @Override
    protected void initDecos()
    {
		DecoBoulder decoBoulder = new DecoBoulder();
		decoBoulder.boulderBlock = Blocks.cobblestone;
		decoBoulder.maxY = 83;
		this.addDeco(decoBoulder);
        
		DecoTree riverTrees = new DecoTree();
		riverTrees.checkRiver = true;
		riverTrees.minRiver = 0.86f;
		riverTrees.strengthNoiseFactorForLoops = false;
		riverTrees.strengthFactorForLoops = 10f;
		riverTrees.treeType = TreeType.SAVANNA_RIVER;
		riverTrees.treeCondition = TreeCondition.ALWAYS_GENERATE;
		riverTrees.maxY = 100;
		this.addDeco(riverTrees);
		
		DecoCactus decoRiverCactus = new DecoCactus();
		decoRiverCactus.checkRiver = true;
		decoRiverCactus.minRiver = 0.7f;
		decoRiverCactus.maxY = 80;
		decoRiverCactus.strengthFactor = 12f;
        this.addDeco(decoRiverCactus);
        
        DecoReed decoReed = new DecoReed();
        decoReed.checkRiver = true;
        decoReed.minRiver = 0.7f;
		decoReed.maxY = 68;
		decoReed.strengthFactor = 2f;
        this.addDeco(decoReed);
        
        DecoGrassDoubleTallgrass decoGrassDoubleTallgrass = new DecoGrassDoubleTallgrass();
        decoGrassDoubleTallgrass.checkRiver = true;
        decoGrassDoubleTallgrass.minRiver = 0.7f;
        decoGrassDoubleTallgrass.maxY = 128;
        decoGrassDoubleTallgrass.loops = 15;
        decoGrassDoubleTallgrass.doubleGrassChance = 3;
        this.addDeco(decoGrassDoubleTallgrass);

        DecoShrub decoShrub = new DecoShrub();
        decoShrub.loops = 3;
        decoShrub.maxY = 90;
        addDeco(decoShrub);
        
        DecoDeadBush decoDeadBush = new DecoDeadBush();
		decoDeadBush.maxY = 100;
		decoDeadBush.loops = 3;
        this.addDeco(decoDeadBush);
        
        DecoCactus decoCactus = new DecoCactus();
        decoCactus.soil = Blocks.sand.getStateFromMeta(1);
        decoCactus.loops = 18;
        decoCactus.maxY = 100;
        this.addDeco(decoCactus);
    }
}
