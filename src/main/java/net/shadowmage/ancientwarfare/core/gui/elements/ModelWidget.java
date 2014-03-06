package net.shadowmage.ancientwarfare.core.gui.elements;

import java.nio.ByteBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.model.Primitive;
import net.shadowmage.ancientwarfare.core.model.PrimitiveBox;
import net.shadowmage.ancientwarfare.core.util.Trig;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ModelWidget extends GuiElement
{

private ModelBaseAW model;
private ModelPiece selectedPiece = null;
private Primitive selectedPrimitive = null;

private int downX;
private int downY;

boolean dragging = false;
boolean dragLeft = true;
private int lastX;
private int lastY;

private boolean selectable = false;
private boolean doSelection = false;
private int selectionX;
private int selectionY;

int gridDisplayList = -1;

float yaw;
float pitch;
float viewDistance = 5.f;

/**
 * stored/calc'd values
 */
float viewPosX, viewPosY, viewPosZ, viewTargetX, viewTargetY, viewTargetZ;

public ModelWidget(int topLeftX, int topLeftY, int width, int height)
  {
  super(topLeftX, topLeftY, width, height);
  this.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(isMouseOverElement(evt.mx, evt.my))
        {
        if(selectable && downX==evt.mx && downY==evt.my)
          {
          doSelection = true;
          selectionX = Mouse.getX();
          selectionY = Mouse.getY();
          }
        dragging = false;
        }
      return true;
      };
    });
  this.addNewListener(new Listener(Listener.MOUSE_DOWN)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(isMouseOverElement(evt.mx, evt.my))
        {
        dragging = true;
        downX = evt.mx;
        downY = evt.my;     
        lastX = evt.mx;
        lastY = evt.my;
        dragLeft = evt.mButton==0;
        }
      return true;
      }
    });
  this.addNewListener(new Listener(Listener.MOUSE_MOVED)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(dragging && isMouseOverElement(evt.mx, evt.my))
        {
        handleMouseDragged(evt.mx, evt.my);
        }
      else
        {
        dragging = false;
        lastX = evt.mx;
        lastY = evt.my;
        }
      return true;
      }
    });
  this.addNewListener(new Listener(Listener.MOUSE_WHEEL)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(isMouseOverElement(evt.mx, evt.my))
        {
        handleMouseWheel(evt.mw);
        }
      return true;
      }
    });
  
  viewPosZ = 5;
  viewPosY = 5; 
  
  }

private void handleMouseDragged(int mx, int my)
  {
  AWLog.logDebug("handling mouse dragged...");
  int dx = mx - lastX;
  int dy = my - lastY;
  if(dragLeft)
    {
    float xChange = dy * MathHelper.sin(pitch) * MathHelper.sin(yaw);
    float zChange = dy * MathHelper.sin(pitch) * MathHelper.cos(yaw);
    
    xChange += MathHelper.cos(yaw)*dx;
    zChange -= MathHelper.sin(yaw)*dx;
    
    float yChange = dy * MathHelper.cos(pitch);
    
    viewPosX -= xChange * 0.1f;
    viewPosY += yChange * 0.1f;
    viewPosZ -= zChange * 0.1f;
    
    viewTargetX -= xChange * 0.1f;
    viewTargetY += yChange * 0.1f;
    viewTargetZ -= zChange * 0.1f;
    }
  else
    {
    yaw -= dx*Trig.TORADIANS;
    pitch += dy*Trig.TORADIANS;
    if(pitch*Trig.TODEGREES>=89.f)
      {
      pitch = 89.f * Trig.TORADIANS;
      }
    if(pitch*Trig.TODEGREES<=-89.f)
      {
      pitch = -89.f * Trig.TORADIANS;
      }    
    viewPosX = viewTargetX + viewDistance * MathHelper.sin(yaw) * MathHelper.cos(pitch);
    viewPosZ = viewTargetZ + viewDistance * MathHelper.cos(yaw) * MathHelper.cos(pitch);
    viewPosY = viewTargetY + viewDistance * MathHelper.sin(pitch);
    }
  lastX = mx;
  lastY = my;
  }

private void handleMouseWheel(int wheel)
  {
  if(wheel<0)
    {
    viewDistance+=0.25f;
    }
  else
    {
    viewDistance-=0.25f;
    }
  viewPosX = viewTargetX + viewDistance * MathHelper.sin(yaw) * MathHelper.cos(pitch);
  viewPosZ = viewTargetZ + viewDistance * MathHelper.cos(yaw) * MathHelper.cos(pitch);
  viewPosY = viewTargetY + viewDistance * MathHelper.sin(pitch);
  }

/**
 * if true, will enable mouse-picking of model pieces/primitives
 * @param val
 */
public void setSelectable(boolean val)
  {
  this.selectable = val;
  }

public void setModel(ModelBaseAW model)
  {
  this.model = model;  
  }

