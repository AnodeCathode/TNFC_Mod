package mods.immibis.tubestuff;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityBlackHoleFX extends EntityFX {
	
	public final double initialDist = 2.5;
	public final double gravity = 0.05;
	public float initialR, initialG, initialB;
	public double cx, cy, cz;
	
	public final int[] possibleIndices = new int[] {
		0, 1, 2, 3, 4, 5, 6, 7,
		16, 17, 19, 20, 21, 22,
		//32, // 32 = bubble, 33 = fishing rod thing
		49,
		65, 66,
		97,
		128, 129, 130, 131, 132, 133, 134, 135,
		144, 145, 146, 147, 148, 149, 150, 151
	};

	public EntityBlackHoleFX(World world, double x, double y, double z) {
		super(world, 0, 0, 0, 0, 0, 0);
		
		double dx = world.rand.nextDouble() - 0.5;
		double dy = world.rand.nextDouble() - 0.5;
		double dz = world.rand.nextDouble() - 0.5;
		cx = x; cy = y; cz = z;
		// Normalize (dx,dy,dz) then multiply it by initialDist
		try {
			double scale = initialDist / Math.sqrt(dx*dx + dy*dy + dz*dz);
			dx *= scale;
			dy *= scale;
			dz *= scale;
		} catch(ArithmeticException e) {
			dx = dy = 0;
			dz = initialDist;
		}
		this.posX = x + dx;
		this.posY = y + dy;
		this.posZ = z + dz;
		
		//particleRed = initialR = world.rand.nextFloat();
		//particleGreen = initialG = world.rand.nextFloat();
		//particleBlue = initialB = world.rand.nextFloat();
		
		particleRed = particleGreen = particleBlue = initialR = initialG = initialB = 0;
		
		//motionX = world.rand.nextGaussian() * 0.2;
		//motionY = world.rand.nextGaussian() * 0.2;
		//motionZ = world.rand.nextGaussian() * 0.2;
		
		motionX = -dz/20; motionY = 0; motionZ = dx/20;
		
		this.setParticleTextureIndex(possibleIndices[world.rand.nextInt(possibleIndices.length)]);
		//setParticleTextureIndex(144);
	}
	
	@Override
	public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        double dx = cx - posX;
        double dy = cy - posY;
        double dz = cz - posZ;
        
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        if(dist < 0.5 || dist > 8.0)
        	setDead();
        
        try {
			double scale = initialDist / dist;
			dx *= scale;
			dy *= scale;
			dz *= scale;
		} catch(ArithmeticException e) {
			dx = dy = 0;
			dz = initialDist;
		}
		this.motionX *= 0.99;
		this.motionY *= 0.99;
		this.motionZ *= 0.99;
		// Dist instead of dist*dist is intentional
		this.motionX += dx / dist * gravity;
		this.motionY += dy / dist * gravity;
		this.motionZ += dz / dist * gravity;

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        
        float colourScale = (float)Math.min(1, dist/4);
        
        particleRed = initialR * colourScale;
        particleGreen = initialG * colourScale;
        particleBlue = initialB * colourScale;
    }

}