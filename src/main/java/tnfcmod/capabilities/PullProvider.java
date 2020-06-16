package tnfcmod.capabilities;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;



public class PullProvider implements ICapabilityProvider
{
    @CapabilityInject(IPull.class)
    public static final Capability<IPull> PULL = null;
    private IPull instance;

    public PullProvider() {
        this.instance = (IPull)PULL.getDefaultInstance();
    }

    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == PULL;
    }

    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == PULL ? PULL.cast(this.instance) : null;
    }
}
