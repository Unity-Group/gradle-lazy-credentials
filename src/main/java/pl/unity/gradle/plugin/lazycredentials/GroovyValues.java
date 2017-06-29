/*
* Copyright 2017 Unity S.A.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package pl.unity.gradle.plugin.lazycredentials;

import java.util.concurrent.atomic.AtomicReference;

import groovy.lang.Closure;
import groovy.lang.GString;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

/**
 * This exists in order to easily handle both Closures and specific values (and GStrings)
 * that user may want to supply without the need to evaluate anything early.
 */
@UtilityClass
public class GroovyValues {
	
	interface GroovyValue<T> {
		T getValue();
	}
	
	@Value
	static class SimpleGroovyValue<T> implements GroovyValue<T> {
		T value;
	}
	
	/**
	 * We don't want our value to change after first evaluation, since this
	 * can have not nice effects
	 */
	@RequiredArgsConstructor
	static class ClosureBasedGroovyValue<T> implements GroovyValue<T> {
		private final Closure<T> closure;
		private final Class<T> expectedType;
		private AtomicReference<ValueContainer> value = new AtomicReference<>();
		
		@Override
		public T getValue() {
			if (value.get() == null) {
				T newValue = closure.call();
				if (newValue instanceof GString && expectedType.equals(String.class)) {
					newValue = (T) newValue.toString();
				}
				if (newValue != null && !expectedType.isInstance(newValue)) {
					throw new IllegalArgumentException("Expected value of type " + expectedType.getCanonicalName()
						+ ". Got " + newValue.getClass().getCanonicalName() + " instead.");
				}
				value.compareAndSet(null, new ValueContainer(newValue));
			}
			return value.get().value;
		}
		
		// would use Optional, but we build against JDK7
		// (and I don't want to depend on guava just for one simple thing)
		@Value
		private class ValueContainer {
			T value;
		}
	}
	
	@RequiredArgsConstructor
	static class GStringGroovyValue implements GroovyValue<String> {
		private final GString value;
		
		@Override
		public String getValue() {
			return value.toString();
		}
	}
	
	public static <T> GroovyValue<T> asGroovyValue(Class<T> expectedType, Object value) {
		if (value instanceof Closure) {
			return new ClosureBasedGroovyValue<>((Closure) value, expectedType);
		}
		
		if (value == null || expectedType.isInstance(value)) {
			return new SimpleGroovyValue<>((T) value);
		}
		
		if (value instanceof GString && expectedType.equals(String.class)) {
			return (GroovyValue<T>) new GStringGroovyValue((GString) value);
		}
		
		throw new IllegalArgumentException("I don't know what to do with " + value.getClass().getCanonicalName()
			+ ". Expecting " + expectedType.getCanonicalName() + " or Closure");
	}
}
