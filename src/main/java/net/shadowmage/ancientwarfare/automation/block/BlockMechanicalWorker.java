package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.TileWorkerBC;
import net.shadowmage.ancientwarfare.automation.tile.TileWorkerTest;

public class BlockMechanicalWorker extends Block
{

public BlockMechanicalWorker(String regName)
  {
  super(Material.rock);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setBlockName(regName);
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  return new TileWorkerBC();
  }

@Override
public boolean hasTileEntity(int metadata)
  {
  return true;
  }

@Override
public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_)
  {
  TileWorkerTest te = (TileWorkerTest) p_149749_1_.getTileEntity(p_149749_2_, p_149749_3_, p_149749_4_);
  if(te!=null)
    {
    te.onBlockBroken();    
    }
  super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
  }

}