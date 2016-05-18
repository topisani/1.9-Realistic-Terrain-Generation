package teamrtg.rtg.api.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import teamrtg.rtg.api.mods.Mods;
import teamrtg.rtg.api.tools.feature.WorldGenPond;
import teamrtg.rtg.api.util.math.MathUtils;
import teamrtg.rtg.api.util.math.RandomUtil;
import teamrtg.rtg.api.util.noise.SimplexOctave;
import teamrtg.rtg.api.world.RTGWorld;
import teamrtg.rtg.api.world.biome.RTGBiomeBase;

import java.util.Random;

/**
 * @author topisani
 */
public class RTGBiomeGenerator implements IWorldFeatureGenerator {
    private static float actualRiverProportion = 300f / 1600f;
    public final RTGBiomeBase realistic;

    public RTGBiomeGenerator(RTGBiomeBase realistic) {
        this.realistic = realistic;
    }

    public void populatePreDecorate(IChunkGenerator iChunkGenerator, World worldObj, Random rand, int chunkX, int chunkZ, boolean flag) {
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        boolean gen = true;

        gen = TerrainGen.populate(iChunkGenerator, worldObj, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.LAKE);

        // Underground WATER lakes.
        if (Mods.RTG.config.ENABLE_UNDERGROUND_WATER_LAKES.get()) {

            if (gen && (Mods.RTG.config.UNDERGROUND_WATER_LAKE_CHANCE.get() > 0)) {

                int i2 = worldX + rand.nextInt(16) + 8;
                int l4 = RandomUtil.getRandomInt(rand, 1, 50);
                int i8 = worldZ + rand.nextInt(16) + 8;

                if (rand.nextInt(Mods.RTG.config.UNDERGROUND_WATER_LAKE_CHANCE.get()) == 0 && (RandomUtil.getRandomInt(rand, 1, Mods.RTG.config.UNDERGROUND_WATER_LAKE_CHANCE.get()) == 1)) {

                    (new WorldGenLakes(Blocks.WATER)).generate(worldObj, rand, new BlockPos(new BlockPos(i2, l4, i8)));
                }
            }
        }

        // Surface WATER lakes.
        if (Mods.RTG.config.ENABLE_SURFACE_WATER_LAKES.get()) {

            if (gen && (this.realistic.getConfig().WATER_POND_CHANCE.get() > 0)) {

                int i2 = worldX + rand.nextInt(16) + 8;
                int i8 = worldZ + rand.nextInt(16) + 8;
                int l4 = worldObj.getHeight(new BlockPos(i2, 0, i8)).getY();

                //Surface lakes.
                if (rand.nextInt(this.realistic.getConfig().WATER_POND_CHANCE.get()) == 0 && (RandomUtil.getRandomInt(rand, 1, this.realistic.getConfig().WATER_POND_CHANCE.get()) == 1)) {

                    if (l4 > 63) {

                        (new WorldGenPond(Blocks.WATER)).generate(worldObj, rand, i2, l4, i8);
                    }
                }
            }
        }

        gen = TerrainGen.populate(iChunkGenerator, worldObj, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.LAVA);

        // Underground lava lakes.
        if (Mods.RTG.config.ENABLE_UNDERGROUND_LAVA_LAKES.get()) {

            if (gen && (Mods.RTG.config.UNDERGROUND_LAVA_LAKE_CHANCE.get() > 0)) {

                int i2 = worldX + rand.nextInt(16) + 8;
                int l4 = RandomUtil.getRandomInt(rand, 1, 50);
                int i8 = worldZ + rand.nextInt(16) + 8;

                if (rand.nextInt(Mods.RTG.config.UNDERGROUND_LAVA_LAKE_CHANCE.get()) == 0 && (RandomUtil.getRandomInt(rand, 1, Mods.RTG.config.UNDERGROUND_LAVA_LAKE_CHANCE.get()) == 1)) {

                    (new WorldGenLakes(Blocks.LAVA)).generate(worldObj, rand, new BlockPos(i2, l4, i8));
                }
            }
        }

        // Surface lava lakes.
        if (Mods.RTG.config.ENABLE_SURFACE_LAVA_LAKES.get()) {

            if (gen && (this.realistic.getConfig().LAVA_POND_CHANCE.get() > 0)) {

                int i2 = worldX + rand.nextInt(16) + 8;
                int i8 = worldZ + rand.nextInt(16) + 8;
                int l4 = worldObj.getHeight(new BlockPos(i2, 0, i8)).getY();

                //Surface lakes.
                if (rand.nextInt(this.realistic.getConfig().LAVA_POND_CHANCE.get()) == 0 && (RandomUtil.getRandomInt(rand, 1, this.realistic.getConfig().LAVA_POND_CHANCE.get()) == 1)) {

                    if (l4 > 63) {

                        (new WorldGenPond(Blocks.LAVA)).generate(worldObj, rand, i2, l4, i8);
                    }
                }
            }
        }

        if (Mods.RTG.config.GENERATE_DUNGEONS.get()) {

            gen = TerrainGen.populate(iChunkGenerator, worldObj, rand, chunkX, chunkZ, flag, PopulateChunkEvent.Populate.EventType.DUNGEON);
            for (int k1 = 0; k1 < 8 && gen; k1++) {
                int j5 = worldX + rand.nextInt(16) + 8;
                int k8 = rand.nextInt(128);
                int j11 = worldZ + rand.nextInt(16) + 8;

                (new WorldGenDungeons()).generate(worldObj, rand, new BlockPos(j5, k8, j11));
            }
        }
    }

