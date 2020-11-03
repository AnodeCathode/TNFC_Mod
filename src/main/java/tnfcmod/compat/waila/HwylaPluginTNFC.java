package tnfcmod.compat.waila;

import java.util.Arrays;
import java.util.List;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.dries007.tfc.compat.waila.interfaces.HwylaBlockInterface;

@WailaPlugin
public class HwylaPluginTNFC implements IWailaPlugin
{
    public static final List<IWailaPlugin> TNFC_PLUGINS = Arrays.asList(
        new HwylaBlockInterface(new PlayDetectorProvider())
    );

    @Override
    public void register(IWailaRegistrar registrar)
    {
        for (IWailaPlugin plugin : TNFC_PLUGINS)
        {
            plugin.register(registrar);
        }
    }
}
