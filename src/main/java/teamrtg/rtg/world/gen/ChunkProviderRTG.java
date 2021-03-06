package teamrtg.rtg.world.gen;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import teamrtg.rtg.api.biome.RealisticBiomeBase;
import teamrtg.rtg.api.mods.Mods;
import teamrtg.rtg.api.util.BiomeUtils;
import teamrtg.rtg.util.PlaneLocation;
import teamrtg.rtg.util.math.CanyonColour;
import teamrtg.rtg.util.math.MathUtils;
import teamrtg.rtg.util.noise.CellNoise;
import teamrtg.rtg.util.noise.OpenSimplexNoise;
import teamrtg.rtg.util.noise.SimplexCellularNoise;
import teamrtg.rtg.util.noise.SimplexOctave;
import teamrtg.rtg.world.biome.BiomeAnalyzer;
import teamrtg.rtg.world.biome.BiomeProviderRTG;
import teamrtg.rtg.world.biome.fake.RealisticBiomeFaker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;

/**
 * Note from the ChunkProviderRTG-gods:
 * <p>
 * Any poor soul who trespass against us,
 * whether it be beast or man,
 * will suffer the bite or be stung dead on sight
 * by those who inhabit this land.
 * For theirs is the power and this is their kingdom,
 * as sure as the sun does burn.
 * So enter this path, but heed these four words:
 * You shall never return
 */
public class ChunkProviderRTG implements IChunkGenerator {
    /**
     * Declare variables.
     */

    private static final int centerLocationIndex = 312;// this is x=8, y=8 with the calcs below
    private static final int sampleSize = 8;
    public final RealisticBiomeFaker biomeFaker;
    public final Random rand;
    public final Random mapRand;
    public final World world;
    public final OpenSimplexNoise simplex;
    public final CellNoise cell;
    public final SimplexOctave.Disk surfaceJitter = new SimplexOctave.Disk();
    private final MapGenBase caveGenerator;
    private final MapGenBase ravineGenerator;
    private final MapGenStronghold strongholdGenerator;
    private final MapGenMineshaft mineshaftGenerator;
    private final MapGenVillage villageGenerator;
    private final MapGenScatteredFeature scatteredFeatureGenerator;
    private final StructureOceanMonument oceanMonumentGenerator;
    private final boolean mapFeaturesEnabled;
    private final int worldHeight;
    private final int sampleArraySize;
    private final int parabolicSize;
    private final int parabolicArraySize;
    private final float[] parabolicField;
    private final BiomeAnalyzer analyzer;
    private final IBlockState bedrockBlock = Mods.RTG.config.BEDROCK_BLOCK.get();
    private BiomeProviderRTG bprv;
    private RealisticBiomeBase[] biomesForGeneration;
    private BiomeGenBase[] baseBiomesList;
    private int[] biomeData;
    private float parabolicFieldTotal;
    private float[][] hugeRender;
    private float[][] smallRender;
    private float[] testHeight;
    private float[] borderNoise;
    private long worldSeed;