    /**
     * This method should be called if both of the following conditions are true:
     * <p/>
     * 1) You are manually decorating a biome by overrding rDecorate().
     * 2) You are NOT calling decorateBaseBiome() within rDecorate().
     */
    public void generateOres(RTGWorld rtgWorld, BlockPos blockPos, float strength, float river) {
        BiomeGenBase seedBiome = this.realistic.getBiome();
        Random rand = rtgWorld.rand;

        if (seedBiome.theBiomeDecorator.chunkProviderSettings == null) {

            String s = rtgWorld.world.getWorldInfo().getGeneratorOptions();

            if (s != null) {
                seedBiome.theBiomeDecorator.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory(s).build();
            } else {
                seedBiome.theBiomeDecorator.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory("").build();
            }
        }
        if (Mods.RTG.config.GENERATE_ORES.get()) {
            seedBiome.theBiomeDecorator.dirtGen = new WorldGenMinable(Blocks.DIRT.getDefaultState(), seedBiome.theBiomeDecorator.chunkProviderSettings.dirtSize);
            seedBiome.theBiomeDecorator.gravelGen = new WorldGenMinable(Blocks.GRAVEL.getDefaultState(), seedBiome.theBiomeDecorator.chunkProviderSettings.gravelSize);
            seedBiome.theBiomeDecorator.graniteGen = new WorldGenMinable(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE), seedBiome.theBiomeDecorator.chunkProviderSettings.graniteSize);
            seedBiome.theBiomeDecorator.dioriteGen = new WorldGenMinable(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE), seedBiome.theBiomeDecorator.chunkProviderSettings.dioriteSize);
            seedBiome.theBiomeDecorator.andesiteGen = new WorldGenMinable(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE), seedBiome.theBiomeDecorator.chunkProviderSettings.andesiteSize);
            seedBiome.theBiomeDecorator.coalGen = new WorldGenMinable(Blocks.COAL_ORE.getDefaultState(), seedBiome.theBiomeDecorator.chunkProviderSettings.coalSize);
            seedBiome.theBiomeDecorator.ironGen = new WorldGenMinable(Blocks.IRON_ORE.getDefaultState(), seedBiome.theBiomeDecorator.chunkProviderSettings.ironSize);
            seedBiome.theBiomeDecorator.goldGen = new WorldGenMinable(Blocks.GOLD_ORE.getDefaultState(), seedBiome.theBiomeDecorator.chunkProviderSettings.goldSize);
            seedBiome.theBiomeDecorator.redstoneGen = new WorldGenMinable(Blocks.REDSTONE_ORE.getDefaultState(), seedBiome.theBiomeDecorator.chunkProviderSettings.redstoneSize);
            seedBiome.theBiomeDecorator.diamondGen = new WorldGenMinable(Blocks.DIAMOND_ORE.getDefaultState(), seedBiome.theBiomeDecorator.chunkProviderSettings.diamondSize);
            seedBiome.theBiomeDecorator.lapisGen = new WorldGenMinable(Blocks.LAPIS_ORE.getDefaultState(), seedBiome.theBiomeDecorator.chunkProviderSettings.lapisSize);

            net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS.post(new net.minecraftforge.event.terraingen.OreGenEvent.Pre(rtgWorld.world, rand, blockPos));
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.dirtGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIRT))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.dirtCount, seedBiome.theBiomeDecorator.dirtGen, seedBiome.theBiomeDecorator.chunkProviderSettings.dirtMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.dirtMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.gravelGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GRAVEL))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.gravelCount, seedBiome.theBiomeDecorator.gravelGen, seedBiome.theBiomeDecorator.chunkProviderSettings.gravelMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.gravelMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.dioriteGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIORITE))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.dioriteCount, seedBiome.theBiomeDecorator.dioriteGen, seedBiome.theBiomeDecorator.chunkProviderSettings.dioriteMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.dioriteMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.graniteGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GRANITE))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.graniteCount, seedBiome.theBiomeDecorator.graniteGen, seedBiome.theBiomeDecorator.chunkProviderSettings.graniteMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.graniteMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.andesiteGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.ANDESITE))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.andesiteCount, seedBiome.theBiomeDecorator.andesiteGen, seedBiome.theBiomeDecorator.chunkProviderSettings.andesiteMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.andesiteMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.coalGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.COAL))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.coalCount, seedBiome.theBiomeDecorator.coalGen, seedBiome.theBiomeDecorator.chunkProviderSettings.coalMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.coalMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.ironGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.IRON))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.ironCount, seedBiome.theBiomeDecorator.ironGen, seedBiome.theBiomeDecorator.chunkProviderSettings.ironMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.ironMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.goldGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.GOLD))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.goldCount, seedBiome.theBiomeDecorator.goldGen, seedBiome.theBiomeDecorator.chunkProviderSettings.goldMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.goldMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.redstoneGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.REDSTONE))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.redstoneCount, seedBiome.theBiomeDecorator.redstoneGen, seedBiome.theBiomeDecorator.chunkProviderSettings.redstoneMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.redstoneMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.diamondGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.DIAMOND))
                oreGenHelper1(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.diamondCount, seedBiome.theBiomeDecorator.diamondGen, seedBiome.theBiomeDecorator.chunkProviderSettings.diamondMinHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.diamondMaxHeight);
            if (net.minecraftforge.event.terraingen.TerrainGen.generateOre(rtgWorld.world, rand, seedBiome.theBiomeDecorator.lapisGen, blockPos, net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.LAPIS))
                oreGenHelper2(rtgWorld.world, rand, blockPos, seedBiome.theBiomeDecorator.chunkProviderSettings.lapisCount, seedBiome.theBiomeDecorator.lapisGen, seedBiome.theBiomeDecorator.chunkProviderSettings.lapisCenterHeight, seedBiome.theBiomeDecorator.chunkProviderSettings.lapisSpread);

            net.minecraftforge.common.MinecraftForge.ORE_GEN_BUS.post(new net.minecraftforge.event.terraingen.OreGenEvent.Post(rtgWorld.world, rand, blockPos));
        }
    }

    // lake calculations

    /**
     * Standard ore generation helper. Generates most ores.
     * @see net.minecraft.world.biome.BiomeDecorator
     */
    private void oreGenHelper1(World worldObj, Random rand, BlockPos blockPos, int blockCount, WorldGenerator generator, int minHeight, int maxHeight) {
        if (maxHeight < minHeight) {
            int i = minHeight;
            minHeight = maxHeight;
            maxHeight = i;
        } else if (maxHeight == minHeight) {
            if (minHeight < 255) {
                ++maxHeight;
            } else {
                --minHeight;
            }
        }

        for (int j = 0; j < blockCount; ++j) {
            BlockPos blockpos = blockPos.add(rand.nextInt(16), rand.nextInt(maxHeight - minHeight) + minHeight, rand.nextInt(16));
            generator.generate(worldObj, rand, blockpos);
        }
    }

    /**
     * Standard ore generation helper. Generates Lapis Lazuli.
     * @see net.minecraft.world.biome.BiomeDecorator
     */
    private void oreGenHelper2(World worldObj, Random rand, BlockPos blockPos, int blockCount, WorldGenerator generator, int centerHeight, int spread) {
        for (int i = 0; i < blockCount; ++i) {
            BlockPos blockpos = blockPos.add(rand.nextInt(16), rand.nextInt(spread) + rand.nextInt(spread) + centerHeight - spread, rand.nextInt(16));
            generator.generate(worldObj, rand, blockpos);
        }
    }

    /**
     * When manually decorating biomes by overriding rDecorate(), sometimes you want the biome
     * to partially decorate itself. That's what this method does... it calls the biome's decorate() method.
     */
    public void decorateBaseBiome(RTGWorld rtgWorld, int chunkX, int chunkY, float strength, float river) {
        if (strength > 0.3f) {
            this.realistic.getBiome().decorate(rtgWorld.world, rtgWorld.rand, new BlockPos(chunkX, 0, chunkY));
        } else {
            generateOres(rtgWorld, new BlockPos(chunkX, 0, chunkY), strength, river);
        }
    }

    public float terrainHeight(RTGWorld rtgWorld, int x, int y, float border, float river) {
        // we now have both lakes and rivers lowering land
        if (realistic.noWaterFeatures) {
            float borderForRiver = border * 2;
            if (borderForRiver > 1f) borderForRiver = 1;
            river = 1f - (1f - borderForRiver) * (1f - river);
            return realistic.getTerrain().generateNoise(rtgWorld, x, y, border, river);
        }
        float lakeStrength = lakePressure(rtgWorld, x, y, border);
        float lakeFlattening = (float) lakeFlattening(lakeStrength, realistic.lakeShoreLevel, realistic.lakeDepressionLevel);
        // we add some flattening to the rivers. The lakes are pre-flattened.
        float riverFlattening = river * 1.25f - 0.25f;
        if (riverFlattening < 0) riverFlattening = 0;
        if ((river < 1) && (lakeFlattening < 1)) {
            riverFlattening = (float) ((1f - riverFlattening) / riverFlattening + (1f - lakeFlattening) / lakeFlattening);
            riverFlattening = (1f / (riverFlattening + 1f));
        } else {
            if (lakeFlattening < riverFlattening) riverFlattening = (float) lakeFlattening;
        }
        // the lakes have to have a little less flattening to avoid the rocky edges
        lakeFlattening = lakeFlattening(lakeStrength, realistic.lakeWaterLevel, realistic.lakeDepressionLevel);

        if ((river < 1) && (lakeFlattening < 1)) {
            river = (float) ((1f - river) / river + (1f - lakeFlattening) / lakeFlattening);
            river = (1f / (river + 1f));
        } else {
            if (lakeFlattening < river) river = (float) lakeFlattening;
        }
        // flatten getTerrain() to set up for the water features
        float terrainNoise = realistic.getTerrain().generateNoise(rtgWorld, x, y, border, riverFlattening);
        // place water features
        return this.erodedNoise(rtgWorld, x, y, river, border, terrainNoise, lakeFlattening);
    }

    public float lakePressure(RTGWorld rtgWorld, int x, int y, float border) {
        if (realistic.noLakes) return 1f;
        SimplexOctave.Disk jitter = new SimplexOctave.Disk();
        rtgWorld.simplex.riverJitter().evaluateNoise((float) x / 240.0, (float) y / 240.0, jitter);
        double pX = x + jitter.deltax() * realistic.largeBendSize;
        double pY = y + jitter.deltay() * realistic.largeBendSize;
        rtgWorld.simplex.mountain().evaluateNoise((float) x / 80.0, (float) y / 80.0, jitter);
        pX += jitter.deltax() * realistic.mediumBendSize;
        pY += jitter.deltay() * realistic.mediumBendSize;
        rtgWorld.simplex.octave(4).evaluateNoise((float) x / 30.0, (float) y / 30.0, jitter);
        pX += jitter.deltax() * realistic.smallBendSize;
        pY += jitter.deltay() * realistic.smallBendSize;
        //double results =simplexCell.river().noise(pX / lakeInterval, pY / lakeInterval,1.0);
        double[] lakeResults = rtgWorld.cell.river().eval((float) pX / realistic.lakeInterval, (float) pY / realistic.lakeInterval);
        float results = 1f - (float) ((lakeResults[1] - lakeResults[0]) / lakeResults[1]);
        if (results > 1.01) throw new RuntimeException("" + lakeResults[0] + " , " + lakeResults[1]);
        if (results < -.01) throw new RuntimeException("" + lakeResults[0] + " , " + lakeResults[1]);
        //return simplexCell.river().noise((float)x/ lakeInterval, (float)y/ lakeInterval,1.0);
        return results;
    }

    public float lakeFlattening(float pressure, float bottomLevel, float topLevel) {
        // this number indicates a multiplier to height
        if (pressure > topLevel) return 1;
        if (pressure < bottomLevel) return 0;
        return (float) Math.pow((pressure - bottomLevel) / (topLevel - bottomLevel), 1.0);
    }

    public float erodedNoise(RTGWorld rtgWorld, int x, int y, float river, float border, float biomeHeight, double lakeFlattening) {

        float r = 1f;

        // put a flat spot in the middle of the river
        float riverFlattening = river; // moved the flattening to terrain stage
        if (riverFlattening < 0) riverFlattening = 0;

        r = riverFlattening / actualRiverProportion;
        if ((r < 1f && biomeHeight > 57f)) {
            return (biomeHeight * (r))
                + ((57f + rtgWorld.simplex.noise2(x / 12f, y / 12f) * 2f + rtgWorld.simplex.noise2(x / 8f, y / 8f) * 1.5f) * (1f - r));
        } else {
            return biomeHeight;
        }
    }

    @Override
    public RTGBiomeBase get() {
        return this.realistic;
    }

    public void paintSurface(ChunkPrimer primer, int bx, int bz, int depth, float[] noise, float river, RTGWorld rtgWorld) {
        for (int by = 0; by < 256; by++) {
            Block b = primer.getBlockState(MathUtils.globalToLocal(bx), by, MathUtils.globalToLocal(bz)).getBlock();
            if (b == Blocks.AIR) {
                depth = -1;
            } else if (b == Blocks.STONE) {
                depth++;
                if (Mods.RTG.config.ENABLE_RTG_SURFACES.get() && this.realistic.getConfig().USE_RTG_SURFACES.get()) {
                    this.realistic.getSurface().paintWithSubparts(primer, bx, by, bz, depth, noise, river, rtgWorld);
                } else {
                    this.realistic.PARTS.surfaceGeneric().paintWithSubparts(primer, bx, by, bz, depth, noise, river, rtgWorld);
                }
            }
        }
    }

}
