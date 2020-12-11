package vazkii.aurelienribon.tweenengine;

import vazkii.aurelienribon.tweenengine.equations.Back;
import vazkii.aurelienribon.tweenengine.equations.Bounce;
import vazkii.aurelienribon.tweenengine.equations.Circ;
import vazkii.aurelienribon.tweenengine.equations.Cubic;
import vazkii.aurelienribon.tweenengine.equations.Elastic;
import vazkii.aurelienribon.tweenengine.equations.Expo;
import vazkii.aurelienribon.tweenengine.equations.Linear;
import vazkii.aurelienribon.tweenengine.equations.Quad;
import vazkii.aurelienribon.tweenengine.equations.Quart;
import vazkii.aurelienribon.tweenengine.equations.Quint;
import vazkii.aurelienribon.tweenengine.equations.Sine;

/**
 * Collection of built-in easing equations
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public interface TweenEquations {
	Linear easeNone = Linear.INOUT;
	Quad easeInQuad = Quad.IN;
	Quad easeOutQuad = Quad.OUT;
	Quad easeInOutQuad = Quad.INOUT;
	Cubic easeInCubic = Cubic.IN;
	Cubic easeOutCubic = Cubic.OUT;
	Cubic easeInOutCubic = Cubic.INOUT;
	Quart easeInQuart = Quart.IN;
	Quart easeOutQuart = Quart.OUT;
	Quart easeInOutQuart = Quart.INOUT;
	Quint easeInQuint = Quint.IN;
	Quint easeOutQuint = Quint.OUT;
	Quint easeInOutQuint = Quint.INOUT;
	Circ easeInCirc = Circ.IN;
	Circ easeOutCirc = Circ.OUT;
	Circ easeInOutCirc = Circ.INOUT;
	Sine easeInSine = Sine.IN;
	Sine easeOutSine = Sine.OUT;
	Sine easeInOutSine = Sine.INOUT;
	Expo easeInExpo = Expo.IN;
	Expo easeOutExpo = Expo.OUT;
	Expo easeInOutExpo = Expo.INOUT;
	Back easeInBack = Back.IN;
	Back easeOutBack = Back.OUT;
	Back easeInOutBack = Back.INOUT;
	Bounce easeInBounce = Bounce.IN;
	Bounce easeOutBounce = Bounce.OUT;
	Bounce easeInOutBounce = Bounce.INOUT;
	Elastic easeInElastic = Elastic.IN;
	Elastic easeOutElastic = Elastic.OUT;
	Elastic easeInOutElastic = Elastic.INOUT;
}