    public ChunkProviderRTG(World worldIn, long l) {
        this.world = worldIn;
        bprv = (BiomeProviderRTG) this.world.getBiomeProvider();
        bprv.chunkProvider = this;
        worldHeight = this.world.provider.getActualHeight();
        rand = new Random(l);
        simplex = new OpenSimplexNoise(l);
        cell = new SimplexCellularNoise(l);

        mapRand = new Random(l);
        worldSeed = l;

        Map m = new HashMap();
        m.put("size", "0");
        m.put("distance", "24");

        mapFeaturesEnabled = worldIn.getWorldInfo().isMapFeaturesEnabled();

        biomeFaker = new RealisticBiomeFaker(this);
        Mods.initAllBiomes(this);
        biomeFaker.initFakeBiomes();
        analyzer = new BiomeAnalyzer();

        if (Mods.RTG.config.ENABLE_CAVE_MODIFICATIONS.get()) {
            caveGenerator = TerrainGen.getModdedMapGen(new MapGenCavesRTG(), CAVE);
        } else {
            caveGenerator = TerrainGen.getModdedMapGen(new MapGenCaves(), CAVE);
        }

        if (Mods.RTG.config.ENABLE_RAVINE_MODIFICATIONS.get()) {
            ravineGenerator = TerrainGen.getModdedMapGen(new MapGenRavineRTG(), RAVINE);
        } else {
            ravineGenerator = TerrainGen.getModdedMapGen(new MapGenRavine(), RAVINE);
        }

        villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(new MapGenVillage(m), VILLAGE);
        strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(new MapGenStronghold(), STRONGHOLD);
        mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(new MapGenMineshaft(), MINESHAFT);
        scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(new MapGenScatteredFeature(), SCATTERED_FEATURE);
        oceanMonumentGenerator = (StructureOceanMonument) TerrainGen.getModdedMapGen(new StructureOceanMonument(), OCEAN_MONUMENT);

        CanyonColour.init(l);

        sampleArraySize = sampleSize * 2 + 5;

        parabolicSize = sampleSize;
        parabolicArraySize = parabolicSize * 2 + 1;
        parabolicField = new float[parabolicArraySize * parabolicArraySize];
        for (int j = -parabolicSize; j <= parabolicSize; ++j) {
            for (int k = -parabolicSize; k <= parabolicSize; ++k) {
                float f = 0.445f / MathHelper.sqrt_float((float) (j * j + k * k) + 0.3F);
                parabolicField[j + parabolicSize + (k + parabolicSize) * parabolicArraySize] = f;
                parabolicFieldTotal += f;
            }
        }

        baseBiomesList = new BiomeGenBase[256];
        biomeData = new int[sampleArraySize * sampleArraySize];
        hugeRender = new float[81][256];
        smallRender = new float[625][256];
        testHeight = new float[256];
        borderNoise = new float[256];

        //aic = new AICWrapper();
        //isAICExtendingBiomeIdsLimit = aic.isAICExtendingBiomeIdsLimit();
    }

    /**
     * @see IChunkProvider
     * <p/>
     * Loads or generates the chunk at the chunk location specified.
     */
    public Chunk loadChunk(int par1, int par2) {
        return provideChunk(par1, par2);
    }

