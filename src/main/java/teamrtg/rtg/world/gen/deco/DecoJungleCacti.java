package teamrtg.rtg.world.gen.deco;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;
import teamrtg.rtg.util.noise.CellNoise;
import teamrtg.rtg.util.noise.OpenSimplexNoise;
import teamrtg.rtg.world.gen.RealisticBiomeGenerator;
import teamrtg.rtg.world.gen.feature.WorldGenJungleCacti;

import java.util.Random;

import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.CACTUS;

/**
 * @author WhichOnesPink
 */
public class DecoJungleCacti extends DecoBase {

    public float strengthFactor;
    public int maxY;
    public boolean sandOnly;
    public int extraHeight;
    public byte sandMeta;

    public DecoJungleCacti() {
        super();

        /**
         * Default values.
         * These can be overridden when configuring the Deco object in the realistic biome.
         */
        this.strengthFactor = 8f;
        this.maxY = 255; // No height limit by default.
        this.sandOnly = false;
        this.extraHeight = 7;
        this.sandMeta = (byte) 1;

        this.addDecoTypes(DecoType.CACTUS);
    }

    @Override
    public void generate(RealisticBiomeGenerator biomeGenerator, World world, Random rand, int chunkX, int chunkY, OpenSimplexNoise simplex, CellNoise cell, float strength, float river) {
        if (this.allowed) {

            if (TerrainGen.decorate(world, rand, new BlockPos(chunkX, 0, chunkY), CACTUS)) {

                for (int i = 0; i < this.strengthFactor * strength; i++) {
                    int intX = chunkX + rand.nextInt(16) + 8;
                    int intY = rand.nextInt(160);
                    int intZ = chunkY + rand.nextInt(16) + 8;

                    if (intY < this.maxY) {
                        (new WorldGenJungleCacti(this.sandOnly, rand.nextInt(this.extraHeight), this.sandMeta)).generate(world, rand, new BlockPos(intX, intY, intZ));
                    }
                }
            }
        }
    }
}
