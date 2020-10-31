package mods.immibis.core.impl.texslice;

interface SpritePosition {
	int getX(int sheetWidth, int sheetHeight);
	int getY(int sheetWidth, int sheetHeight);
	SpritePosition increment();
}

class SpritePosition1D implements SpritePosition {
	private int n;
	public SpritePosition1D(int n) {this.n = n;}
	
	@Override
	public int getX(int nSpritesX, int nSpritesY) {
		return n % nSpritesX;
	}
	@Override
	public int getY(int nSpritesX, int nSpritesY) {
		return n / nSpritesX;
	}

	@Override
	public SpritePosition increment() {
		return new SpritePosition1D(n+1);
	}
}

class SpritePosition2D implements SpritePosition {
	private int x, y;
	public SpritePosition2D(int x, int y) {this.x = x; this.y = y;}
	
	@Override
	public int getX(int nSpritesX, int nSpritesY) {
		return x;
	}
	@Override
	public int getY(int nSpritesX, int nSpritesY) {
		return y;
	}

	@Override
	public SpritePosition increment() {
		return new SpritePosition2D(x+1, y);
	}
}
