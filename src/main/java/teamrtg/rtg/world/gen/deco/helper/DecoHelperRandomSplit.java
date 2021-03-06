package teamrtg.rtg.world.gen.deco.helper;

import net.minecraft.world.World;
import teamrtg.rtg.util.noise.CellNoise;
import teamrtg.rtg.util.noise.OpenSimplexNoise;
import teamrtg.rtg.world.gen.RealisticBiomeGenerator;
import teamrtg.rtg.world.gen.deco.DecoBase;

import java.util.Random;

/**
 * This deco helper takes an array of deco objects and an array of chances and generates one accordingly.
 * @author WhichOnesPink
 */
public class DecoHelperRandomSplit extends DecoBase {

    public DecoBase[] decos;
    public int[] chances;

    public DecoHelperRandomSplit() {
        super();

        this.decos = new DecoBase[] {};
        this.chances = new int[] {};
    }

    @Override
    public void generate(RealisticBiomeGenerator biomeGenerator, World world, Random rand, int chunkX, int chunkY, OpenSimplexNoise simplex, CellNoise cell, float strength, float river) {
        if (this.allowed) {

            if (this.decos.length < 1 || this.chances.length < 1 || this.decos.length != this.chances.length) {
                throw new RuntimeException("DecoHelperRandomSplit is confused.");
            }

            for (int i = 0; i < this.decos.length; i++) {

                if (rand.nextInt(this.chances[i]) == 0) {

                    this.decos[i].generate(biomeGenerator, world, rand, chunkX, chunkY, simplex, cell, strength, river);
                }
            }
        }
    }
}