public void initModel()
  {
  model = new ModelBaseAW();
  model.setTextureSize(256, 256);
  ModelPiece piece = new ModelPiece(model, "part1", 0, 0, 0, 0, 0, 0, null);
  PrimitiveBox box = new PrimitiveBox(piece);
  box.setOrigin(0, 0, 0);
  box.setRotation(0, 0, 0);
  box.setBounds(-0.5f, 0.f, -0.5f, 1, 1, 1);
  model.addPiece(piece);
  piece.addPrimitive(box);
  selectedPiece = piece;
  selectedPrimitive = box;
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  setViewport();
  GL11.glClearColor(0.2f, 0.2f, 0.2f, 0.2f);
  GL11.glDisable(GL11.GL_DEPTH_TEST);
  GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  renderGrid();
  if(model!=null)
    {
    if(doSelection)
      {
      doSelection();
      doSelection = false;
      }  
    GL11.glClearColor(0.2f, 0.2f, 0.2f, 0.2f);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

    enableModelLighting();
    /**
     * TODO bind texture
     */
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
    model.renderForEditor(selectedPiece, selectedPrimitive);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    }    
  
  resetViewport();
  GL11.glEnable(GL11.GL_DEPTH_TEST);
  }

private void enableModelLighting()
  {
  EntityPlayer player = Minecraft.getMinecraft().thePlayer;
  int bright = player.worldObj.getLightBrightnessForSkyBlocks((int)player.posX, (int)player.posY, (int)player.posZ, 0);

  int var11 = bright % 65536;
  int var12 = bright / 65536;
  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var11 / 1.0F, (float)var12 / 1.0F);

  RenderHelper.enableStandardItemLighting();
  }

private void renderGrid()
  {
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glLineWidth(2.f);
  if(gridDisplayList>=0)
    {    
    GL11.glCallList(gridDisplayList);
    }
  else
    {
    gridDisplayList = GL11.glGenLists(1);
    GL11.glNewList(gridDisplayList, GL11.GL_COMPILE_AND_EXECUTE);
    GL11.glColor4f(0.f, 0.f, 1.f, 1.f);
    for(int x = -5; x<=5; x++)
      {
      GL11.glBegin(GL11.GL_LINE_LOOP);
      GL11.glVertex3f(x, 0.f, -5.f);
      GL11.glVertex3f(x, 0.f, 5.f);
      GL11.glEnd();    
      }  
    for(int z = -5; z<=5; z++)
      {
      GL11.glBegin(GL11.GL_LINE_LOOP);
      GL11.glVertex3f(-5.f, 0.f, z);
      GL11.glVertex3f(5.f, 0.f, z);
      GL11.glEnd();    
      }
    GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
    GL11.glEndList();
    }
  GL11.glEnable(GL11.GL_LIGHTING);
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

private void setViewport()
  {
  /**
   * load a clean projection matrix
   */
  GL11.glMatrixMode(GL11.GL_PROJECTION);
  GL11.glPushMatrix(); 
  GL11.glLoadIdentity();
  
  /**
   * set up the base projection transformation matrix, as well as view target and position
   * (camera setup)
   */
  Minecraft mc = Minecraft.getMinecraft();
  float aspect = (float)mc.displayWidth/(float)mc.displayHeight;  
  GLU.gluPerspective(60.f, aspect, 0.1f, 100.f); 
  GLU.gluLookAt(viewPosX, viewPosY, viewPosZ, viewTargetX, viewTargetY, viewTargetZ, 0, 1, 0);   
    
  /**
   * load a clean model-view matrix
   */
  GL11.glMatrixMode(GL11.GL_MODELVIEW);
  GL11.glPushMatrix();  
  GL11.glLoadIdentity();
  
  /**
   * and finally, clear the depth buffer 
   * (we want to ignore any world/etc, as we're rendering over-top of it all anyway)
   */
  GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
  
  /**
   * TODO push scissors viewport
   * (need to move viewport stack into base gui class)
   */
  GuiContainerBase.pushViewport(renderX, renderY, width, height);
  }

private void resetViewport()
  {
  GL11.glMatrixMode(GL11.GL_PROJECTION);
  GL11.glPopMatrix();
  GL11.glMatrixMode(GL11.GL_MODELVIEW);
  GL11.glPopMatrix();
  GuiContainerBase.popViewport();
  /**
   * TODO pop scissors viewport
   */
  }

/**
 * render for selection
 */
private void doSelection()
  {
  int posX = selectionX;
  int posY = selectionY;  

  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glClearColor(1.f, 1.f, 1.f, 1.f);
  GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  model.renderForSelection();   

  byte[] pixelColorsb = new byte[3];
  ByteBuffer pixelColors = ByteBuffer.allocateDirect(3);
  GL11.glReadPixels(posX, posY, 1, 1, GL11.GL_RGB, GL11.GL_BYTE, pixelColors);

  for(int i = 0; i < 3 ; i++)
    {
    pixelColorsb[i] = pixelColors.get(i);
    }
  
  int r = pixelColorsb[0];
  int g = pixelColorsb[1];
  int b = pixelColorsb[2];

  GL11.glEnable(GL11.GL_TEXTURE_2D);
  AWLog.logDebug("colors clicked on: "+r+","+g+","+b);
  int color = (r<<16) | (g<<8) | b;
  AWLog.logDebug("color out: "+color);

  GL11.glClearColor(.2f, .2f, .2f, 1.f);
  GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  Primitive p = model.getPrimitive(color);
  
  this.selectedPrimitive = p;
  
  if(p==null)
    {
    this.selectedPiece = null;
    }
  else
    {
    this.selectedPiece = p.parent;
    }
  this.onSelection(selectedPiece, selectedPrimitive);
  }

public ModelPiece getSelectedPiece()
  {
  return null;
  }

public Primitive getSelectedPrimitive()
  {
  return null;
  }

public ModelBaseAW getModel()
  {
  return model;
  }

/**
 * implementations should override to provide a callback for piece selection
 */
protected void onSelection(ModelPiece piece, Primitive primitive)
  {
  
  }

}