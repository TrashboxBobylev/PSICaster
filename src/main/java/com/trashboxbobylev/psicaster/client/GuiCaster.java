package com.trashboxbobylev.psicaster.client;

import com.trashboxbobylev.psicaster.PSICaster;
import com.trashboxbobylev.psicaster.container.ContainerCaster;
import io.github.phantamanta44.libnine.client.gui.L9GuiContainer;
import io.github.phantamanta44.libnine.gui.L9Container;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class GuiCaster extends L9GuiContainer {

    private ContainerCaster container;
    public static ResourceLocation GUI_CASTER;

    public GuiCaster(ContainerCaster container, @Nullable ResourceLocation bg) {
        super(container, bg);
        this.container = container;

    }

    public GuiCaster(L9Container container) {
        super(container, GUI_CASTER);
    }

    @Override
    public void drawForeground(float partialTicks, int mX, int mY) {
        super.drawForeground(partialTicks, mX, mY);
        this.drawContainerName(I18n.format("psicaster.misc.gui.caster", new Object[0]));
    }

    static {
        GUI_CASTER = PSICaster.INSTANCE.newResourceLocation("textures/gui/gui_caster.png");
    }
}
