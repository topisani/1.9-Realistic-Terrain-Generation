package teamrtg.rtg.api.tools.deco;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.terraingen.TerrainGen;
import teamrtg.rtg.api.tools.feature.WorldGenGrass;
import teamrtg.rtg.api.world.RTGWorld;
import teamrtg.rtg.api.world.biome.deco.DecoBase;
import teamrtg.rtg.api.world.gen.IWorldFeatureGenerator;

import java.util.Random;

import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.GRASS;

/**
 * @author WhichOnesPink
 */
public class DecoDoubleGrass extends DecoBase {

    public float strengthFactor;
    public int maxY;
    public int loops;

    public DecoDoubleGrass() {
        super();

        /**
         * Default values.
         * These can be overridden when configuring the Deco object in the realistic biome.
         */
        this.maxY = 255; // No height limit by default.
        this.strengthFactor = 0f; // The higher the value, the more there will be.
        this.loops = 1;

        this.addDecoTypes(DecoType.GRASS_DOUBLE);
    }

    @Override
    public void generate(RTGWorld rtgWorld, Random rand, int chunkX, int chunkY, float strength, float river, IWorldFeatureGenerator mapGenGenerator) {

        if (this.allowed) {

            if (TerrainGen.decorate(rtgWorld.world, rand, new BlockPos(chunkX, 0, chunkY), GRASS)) {

                this.loops = (this.strengthFactor > 0f) ? (int) (this.strengthFactor * strength) : this.loops;
                for (int i = 0; i < this.loops; i++) {
                    int intX = chunkX + rand.nextInt(16) + 8;
                    int intY = rand.nextInt(this.maxY);
                    int intZ = chunkY + rand.nextInt(16) + 8;

                    if (intY <= this.maxY) {
                        (new WorldGenGrass(Blocks.DOUBLE_PLANT, 2)).generate(rtgWorld.world, rand, intX, intY, intZ);
                    }
                }
            }
        }
    }
}
