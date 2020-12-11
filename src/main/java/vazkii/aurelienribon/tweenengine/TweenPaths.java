package vazkii.aurelienribon.tweenengine;

import vazkii.aurelienribon.tweenengine.paths.CatmullRom;
import vazkii.aurelienribon.tweenengine.paths.Linear;

/**
 * Collection of built-in paths.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface TweenPaths {
	Linear linear = new Linear();
	CatmullRom catmullRom = new CatmullRom();
}
