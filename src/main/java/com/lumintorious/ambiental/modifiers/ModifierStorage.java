package com.lumintorious.ambiental.modifiers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class ModifierStorage  implements Iterable<BaseModifier>{
	private HashMap<String, BaseModifier> map = new HashMap<String, BaseModifier>();
	
	private BaseModifier put(String key, BaseModifier value) {
		if((value.getChange() == 0f && value.getPotency() == 0f)) {
			return null;
		}
		BaseModifier modifier = map.get(key);
		if(modifier != null) {
			modifier.absorb(value);
			return modifier;
		}else {
			return map.put(key, value);
		}
	}
	
	public BaseModifier add(BaseModifier value) {
		if(value == null) {
			return null;
		}
		return this.put(value.getUnlocalizedName(), value);
	}
	
	public boolean contains(String key) {
		return map.containsKey(key);
	}
	
	public boolean contains(BaseModifier value) {
		return map.containsValue(value);
	}
	
	public BaseModifier get(String key) {
		return map.get(key);
	}
	
	public float getTotalPotency() {
		float potency = 1f;
		for(Map.Entry<String, BaseModifier> entry : map.entrySet()) {
			potency += entry.getValue().getPotency();
		}
		return potency;
	}
	
	public float getTargetTemperature() {
		float change = 0f;
		for(Map.Entry<String, BaseModifier> entry : map.entrySet()) {
			change += entry.getValue().getChange();
		}
		return change;
	}
	
	public void forEach(Consumer<BaseModifier> func) {
		map.forEach((k, v) -> {func.accept(v);});
	}

	@Override
	public Iterator<BaseModifier> iterator() {
		Map<String, BaseModifier> map1 = map;
		return new Iterator<BaseModifier>() {
			private Iterator<Map.Entry<String, BaseModifier>> mapIterator = map1.entrySet().iterator();

			@Override
			public boolean hasNext() {
				return mapIterator.hasNext();
			}

			@Override
			public BaseModifier next() {
				return mapIterator.next().getValue();
			}
		};
	}
}
