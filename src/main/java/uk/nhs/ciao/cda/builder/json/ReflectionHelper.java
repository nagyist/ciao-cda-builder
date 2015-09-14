package uk.nhs.ciao.cda.builder.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import uk.nhs.interoperability.payloads.util.Emptiable;

/**
 * Uses reflection and {@link Emptiable} to normalize object graphs
 */

// TODO: This class is dangerous! Replace with Normalizer classes (coded by hand)
public class ReflectionHelper {
	private static final ReflectionHelper INSTANCE = new ReflectionHelper();	
	private final ConcurrentMap<Class<?>, List<Field>> fieldsByClass = Maps.newConcurrentMap();
	
	public static ReflectionHelper getInstance() {
		return INSTANCE;
	}
	
	public Object normalize(final Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Collection<?>) {
			final Collection<?> values = (List<?>)object;
			
			for (final Iterator<?> iterator = values.iterator(); iterator.hasNext();) {
				final Object value = iterator.next();
				if (value != null) {
					normalize(value);
				}
				
				if (isNullOrEmpty(value)) {
					iterator.remove();
				}
			}
			
			return object;
		} else if (object instanceof String || object instanceof Enum<?> || object.getClass().isPrimitive()) {
			return object;
		}
		
		return isNullOrEmpty(object) ? null : object;
	}
	
	public boolean isNullOrEmpty(final Object object) {
		if (object == null) {
			return true;
		} else if (object instanceof Emptiable) {
			return ((Emptiable)object).isEmpty();
		} else if (object instanceof String) {
			return ((String)object).isEmpty();
		} else if (object instanceof Collection<?>) {
			return ((Collection<?>)object).isEmpty();
		} else if (object.getClass().isArray()) {
			return Array.getLength(object) == 0;
		} else if (object instanceof Enum<?>) {
			return false;
		} else if (object.getClass().isPrimitive()) {
			return false;
		}
		
		final List<Field> fields = getFields(object.getClass());
		if (!fields.isEmpty()) {
			for (final Field field: fields) {
				try {
					final Object value = field.get(object);
					if (!isNullOrEmpty(value)) {
						return false;
					}
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private List<Field> getFields(final Class<?> clazz) {
		if (clazz.isArray() || clazz.isPrimitive()) {
			return Collections.emptyList();
		}
		
		List<Field> fields = fieldsByClass.get(clazz);
		if (fields != null) {
			return fields;
		}
		
		fields = Lists.newArrayList();
		for (final Field field: clazz.getDeclaredFields()) {
			field.setAccessible(true);
			fields.add(field);
		}
		
		if (clazz.getSuperclass() != null) {
			fields.addAll(getFields(clazz.getSuperclass()));
		}
		
		final List<Field> previous = fieldsByClass.putIfAbsent(clazz, fields);
		return previous == null ? fields : previous;
	}
}
