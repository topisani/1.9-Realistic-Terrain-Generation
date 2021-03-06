package teamrtg.rtg.world.gen.deco;

import net.minecraft.world.World;
import teamrtg.rtg.api.biome.RealisticBiomeBase;
import teamrtg.rtg.util.noise.CellNoise;
import teamrtg.rtg.util.noise.OpenSimplexNoise;
import teamrtg.rtg.world.gen.RealisticBiomeGenerator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Documentation for the new biome system can be found here:
 * https://teamrtg.gitbooks.io/rtg-code-documentation/content/biome_decoration.html
 * @author WhichOnesPink
 */
public abstract class DecoBase {

    /**
     * If false, the deco won't get generated during chunk decoration.
     * Currently, the only deco that uses allow=false is the DecoBaseBiomeDecorations deco, and it only gets
     * set to false when we need to generate ores in biomes that don't let the base biome handle decoration at all.
     */
    public boolean allowed;
    public ArrayList<DecoType> decoTypes;
    public boolean checkRiver;
    public float minRiver; // Minimum river value required to generate.
    public float maxRiver; // Maximum river value required to generate.
    private String name;

    public DecoBase() {
        this.allowed = true;
        this.decoTypes = new ArrayList<DecoType>();
        this.checkRiver = false;
        this.minRiver = -2f;
        this.maxRiver = 2f;
        //TODO: add name to all decos so this can go
        this.name = getClass().getSimpleName();
    }

    /**
     * Name is a unique identified used for things like configs
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Performs pre-generation checks to determine if the deco is allowed to generate.
     * The parameters are virtually the same as the ones passed to the legacy rDecorate() method.
     * This method should NOT be overridden in the individual deco objects.
     * @param biome
     * @param world
     * @param rand
     * @param chunkX
     * @param chunkY
     * @param simplex
     * @param cell
     * @param strength
     * @param river
     */
    public final boolean preGenerate(RealisticBiomeBase biome, World world, Random rand, int chunkX, int chunkY, OpenSimplexNoise simplex, CellNoise cell, float strength, float river) {
        if (this.checkRiver) {

            if (river > this.maxRiver || river < this.minRiver) {
                return false;
            }
        }

        return true;
    }

    /**
     * Generates the decoration.
     * The parameters are virtually the same as the ones passed to the legacy rDecorate() method.
     * This method should be overridden in the individual deco objects.
     * @param biome
     * @param world
     * @param rand
     * @param chunkX
     * @param chunkY
     * @param simplex
     * @param cell
     * @param strength
     * @param river
     */
    public abstract void generate(RealisticBiomeGenerator biome, World world, Random rand, int chunkX, int chunkY, OpenSimplexNoise simplex, CellNoise cell, float strength, float river);

    /**
     * Adds one or more deco types.
     * @param decos
     */
    public void addDecoTypes(DecoType... decos) {
        for (int i = 0; i < decos.length; i++) {
            this.decoTypes.add(decos[i]);
        }
    }

    /**
     * Enum to classify the various decos.
     * @author WhichOnesPink
     */
    public enum DecoType {
        BASE_BIOME_DECORATION,
        BOULDER,
        CACTUS,
        DEAD_BUSH,
        DESERT_WELL,
        FALLEN_TREE,
        FERN,
        FERN_DOUBLE,
        FLOWER,
        GRASS,
        GRASS_DOUBLE,
        LILYPAD,
        MUSHROOM,
        PUMPKIN,
        REED,
        SHRUB,
        TREE,
        VINE
    }
}