    @Override
    public Chunk provideChunk(int cx, int cz) {
        rand.setSeed((long) cx * 0x4f9939f508L + (long) cz * 0x1ef1565bd5L);
        ChunkPrimer primer = new ChunkPrimer();
        BiomeGenBase[] baseBiomes = new BiomeGenBase[256];
        RealisticBiomeBase[] jitteredBiomes = new RealisticBiomeBase[256];

        float[] noise = bprv.getHeights(cx, cz);


        for (int k = 0; k < 256; k++) {
            baseBiomes[k] = RealisticBiomeBase.getBiome(bprv.getBiomes(cx, cz)[k]);
        }

        RealisticBiomeBase jittered, actual;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                simplex.evaluateNoise(cx * 16 + i, cz * 16 + j, surfaceJitter);
                int pX = (int) Math.round(cx * 16 + i + surfaceJitter.deltax() * Mods.RTG.config.SURFACE_BLEED_RADIUS.get());
                int pZ = (int) Math.round(cz * 16 + j + surfaceJitter.deltay() * Mods.RTG.config.SURFACE_BLEED_RADIUS.get());
                actual = bprv.getBiomeDataAt(cx * 16 + i, cz * 16 + j);
                jittered = bprv.getBiomeDataAt(pX, pZ);
                jitteredBiomes[i * 16 + j] = (actual.config.SURFACE_BLEED_IN.get() && jittered.config.SURFACE_BLEED_OUT.get()) ? jittered : actual;
            }
        }

        BiomeGenBase[] inverseBaseBiomes = new BiomeGenBase[256];
        for (int i = 0; i < 256; i++) {
            inverseBaseBiomes[i] = baseBiomes[MathUtils.XY_INVERTED[i]];
        }

        baseBiomes = inverseBaseBiomes;

        generateTerrain(primer, noise);

        replaceBlocksForBiome(cx, cz, primer, jitteredBiomes, baseBiomes, noise);

        caveGenerator.generate(world, cx, cz, primer);
        ravineGenerator.generate(world, cx, cz, primer);

        if (mapFeaturesEnabled) {

            if (Mods.RTG.config.GENERATE_MINESHAFTS.get()) {
                mineshaftGenerator.generate(this.world, cx, cz, primer);
            }

            if (Mods.RTG.config.GENERATE_STRONGHOLDS.get()) {
                strongholdGenerator.generate(this.world, cx, cz, primer);
            }

            if (Mods.RTG.config.GENERATE_VILLAGES.get()) {

                if (Mods.RTG.config.VILLAGE_CRASH_FIX.get()) {

                    try {
                        villageGenerator.generate(this.world, cx, cz, primer);
                    } catch (Exception e) {
                        // Do nothing.
                    }
                } else {
                    villageGenerator.generate(this.world, cx, cz, primer);
                }
            }

            if (Mods.RTG.config.GENERATE_SCATTERED_FEATURES.get()) {
                scatteredFeatureGenerator.generate(this.world, cx, cz, primer);
            }

            if (Mods.RTG.config.GENERATE_OCEAN_MONUMENTS.get()) {
                oceanMonumentGenerator.generate(this.world, cx, cz, primer);
            }
        }

        Chunk chunk = new Chunk(this.world, primer, cx, cz);
        // doJitter no longer needed as the biome array gets fixed
        byte[] abyte1 = chunk.getBiomeArray();
        for (int k = 0; k < abyte1.length; ++k) {
            // biomes are y-first and generateNoise x-first
            /*
            * This 2 line separation is needed, because otherwise, AIC's dynamic patching algorith detects biomes pattern here and patches this part following biomes logic.
            * Which causes game to crash.
            * I cannot do much on my part, so i have to do it here.
            * - Elix_x
            */
            byte b = (byte) BiomeUtils.getId(baseBiomes[k]);
            abyte1[k] = b;
        }
        chunk.setBiomeArray(abyte1);
        chunk.generateSkylightMap();
        return chunk;
    }

    private void generateTerrain(ChunkPrimer primer, float[] heights) {
        int h;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                h = (int) heights[i * 16 + j];

                for (int k = 0; k < 256; k++) {
                    if (k > h) {
                        if (k < 63) {
                            primer.setBlockState(i, k, j, Blocks.WATER.getDefaultState());
                        } else {
                            primer.setBlockState(i, k, j, Blocks.AIR.getDefaultState());
                        }
                    } else {
                        primer.setBlockState(i, k, j, Blocks.STONE.getDefaultState());
                    }
                }
            }
        }
    }

    private void replaceBlocksForBiome(int cx, int cz, ChunkPrimer primer, RealisticBiomeBase[] biomes, BiomeGenBase[] base, float[] n) {
        ChunkGeneratorEvent.ReplaceBiomeBlocks event = new ChunkGeneratorEvent.ReplaceBiomeBlocks(this, cx, cz, primer, world);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) return;
        int i, j, h, depth;
        float river;
        for (i = 0; i < 16; i++) {
            for (j = 0; j < 16; j++) {

                RealisticBiomeBase biome = biomes[i * 16 + j];

                if (biomeFaker.isFakeBiome(biome.getID())) {
                    biomeFaker.fakeSurface(cx * 16 + i, cz * 16 + j, primer, biome);
                } else {

                    river = -bprv.getRiverStrength(cx * 16 + i, cz * 16 + j);
                    depth = -1;

                    RealisticBiomeGenerator.forBiome(biome).paintSurface(primer, cx * 16 + i, cz * 16 + j, i, j, depth, world, rand, simplex, cell, n, river, base);
                }

                int rough;
                int flatBedrockLayers = Mods.RTG.config.FLAT_BEDROCK_LAYERS.get();
                flatBedrockLayers = flatBedrockLayers < 0 ? 0 : (flatBedrockLayers > 5 ? 5 : flatBedrockLayers);

                if (flatBedrockLayers > 0) {
                    for (int bl = 0; bl < flatBedrockLayers; bl++) {
                        primer.setBlockState(i, bl, j, bedrockBlock);
                    }
                } else {

                    primer.setBlockState(i, 0, j, bedrockBlock);

                    rough = rand.nextInt(2);
                    primer.setBlockState(i, rough, j, bedrockBlock);

                    rough = rand.nextInt(3);
                    primer.setBlockState(i, rough, j, bedrockBlock);

                    rough = rand.nextInt(4);
                    primer.setBlockState(i, rough, j, bedrockBlock);

                    rough = rand.nextInt(5);
                    primer.setBlockState(i, rough, j, bedrockBlock);
                }
            }
        }
    }

    /**
     * @see IChunkProvider
     * <p/>
     * Populates chunk with ores etc etc
     */
    @Override
    public void populate(int x, int z) {
        BlockFalling.fallInstantly = true;

        int worldX = x * 16;
        int worldZ = z * 16;
        RealisticBiomeBase biome = bprv.getBiomeDataAt(worldX + 16, worldZ + 16);
        this.rand.setSeed(this.world.getSeed());
        long i1 = this.rand.nextLong() / 2L * 2L + 1L;
        long j1 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long) x * i1 + (long) z * j1 ^ this.world.getSeed());
        boolean flag = false;
        ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(x, z);
        BlockPos worldCoords = new BlockPos(worldX, 0, worldZ);

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(this, world, rand, x, z, flag));

        if (mapFeaturesEnabled) {

            if (Mods.RTG.config.GENERATE_MINESHAFTS.get()) {
                mineshaftGenerator.generateStructure(world, rand, chunkCoords);
            }

            if (Mods.RTG.config.GENERATE_STRONGHOLDS.get()) {
                strongholdGenerator.generateStructure(world, rand, chunkCoords);
            }

            if (Mods.RTG.config.GENERATE_VILLAGES.get()) {

                if (Mods.RTG.config.VILLAGE_CRASH_FIX.get()) {

                    try {
                        flag = villageGenerator.generateStructure(world, rand, chunkCoords);
                    } catch (Exception e) {
                        flag = false;
                    }
                } else {

                    flag = villageGenerator.generateStructure(world, rand, chunkCoords);
                }
            }

            if (Mods.RTG.config.GENERATE_SCATTERED_FEATURES.get()) {
                scatteredFeatureGenerator.generateStructure(world, rand, chunkCoords);
            }

            if (Mods.RTG.config.GENERATE_OCEAN_MONUMENTS.get()) {
                oceanMonumentGenerator.generateStructure(world, rand, chunkCoords);
            }
        }

        RealisticBiomeGenerator.forBiome(biome.baseBiome).populatePreDecorate(this, world, rand, x, z, flag);

        /*
         * What is this doing? And why does it need to be done here? - Pink
         * Answer: building a frequency table of nearby biomes - Zeno.
         */

        final int adjust = 32;// seems off? but decorations aren't matching their chunks.
        for (int bx = -4; bx <= 4; bx++) {

            for (int by = -4; by <= 4; by++) {
                borderNoise[BiomeUtils.getId(bprv.getBiomeGenAt(worldX + adjust + bx * 4, worldZ + adjust + by * 4))] += 0.01234569f;
            }
        }

        /*
         * ########################################################################
         * # START DECORATE BIOME
         * ########################################################################
         */

        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, rand, worldCoords));

        //Initialise variables.
        float river = -bprv.getRiverStrength(worldX + 16, worldZ + 16);

        //Border noise. (Does this have to be done here? - Pink)
        RealisticBiomeBase realisticBiome;
        float snow = 0f;

        for (int bn = 0; bn < 256; bn++) {
            if (borderNoise[bn] > 0f) {
                if (borderNoise[bn] >= 1f) {
                    borderNoise[bn] = 1f;
                }
                realisticBiome = RealisticBiomeBase.getBiome(bn);

                /*
                 * When decorating the biome, we need to look at the biome configs to see if RTG is allowed to decorate it.
                 * If the biome configs don't allow it, then we try to let the base biome decorate itself.
                 * However, there are some mod biomes that crash when they try to decorate themselves,
                 * so that's what the try/catch is for. If it fails, then it falls back to RTG decoration.
                 * TODO: Is there a more efficient way to do this? - Pink
                 */
                if (Mods.RTG.config.ENABLE_RTG_BIOME_DECORATIONS.get() && realisticBiome.config.USE_RTG_DECORATIONS.get()) {
                    RealisticBiomeGenerator.forBiome(realisticBiome).decorate(this.world, this.rand, worldX, worldZ, simplex, cell, borderNoise[bn], river);
                } else {
                    try {
                        realisticBiome.baseBiome.decorate(this.world, rand, worldCoords);
                    } catch (Exception e) {
                        RealisticBiomeGenerator.forBiome(realisticBiome).decorate(this.world, this.rand, worldX, worldZ, simplex, cell, borderNoise[bn], river);
                    }
                }

                if (realisticBiome.baseBiome.getTemperature() < 0.15f) {
                    snow -= 0.6f * borderNoise[bn];
                } else {
                    snow += 0.6f * borderNoise[bn];
                }
                borderNoise[bn] = 0f;
            }
        }

        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, rand, worldCoords));

        /*
         * ########################################################################
         * # END DECORATE BIOME
         * ########################################################################
         */

        //Flowing WATER.
        if (rand.nextInt(100) == 0) {
            for (int l18 = 0; l18 < 50; l18++) {
                int l21 = worldX + rand.nextInt(16) + 8;
                int k23 = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
                int l24 = worldZ + rand.nextInt(16) + 8;

                (new WorldGenLiquids(Blocks.FLOWING_WATER)).generate(world, rand, new BlockPos(l21, k23, l24));
            }
        }

        //Flowing lava.
        if (rand.nextInt(100) == 0) {
            for (int i19 = 0; i19 < 20; i19++) {
                int i22 = worldX + rand.nextInt(16) + 8;
                int l23 = rand.nextInt(worldHeight / 2);
                int i25 = worldZ + rand.nextInt(16) + 8;
                (new WorldGenLiquids(Blocks.FLOWING_LAVA)).generate(world, rand, new BlockPos(i22, l23, i25));
            }
        }

        if (TerrainGen.populate(this, world, rand, x, z, flag, PopulateChunkEvent.Populate.EventType.ANIMALS)) {
            WorldEntitySpawner.performWorldGenSpawning(this.world, world.getBiomeGenForCoords(new BlockPos(worldX + 16, 0, worldZ + 16)), worldX + 8, worldZ + 8, 16, 16, this.rand);
        }

        if (TerrainGen.populate(this, world, rand, x, z, flag, PopulateChunkEvent.Populate.EventType.ICE)) {

            int k1, l1, i2;
            BlockPos.MutableBlockPos bp = new BlockPos.MutableBlockPos(0, 0, 0);
            for (k1 = 0; k1 < 16; ++k1) {

                for (l1 = 0; l1 < 16; ++l1) {

                    i2 = this.world.getPrecipitationHeight(bp.set(worldX + k1, 0, worldZ + l1)).getY();

                    if (this.world.canBlockFreezeNoWater(bp.set(k1 + worldX, i2 - 1, l1 + worldZ))) {
                        this.world.setBlockState(bp.set(k1 + worldX, i2 - 1, l1 + worldZ), Blocks.ICE.getDefaultState(), 2);
                    }

                    if (Mods.RTG.config.ENABLE_SNOW_LAYERS.get() && this.world.canSnowAt(bp.set(k1 + worldX, i2, l1 + worldZ), true)) {
                        this.world.setBlockState(bp.set(k1 + worldX, i2, l1 + worldZ), Blocks.SNOW_LAYER.getDefaultState(), 2);
                    }
                }
            }
        }

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(this, world, rand, x, z, flag));

        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    /**
     * @see IChunkProvider
     * <p/>
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    @Override
    public List getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        BiomeGenBase var5 = this.world.getBiomeGenForCoords(pos);
        if (this.mapFeaturesEnabled) {
            if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(pos)) {
                return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
            }

            if (creatureType == EnumCreatureType.MONSTER && Mods.RTG.config.GENERATE_OCEAN_MONUMENTS.get() && this.oceanMonumentGenerator.isPositionInStructure(this.world, pos)) {
                return this.oceanMonumentGenerator.getScatteredFeatureSpawnList();
            }
        }
        return var5 == null ? null : var5.getSpawnableList(creatureType);
    }

    /**
     * @see IChunkProvider
     */
    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        if (!Mods.RTG.config.GENERATE_STRONGHOLDS.get()) {
            return null;
        }

        return "Stronghold".equals(structureName) && this.strongholdGenerator != null ? this.strongholdGenerator.getClosestStrongholdPos(worldIn, position) : null;
    }

    /**
     * @see IChunkProvider
     */
    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

        if (mapFeaturesEnabled) {

            if (Mods.RTG.config.GENERATE_MINESHAFTS.get()) {
                mineshaftGenerator.generate(world, x, z, null);
            }

            if (Mods.RTG.config.GENERATE_STRONGHOLDS.get()) {
                strongholdGenerator.generate(world, x, z, null);
            }

            if (Mods.RTG.config.GENERATE_VILLAGES.get()) {

                if (Mods.RTG.config.VILLAGE_CRASH_FIX.get()) {

                    try {
                        villageGenerator.generate(this.world, x, z, null);
                    } catch (Exception e) {
                        // Do nothing.
                    }

                } else {
                    villageGenerator.generate(this.world, x, z, null);
                }
            }

            if (Mods.RTG.config.GENERATE_SCATTERED_FEATURES.get()) {
                scatteredFeatureGenerator.generate(this.world, x, z, null);
            }

            if (Mods.RTG.config.GENERATE_OCEAN_MONUMENTS.get()) {
                oceanMonumentGenerator.generate(this.world, x, z, null);
            }
        }
    }

    public void requestChunk(int cx, int cz) {
        float[] noise;
        RealisticBiomeBase[] biomes = new RealisticBiomeBase[256];
        int[] biomeIds = new int[256];
        int[] biomeData = new int[sampleArraySize * sampleArraySize];
        float[] riverVals = new float[256];

        int k;

        noise = getNewerNoise(bprv, cx * 16, cz * 16, biomes, biomeData, riverVals);

        //fill biomes array with biomeData
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                biomes[i * 16 + j] = RealisticBiomeBase.getBiome(BiomeUtils.getId(bprv.getPreRepair(cx + i, cz + j)));
            }
        }

        //fill with biomeData
        int[] biomeIndices = bprv.getBiomesGens(cx * 16, cz * 16, 16, 16);


        analyzer.newRepair(biomeIndices, biomes, biomeData, sampleSize, noise, riverVals);

        for (int i = 0; i < 256; i++) {
            biomeIds[i] = BiomeUtils.getId(biomes[i]);
        }

        PlaneLocation loc = new PlaneLocation.Invariant(cx, cz);
        bprv.biomes.put(loc, biomeIds);
        bprv.heights.put(loc, noise);
    }

    private float[] getNewerNoise(BiomeProviderRTG cmr, int x, int y, RealisticBiomeBase biomes[], int[] biomeData, float[] riverVals) {

        float[] testHeight;
        float[] biomesGeneratedInChunk;

        testHeight = new float[256];
        biomesGeneratedInChunk = new float[256];

        // get area biome map
        for (int i = -sampleSize; i < sampleSize + 5; i++) {
            for (int j = -sampleSize; j < sampleSize + 5; j++) {
                biomeData[(i + sampleSize) * sampleArraySize + (j + sampleSize)] = BiomeUtils.getId(cmr.getPreRepair(x + ((i * 8)), y + ((j * 8))));
            }
        }
        float river;
        float[] weightedBiomes = new float[256];

        int adjustment = 4;// this should actually vary with sampleSize
        // fill the old smallRender
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int locationIndex = ((int) (i + adjustment) * 25 + (j + adjustment));
                float totalWeight = 0;

                float limit = (float) Math.pow((56f * 56f), .7);
                // float limit = 56f;

                for (int mapX = 0; mapX < sampleArraySize; mapX++) {
                    for (int mapZ = 0; mapZ < sampleArraySize; mapZ++) {
                        float xDist = (i - chunkCoordinate(mapX));
                        float yDist = (j - chunkCoordinate(mapZ));
                        float distanceSquared = xDist * xDist + yDist * yDist;
                        //float distance = (float)Math.sqrt(distanceSquared);
                        float distance = (float) Math.pow(distanceSquared, .7);
                        float weight = 1f - distance / limit;
                        if (weight > 0) {
                            totalWeight += weight;
                            weightedBiomes[biomeData[mapX * sampleArraySize + mapZ]] += weight;
                        }
                    }
                }
                // normalize biome weights
                for (int biomeIndex = 0; biomeIndex < weightedBiomes.length; biomeIndex++) {
                    weightedBiomes[biomeIndex] /= totalWeight;
                }
                testHeight[i * 16 + j] = 0f;

                river = cmr.getRiverStrength(x + i, y + j);
                riverVals[i * 16 + j] = -river;
                float totalBorder = 0f;

                for (int k = 0; k < 256; k++) {

                    if (weightedBiomes[k] > 0f) {

                        if (locationIndex == centerLocationIndex) {
                            biomesGeneratedInChunk[k] = weightedBiomes[k];
                        }

                        totalBorder += weightedBiomes[k];
                        testHeight[i * 16 + j] += RealisticBiomeGenerator.forBiome(k).rNoise(simplex, cell, x + i, y + j, weightedBiomes[k], river + 1f) * weightedBiomes[k];
                        // 0 for the next column
                        weightedBiomes[k] = 0f;

                    }
                }
                if (totalBorder < .999 || totalBorder > 1.001) throw new RuntimeException("" + totalBorder);
            }
        }

        return testHeight;

    }

    private int chunkCoordinate(int biomeMapCoordinate) {
        return (biomeMapCoordinate - sampleSize) * 8;
    }
}
