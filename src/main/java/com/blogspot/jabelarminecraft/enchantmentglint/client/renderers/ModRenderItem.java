package com.blogspot.jabelarminecraft.enchantmentglint.client.renderers;

import java.util.List;

import com.blogspot.jabelarminecraft.enchantmentglint.MainMod;
import com.blogspot.jabelarminecraft.enchantmentglint.init.ModConfig;
import com.blogspot.jabelarminecraft.enchantmentglint.proxy.ClientProxy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModRenderItem extends RenderItem
{
    private static final ResourceLocation RES_ITEM_GLINT_RUNE = new ResourceLocation(MainMod.MODID, "textures/misc/enchanted_item_glint_rune.png");
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(MainMod.MODID, "textures/misc/enchanted_item_glint.png");
    private final TextureManager textureManager;
    private final ItemColors itemColors;
    @SuppressWarnings("unused")
    private final ItemModelMesher itemModelMesher;

    public ModRenderItem(TextureManager parTextureManager, ModelManager parModelManager, ItemColors parItemColors, ItemModelMesher parItemModelMesher)
    {
        super(parTextureManager, parModelManager, parItemColors);
        textureManager = parTextureManager;
        itemColors = parItemColors;
        itemModelMesher = parItemModelMesher;
    }

    @Override
    public void renderItem(ItemStack stack, IBakedModel model)
    {
        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            if (model.isBuiltInRenderer())
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableRescaleNormal();
                TileEntityItemStackRenderer.instance.renderByItem(stack);
            }
            else
            {
                renderModel(model, stack);
                
                if (stack.hasEffect())
                {
                    renderEffect(model, ClientProxy.getColorForEnchantment(EnchantmentHelper.getEnchantments(stack)));
                }
            }

            GlStateManager.popMatrix();
        }
    }
    
    private void renderEffect(IBakedModel model, int color)
    {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        if (ModConfig.useRuneTexture) 
        {
            textureManager.bindTexture(RES_ITEM_GLINT_RUNE);
        }
        else
        {
            textureManager.bindTexture(RES_ITEM_GLINT);
        }
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(16.0F, 16.0F, 16.0F);
        float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        renderModel(model, color); // original was -8372020);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(16.0F, 16.0F, 16.0F);
        float f1 = Minecraft.getSystemTime() % 4873L / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        renderModel(model, color); // original was -8372020);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }
    
    private void renderModel(IBakedModel model, ItemStack stack)
    {
        renderModel(model, -1, stack);
    }

    private void renderModel(IBakedModel model, int color)
    {
        renderModel(model, color, ItemStack.EMPTY);
    }

    private void renderModel(IBakedModel model, int color, ItemStack stack)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.ITEM);

        for (EnumFacing enumfacing : EnumFacing.values())
        {
            renderQuads(vertexBuffer, model.getQuads((IBlockState)null, enumfacing, 0L), color, stack);
        }

        renderQuads(vertexBuffer, model.getQuads((IBlockState)null, (EnumFacing)null, 0L), color, stack);
        tessellator.draw();
    }
    
    private void renderQuads(VertexBuffer renderer, List<BakedQuad> quads, int color, ItemStack stack)
    {
        boolean flag = color == -1 && !stack.isEmpty();
        int i = 0;

        for (int j = quads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = quads.get(i);
            int k = color;

            if (flag && bakedquad.hasTintIndex())
            {
                k = itemColors.colorMultiplier(stack, bakedquad.getTintIndex());

                if (EntityRenderer.anaglyphEnable)
                {
                    k = TextureUtil.anaglyphColor(k);
                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }

    public static ResourceLocation getResItemGlint()
    {
        return RES_ITEM_GLINT;
    }

    public TextureManager getTextureManager()
    {
        return textureManager;
    }

//    private void func_175036_a(IBakedModel p_175036_1_, ItemStack p_175036_2_)
//    {
//        this.func_175045_a(p_175036_1_, -1, p_175036_2_);
//    }
//
// 
//    private void func_175035_a(IBakedModel p_175035_1_, int p_175035_2_)
//    {
//        this.func_175045_a(p_175035_1_, p_175035_2_, ItemStack.EMPTY);
//    }
//
//    private void func_175045_a(IBakedModel p_175045_1_, int p_175045_2_, ItemStack p_175045_3_)
//    {
//        Tessellator tessellator = Tessellator.getInstance();
//        VertexBuffer vertexbuffer = tessellator.getBuffer();
//        vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
//
//        for (EnumFacing enumfacing : EnumFacing.values())
//        {
//            this.func_175032_a(vertexbuffer, p_175045_1_.getQuads((IBlockState)null, enumfacing, 0L), p_175045_2_, p_175045_3_);
//        }
//
//        this.func_175032_a(vertexbuffer, p_175045_1_.getQuads((IBlockState)null, (EnumFacing)null, 0L), p_175045_2_, p_175045_3_);
//        tessellator.draw();
//    }
//    
//    private void func_175032_a(VertexBuffer p_175032_1_, List<BakedQuad> p_175032_2_, int p_175032_3_, ItemStack p_175032_4_)
//    {
//        boolean flag = p_175032_3_ == -1 && !p_175032_4_.isEmpty();
//        int i = 0;
//
//        for (int j = p_175032_2_.size(); i < j; ++i)
//        {
//            BakedQuad bakedquad = p_175032_2_.get(i);
//            int k = p_175032_3_;
//
//            if (flag && bakedquad.hasTintIndex())
//            {
//                k = this.itemColors.colorMultiplier(p_175032_4_, bakedquad.getTintIndex());
//
//                if (EntityRenderer.anaglyphEnable)
//                {
//                    k = TextureUtil.anaglyphColor(k);
//                }
//
//                k = k | -16777216;
//            }
//
//            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(p_175032_1_, bakedquad, k);
//        }
//    }
//    
//
//    private void func_180451_a(IBakedModel p_180451_1_)
//    {
//        GlStateManager.depthMask(false);
//        GlStateManager.depthFunc(514);
//        GlStateManager.disableLighting();
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
//        this.textureManager.bindTexture(RES_ITEM_GLINT);
//        GlStateManager.matrixMode(5890);
//        GlStateManager.pushMatrix();
//        GlStateManager.scale(8.0F, 8.0F, 8.0F);
//        float f = Minecraft.getSystemTime() % 3000L / 3000.0F / 8.0F;
//        GlStateManager.translate(f, 0.0F, 0.0F);
//        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
//        this.func_175035_a(p_180451_1_, -8372020);
//        GlStateManager.popMatrix();
//        GlStateManager.pushMatrix();
//        GlStateManager.scale(8.0F, 8.0F, 8.0F);
//        float f1 = Minecraft.getSystemTime() % 4873L / 4873.0F / 8.0F;
//        GlStateManager.translate(-f1, 0.0F, 0.0F);
//        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
//        this.func_175035_a(p_180451_1_, -8372020);
//        GlStateManager.popMatrix();
//        GlStateManager.matrixMode(5888);
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        GlStateManager.enableLighting();
//        GlStateManager.depthFunc(515);
//        GlStateManager.depthMask(true);
//        this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//    }
//
}
