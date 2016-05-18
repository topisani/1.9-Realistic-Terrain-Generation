package teamrtg.rtg.api.world.biome;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import teamrtg.rtg.api.config.BiomeConfig;
import teamrtg.rtg.api.mods.RTGSupport;
import teamrtg.rtg.api.world.biome.deco.DecoBase;
import teamrtg.rtg.api.world.biome.deco.DecoBaseBiomeDecorations;
import teamrtg.rtg.api.world.biome.surface.part.PresetParts;
import teamrtg.rtg.api.world.biome.surface.part.SurfacePart;

import java.util.ArrayList;

/**
 * @author topisani
 */
public abstract class TerrainFeature implements IWorldFeature {

    public final RTGSupport mod;
    public final ResourceLocation name;
    protected final BiomeConfig config;
    public PresetParts PARTS;
    public TerrainBase terrain;
    public SurfacePart surface;
    public ArrayList<DecoBase> decos = new ArrayList<>();

    public TerrainFeature(RTGSupport mod, ResourceLocation name) {
        this.mod = mod;
        this.name = name;

        this.config = new BiomeConfig(getMod().getID(), this.getName());
        init();
    }

    private void init() {
        initConfig();
        this.PARTS = new PresetParts(this);
        this.surface = initSurface();
        this.terrain = initTerrain();
        initDecos();
    }

    /**
     * This should set the defaults for all properties
     */
    public void initConfig() {}

    @Override
    public SurfacePart initSurface() {
        return new SurfacePart();
    }

    @Override
    public abstract TerrainBase initTerrain();

    @Override
    public void initDecos() {
        DecoBaseBiomeDecorations decoBaseBiomeDecorations = new DecoBaseBiomeDecorations();
        decoBaseBiomeDecorations.allowed = true;
        this.decos.add(decoBaseBiomeDecorations);
    }

    /**
     * Adds a deco object to the list of biome decos.
     * @param deco
     */
    @Override
    public void addDeco(DecoBase deco) {
        this.decos.add(deco);
        this.config.DECORATIONS.setOptions(ArrayUtils.add(this.config.DECORATIONS.getOptions(), deco.getName()));
        this.config.DECORATIONS.setDefault(ArrayUtils.add(this.config.DECORATIONS.getDefault(), deco.getName()));
    }

    @Override
    public TerrainBase getTerrain() {
        return this.terrain;
    }

    @Override
    public SurfacePart getSurface() {
        return this.surface;
    }

    @Override
    public BiomeConfig getConfig() {
        return config;
    }

    @Override
    public RTGSupport getMod() {
        return mod;
    }

    @Override
    public String getName() {
        return this.name.getResourcePath();
    }
}
