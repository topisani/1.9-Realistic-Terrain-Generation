package teamrtg.rtg.world.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;
import teamrtg.rtg.api.world.biome.RTGBiomeBase;
import teamrtg.rtg.api.mods.Mods;
import teamrtg.rtg.api.util.BiomeUtils;

import java.util.Random;

public class MapGenRavineRTG extends MapGenRavine {
    private static final String __OBFID = "CL_00000390";
    private float[] field_75046_d = new float[1024];
    private boolean enableRavines;
    private int ravineFrequency;

    @Override
    protected void addTunnel(long p_180707_1_, int p_180707_3_, int p_180707_4_, ChunkPrimer p_180707_5_, double p_180707_6_, double p_180707_8_, double p_180707_10_, float p_180707_12_, float p_180707_13_, float p_180707_14_, int p_180707_15_, int p_180707_16_, double p_180707_17_) {
        Random random = new Random(p_180707_1_);
        double d4 = (double) (p_180707_3_ * 16 + 8);
        double d5 = (double) (p_180707_4_ * 16 + 8);
        float f3 = 0.0F;
        float f4 = 0.0F;

        if (p_180707_16_ <= 0) {
            int j1 = this.range * 16 - 16;
            p_180707_16_ = j1 - random.nextInt(j1 / 4);
        }

        boolean flag1 = false;

        if (p_180707_15_ == -1) {
            p_180707_15_ = p_180707_16_ / 2;
            flag1 = true;
        }

        float f5 = 1.0F;

        for (int k1 = 0; k1 < 256; ++k1) {
            if (k1 == 0 || random.nextInt(3) == 0) {
                f5 = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
            }

            this.field_75046_d[k1] = f5 * f5;
        }

        for (; p_180707_15_ < p_180707_16_; ++p_180707_15_) {
            double d12 = 1.5D + (double) (MathHelper.sin((float) p_180707_15_ * (float) Math.PI / (float) p_180707_16_) * p_180707_12_ * 1.0F);
            double d6 = d12 * p_180707_17_;
            d12 *= (double) random.nextFloat() * 0.25D + 0.75D;
            d6 *= (double) random.nextFloat() * 0.25D + 0.75D;
            float f6 = MathHelper.cos(p_180707_14_);
            float f7 = MathHelper.sin(p_180707_14_);
            p_180707_6_ += (double) (MathHelper.cos(p_180707_13_) * f6);
            p_180707_8_ += (double) f7;
            p_180707_10_ += (double) (MathHelper.sin(p_180707_13_) * f6);
            p_180707_14_ *= 0.7F;
            p_180707_14_ += f4 * 0.05F;
            p_180707_13_ += f3 * 0.05F;
            f4 *= 0.8F;
            f3 *= 0.5F;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (flag1 || random.nextInt(4) != 0) {
                double d7 = p_180707_6_ - d4;
                double d8 = p_180707_10_ - d5;
                double d9 = (double) (p_180707_16_ - p_180707_15_);
                double d10 = (double) (p_180707_12_ + 2.0F + 16.0F);

                if (d7 * d7 + d8 * d8 - d9 * d9 > d10 * d10) {
                    return;
                }

                if (p_180707_6_ >= d4 - 16.0D - d12 * 2.0D && p_180707_10_ >= d5 - 16.0D - d12 * 2.0D && p_180707_6_ <= d4 + 16.0D + d12 * 2.0D && p_180707_10_ <= d5 + 16.0D + d12 * 2.0D) {
                    int i4 = MathHelper.floor_double(p_180707_6_ - d12) - p_180707_3_ * 16 - 1;
                    int l1 = MathHelper.floor_double(p_180707_6_ + d12) - p_180707_3_ * 16 + 1;
                    int j4 = MathHelper.floor_double(p_180707_8_ - d6) - 1;
                    int i2 = MathHelper.floor_double(p_180707_8_ + d6) + 1;
                    int k4 = MathHelper.floor_double(p_180707_10_ - d12) - p_180707_4_ * 16 - 1;
                    int j2 = MathHelper.floor_double(p_180707_10_ + d12) - p_180707_4_ * 16 + 1;

                    if (i4 < 0) {
                        i4 = 0;
                    }

                    if (l1 > 16) {
                        l1 = 16;
                    }

                    if (j4 < 1) {
                        j4 = 1;
                    }

                    if (i2 > 248) {
                        i2 = 248;
                    }

                    if (k4 < 0) {
                        k4 = 0;
                    }

                    if (j2 > 16) {
                        j2 = 16;
                    }

                    boolean flag2 = false;
                    int k2;
                    int j3;

                    for (k2 = i4; !flag2 && k2 < l1; ++k2) {
                        for (int l2 = k4; !flag2 && l2 < j2; ++l2) {
                            for (int i3 = i2 + 1; !flag2 && i3 >= j4 - 1; --i3) {
                                j3 = (k2 * 16 + l2) * 256 + i3;

                                if (i3 >= 0 && i3 < 256) {

                                    if (isOceanBlock(p_180707_5_, k2, i3, l2, p_180707_3_, p_180707_4_)) {
                                        flag2 = true;
                                    }

                                    if (i3 != j4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != k4 && l2 != j2 - 1) {
                                        i3 = j4;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag2) {
                        for (k2 = i4; k2 < l1; ++k2) {
                            double d13 = ((double) (k2 + p_180707_3_ * 16) + 0.5D - p_180707_6_) / d12;

                            for (j3 = k4; j3 < j2; ++j3) {
                                double d14 = ((double) (j3 + p_180707_4_ * 16) + 0.5D - p_180707_10_) / d12;
                                int k3 = (k2 * 16 + j3) * 256 + i2;
                                boolean flag = false;

                                if (d13 * d13 + d14 * d14 < 1.0D) {
                                    for (int l3 = i2 - 1; l3 >= j4; --l3) {
                                        double d11 = ((double) l3 + 0.5D - p_180707_8_) / d6;

                                        if ((d13 * d13 + d14 * d14) * (double) this.field_75046_d[l3] + d11 * d11 / 6.0D < 1.0D) {

                                            if (istopBlock(p_180707_5_, k2, l3, j3, p_180707_3_, p_180707_4_)) {
                                                flag = true;
                                            }

                                            digBlock(p_180707_5_, k2, l3, j3, p_180707_3_, p_180707_4_, flag);
                                        }

                                        --k3;
                                    }
                                }
                            }
                        }

                        if (flag1) {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {
        enableRavines = Mods.RTG.config.ENABLE_RAVINES.get();
        ravineFrequency = Mods.RTG.config.RAVINE_FREQUENCY.get();
        try {
            // If the user has set biome-specific settings, let's use those instead.
            BiomeGenBase biome = worldIn.getBiomeGenForCoords(new BlockPos(this.rand.nextInt(16) + chunkX * 16, 0, this.rand.nextInt(16) + chunkZ * 16));
            RTGBiomeBase realisticBiome = RTGBiomeBase.forBiome(BiomeUtils.getId(biome));
            ravineFrequency = (realisticBiome.getConfig().RAVINE_FREQUENCY.get() > -1) ? realisticBiome.getConfig().RAVINE_FREQUENCY.get() : ravineFrequency;
        } catch (Exception ignored) {}

        // Return early if ravines are disabled.
        if (ravineFrequency < 1 || !enableRavines) {
            return;
        }

        if (this.rand.nextInt(ravineFrequency) == 0) {
            double d0 = (double) (chunkX * 16 + this.rand.nextInt(16));
            double d1 = (double) (this.rand.nextInt(this.rand.nextInt(40) + 8) + 20);
            double d2 = (double) (chunkZ * 16 + this.rand.nextInt(16));
            byte b0 = 1;

            for (int i1 = 0; i1 < b0; ++i1) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                this.addTunnel(this.rand.nextLong(), p_180701_4_, p_180701_5_, chunkPrimerIn, d0, d1, d2, f2, f, f1, 0, 0, 3.0D);
            }
        }
    }

    @Override
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        return (data.getBlockState(x, y, z).equals(Blocks.WATER) || data.getBlockState(x, y, z).equals(Blocks.FLOWING_WATER));
    }

    //Exception biomes to make sure we generate like biomes
    private static boolean isExceptionBiome(BiomeGenBase biome) {
        if (biome.equals(Biomes.MUSHROOM_ISLAND)) return true;
        if (biome.equals(Biomes.BEACH)) return true;
        return biome.equals(Biomes.DESERT);
    }

    //Determine if the block at the specified location is the top block for the biome, we take into account
    //Vanilla bugs to make sure that we generate the map the same way biomes does.
    private boolean istopBlock(ChunkPrimer primer, int x, int y, int z, int chunkX, int chunkZ) {
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        return (isExceptionBiome(biome) ? primer.getBlockState(x, y, z).equals(Blocks.GRASS) : primer.getBlockState(x, y, z).equals(biome.topBlock));
    }

    /**
     * Digs out the current block, default implementation removes STONE, filler, and top block
     * Sets the block to lava if y is less then 10, and AIR other wise.
     * If setting to AIR, it also checks to see if we've broken the paintSurface and if so
     * tries to make the floor the biome's top block
     * @param data   Block data array
     * @param x        local X position
     * @param y        local Y position
     * @param z        local Z position
     * @param chunkX   Chunk X position
     * @param chunkZ   Chunk Y position
     * @param foundTop True if we've encountered the biome's top block. Ideally if we've broken the paintSurface.
     */

    @Override
    protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
        BiomeGenBase biome = worldObj.getBiomeGenForCoords(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        Block top = isExceptionBiome(biome) ? Blocks.GRASS : biome.topBlock.getBlock();
        Block filler = isExceptionBiome(biome) ? Blocks.DIRT : biome.topBlock.getBlock();
        Block block = data.getBlockState(x, y, z).getBlock();

        if (block.equals(Blocks.STONE) || block.equals(filler) || block.equals(top)) {
            if (y < 10) {
                data.setBlockState(x, y, z, Blocks.LAVA.getDefaultState());
            } else {
                data.setBlockState(x, y, z, Blocks.AIR.getDefaultState());

                if (foundTop && data.getBlockState(x, y - 1, z).getBlock().equals(filler)) {
                    data.setBlockState(x, y - 1, z, top.getDefaultState());
                }
            }
        }
    }
}