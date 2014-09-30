package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorWaterwheel;

public class BlockTorqueGeneratorWaterwheel extends BlockTorqueGenerator
{

public BlockTorqueGeneratorWaterwheel(String regName)
  {
  super(regName);
  }

@Override
public boolean invertFacing()
  {
  return true;
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileTorqueGeneratorWaterwheel();
  }

@Override
public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {return false;}

@Override
public boolean isOpaqueCube() {return false;}

@Override
public boolean isNormalCube() {return false;}

@Override
public void registerBlockIcons(IIconRegister register)
  {
  }

@Override
public IIcon getIcon(int side, int meta)
  {
  return Blocks.iron_block.getIcon(0, 0);
  }
}
