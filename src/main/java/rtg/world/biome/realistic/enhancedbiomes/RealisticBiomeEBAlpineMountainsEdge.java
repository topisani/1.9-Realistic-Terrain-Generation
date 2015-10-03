package rtg.world.biome.realistic.enhancedbiomes;

import net.minecraft.world.biome.BiomeGenBase;
import rtg.config.ConfigEB;
import rtg.world.biome.BiomeBase;
import rtg.world.biome.BiomeGenManager;
import rtg.world.gen.surface.enhancedbiomes.SurfaceEBAlpineMountainsEdge;
import rtg.world.gen.terrain.enhancedbiomes.TerrainEBAlpineMountainsEdge;

public class RealisticBiomeEBAlpineMountainsEdge extends RealisticBiomeEBBase
{	
	public RealisticBiomeEBAlpineMountainsEdge(BiomeGenBase ebBiome)
	{
		super(
			ebBiome, BiomeBase.climatizedBiome(BiomeGenBase.river, BiomeBase.Climate.COLD),
			new TerrainEBAlpineMountainsEdge(230f, 120f, 50f),
			new SurfaceEBAlpineMountainsEdge(ebBiome.topBlock, ebBiome.fillerBlock, false, null, 0.45f)
		);
		
		this.setRealisticBiomeName("EB Alpine Mountains Edge");
		BiomeGenManager.addFrozenBiome(this, ConfigEB.weightEBAlpineMountainsEdge);
	}
}