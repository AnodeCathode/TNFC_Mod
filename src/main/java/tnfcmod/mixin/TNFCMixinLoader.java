package tnfcmod.mixin;

import java.util.ArrayList;
import java.util.List;

import zone.rong.mixinbooter.ILateMixinLoader;

/**
 * Class is automagically called by mixinbooter
 */
public class TNFCMixinLoader implements ILateMixinLoader
{
    @Override
    public List<String> getMixinConfigs()
    {
        List<String> mixins = new ArrayList<>();
        mixins.add("tnfcmod.mixins.json");
        return mixins;
    }
}
