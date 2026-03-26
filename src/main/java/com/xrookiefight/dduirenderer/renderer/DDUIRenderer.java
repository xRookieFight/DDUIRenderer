package com.xrookiefight.dduirenderer.renderer;

import cn.nukkit.plugin.PluginBase;
import com.xrookiefight.dduirenderer.model.WireframeModel;

public class DDUIRenderer extends AbstractWireframeRenderer {

    private final WireframeModel model;

    public DDUIRenderer(PluginBase plugin, WireframeModel model) {
        super(plugin);
        this.model = model;
    }

    @Override
    protected WireframeModel getModel() {
        return model;
    }
}