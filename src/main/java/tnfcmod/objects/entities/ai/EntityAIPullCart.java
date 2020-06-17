package tnfcmod.objects.entities.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

import tnfcmod.capabilities.PullProvider;


public class EntityAIPullCart extends EntityAIBase
{
    private final EntityLiving living;

    public EntityAIPullCart(EntityLiving livingIn)
    {
        this.living = livingIn;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (this.living.hasCapability(PullProvider.PULL, null))
        {
            return this.living.getCapability(PullProvider.PULL, null).getDrawn() != null;
        }
        return false;
    }

}
