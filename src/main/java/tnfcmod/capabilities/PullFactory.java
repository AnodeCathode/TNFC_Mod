package tnfcmod.capabilities;



import tnfcmod.objects.entities.AbstractDrawnTFC;

public class PullFactory implements IPull
{
    private AbstractDrawnTFC drawn = null;

    public PullFactory() {
    }

    public void setDrawn(AbstractDrawnTFC drawnIn) {
        this.drawn = drawnIn;
    }

    public AbstractDrawnTFC getDrawn() {
        return this.drawn;
    }
}
