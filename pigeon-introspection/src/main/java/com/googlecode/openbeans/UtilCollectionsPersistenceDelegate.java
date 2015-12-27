/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.googlecode.openbeans;

import com.googlecode.openbeans.Encoder;
import com.googlecode.openbeans.Expression;
import com.googlecode.openbeans.PersistenceDelegate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.harmony.beans.BeansUtils;

class UtilCollectionsPersistenceDelegate extends PersistenceDelegate {

	static String CLASS_PREFIX = "java.util.Collections$"; //$NON-NLS-1$

	private static final String COLLECTIONS_TYPE = "type"; //$NON-NLS-1$

	private static final String MAP_KEY_TYPE = "keyType"; //$NON-NLS-1$

	private static final String MAP_VALUE_TYPE = "valueType"; //$NON-NLS-1$

	protected boolean mutatesTo(Object o1, Object o2) {
		if (BeansUtils.declaredEquals(o1.getClass())) {
			return o1.equals(o2);
		}
		if (o1 instanceof Collection<?>) {
			Collection<?> c1 = (Collection<?>) o1;
			Collection<?> c2 = (Collection<?>) o2;
			return c1.size() == c2.size() && c1.containsAll(c2);
		}
		if (o1 instanceof Map<?, ?>) {
			Map<?, ?> m1 = (Map<?, ?>) o1;
			Map<?, ?> m2 = (Map<?, ?>) o2;
			return m1.size() == m2.size() && m1.entrySet().containsAll(m2.entrySet());
		}
		return super.mutatesTo(o1, o2);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Expression instantiate(Object oldInstance, Encoder enc) {
		String className = oldInstance.getClass().getName();
		if (className.endsWith("UnmodifiableCollection")) { //$NON-NLS-1$
			// Collections.unmodifiableCollection(Collection);
			return new Expression(oldInstance, Collections.class, "unmodifiableCollection", new Object[] { new ArrayList( //$NON-NLS-1$
					(Collection) oldInstance) });
		} else if (className.endsWith("UnmodifiableList")) { //$NON-NLS-1$
			// Collections.unmodifiableList(List);
			return new Expression(oldInstance, Collections.class, "unmodifiableList", new Object[] { new LinkedList((Collection) oldInstance) });
		} else if (className.endsWith("UnmodifiableRandomAccessList")) { //$NON-NLS-1$
			// Collections.unmodifiableList(List);
			return new Expression(oldInstance, Collections.class, "unmodifiableList", new Object[] { new ArrayList( //$NON-NLS-1$
					(Collection) oldInstance) });
		} else if (className.endsWith("UnmodifiableSet")) { //$NON-NLS-1$
			// Collections.unmodifiableSet(Set);
			return new Expression(oldInstance, Collections.class, "unmodifiableSet", new Object[] { new HashSet( //$NON-NLS-1$
					(Set) oldInstance) });
		} else if (className.endsWith("UnmodifiableSortedSet")) { //$NON-NLS-1$
			// Collections.unmodifiableSortedSet(set)
			return new Expression(oldInstance, Collections.class, "unmodifiableSortedSet", new Object[] { new TreeSet( //$NON-NLS-1$
					(SortedSet) oldInstance) });
		} else if (className.endsWith("UnmodifiableMap")) { //$NON-NLS-1$
			return new Expression(oldInstance, Collections.class, "unmodifiableMap", new Object[] { new HashMap( //$NON-NLS-1$
					(Map) oldInstance) });
		} else if (className.endsWith("UnmodifiableSortedMap")) { //$NON-NLS-1$
			// Collections.unmodifiableSortedMap(SortedMap);
			return new Expression(oldInstance, Collections.class, "unmodifiableSortedMap", new Object[] { new TreeMap( //$NON-NLS-1$
					(Map) oldInstance) });
		} else if (className.endsWith("SynchronizedCollection")) { //$NON-NLS-1$
			// Collections.synchronizedCollection(Collection);
			return new Expression(oldInstance, Collections.class, "synchronizedCollection", new Object[] { new ArrayList( //$NON-NLS-1$
					(Collection) oldInstance) });
		} else if (className.endsWith("SynchronizedList")) { //$NON-NLS-1$
			// Collections.synchronizedList(List);
			return new Expression(oldInstance, Collections.class, "synchronizedList", new Object[] { new LinkedList( //$NON-NLS-1$
					(List) oldInstance) });
		} else if (className.endsWith("SynchronizedRandomAccessList")) { //$NON-NLS-1$
			// Collections.synchronizedList(List);
			return new Expression(oldInstance, Collections.class, "synchronizedList", new Object[] { new ArrayList( //$NON-NLS-1$
					(List) oldInstance) });
		} else if (className.endsWith("SynchronizedSet")) { //$NON-NLS-1$
			// Collections.synchronizedSet(Set);
			return new Expression(oldInstance, Collections.class, "synchronizedSet", new Object[] { new HashSet( //$NON-NLS-1$
					(Set) oldInstance) });
		} else if (className.endsWith("SynchronizedSortedSet")) { //$NON-NLS-1$
			// Collections.synchronizedSortedSet(SortedSet);
			return new Expression(oldInstance, Collections.class, "synchronizedSortedSet", new Object[] { new TreeSet( //$NON-NLS-1$
					(SortedSet) oldInstance) });
		} else if (className.endsWith("SynchronizedMap")) { //$NON-NLS-1$
			// Collections.synchronizedMap(Map);
			return new Expression(oldInstance, Collections.class, "synchronizedMap", new Object[] { new HashMap( //$NON-NLS-1$
					(Map) oldInstance) });
		} else if (className.endsWith("SynchronizedSortedMap")) { //$NON-NLS-1$
			// Collections.synchronizedSortedMap(SortedMap)
			return new Expression(oldInstance, Collections.class, "synchronizedSortedMap", new Object[] { new TreeMap( //$NON-NLS-1$
					(SortedMap) oldInstance) });
		} else if (className.endsWith("CheckedCollection")) { //$NON-NLS-1$
			// Collections.checkedCollection(Collection, Class);
			return new Expression(oldInstance, Collections.class, "checkedCollection", new Object[] { //$NON-NLS-1$
					new ArrayList((Collection) oldInstance), valueOfField(oldInstance, COLLECTIONS_TYPE) });
		} else if (className.endsWith("CheckedList")) { //$NON-NLS-1$
			// Collections.checkedList(List, Class);
			return new Expression(oldInstance, Collections.class, "checkedList", new Object[] { new LinkedList((Collection) oldInstance), valueOfField(oldInstance, COLLECTIONS_TYPE) });
		} else if (className.endsWith("CheckedRandomAccessList")) { //$NON-NLS-1$
			// Collections.checkedList(List, Class);
			return new Expression(oldInstance, Collections.class, "checkedList", new Object[] { //$NON-NLS-1$
					new ArrayList((Collection) oldInstance), valueOfField(oldInstance, COLLECTIONS_TYPE) });
		} else if (className.endsWith("CheckedSet")) { //$NON-NLS-1$
			// Collections.checkedSet(Set, Class);
			return new Expression(oldInstance, Collections.class, "checkedSet", //$NON-NLS-1$
					new Object[] { new HashSet((Set) oldInstance), valueOfField(oldInstance, COLLECTIONS_TYPE) });
		} else if (className.endsWith("CheckedSortedSet")) { //$NON-NLS-1$
			// Collections.checkedSortedSet(SortedSet, Class);
			return new Expression(oldInstance, Collections.class, "checkedSortedSet", new Object[] { //$NON-NLS-1$
					new TreeSet((Set) oldInstance), valueOfField(oldInstance, COLLECTIONS_TYPE) });
		} else if (className.endsWith("CheckedMap")) { //$NON-NLS-1$
			// Collections.checkedMap(Map, keyType, valueType);
			return new Expression(oldInstance, Collections.class, "checkedMap", //$NON-NLS-1$
					new Object[] { new HashMap((Map) oldInstance), valueOfField(oldInstance, MAP_KEY_TYPE), valueOfField(oldInstance, MAP_VALUE_TYPE) });
		} else if (className.endsWith("CheckedSortedMap")) { //$NON-NLS-1$
			// Collections.checkedSortedMap(SortedMap, keyType, valueType);
			return new Expression(oldInstance, Collections.class, "checkedSortedMap", new Object[] { //$NON-NLS-1$
					new TreeMap((Map) oldInstance), valueOfField(oldInstance, MAP_KEY_TYPE), valueOfField(oldInstance, MAP_VALUE_TYPE) });
		}
		return null;
	}

	private static Object valueOfField(Object obj, String fieldName) {
		Class<?> clazz = obj.getClass();
		Field field = null;
		while (clazz != null) {
			for (Field declaredField : clazz.getDeclaredFields()) {
				if (fieldName.equals(declaredField.getName())) {
					field = declaredField;
					break;
				}
			}
			clazz = clazz.getSuperclass();
		}
		if (field != null) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			try {
				return field.get(obj);
			} catch (Exception e) {
				// Ignored
			}
		}
		return null;
	}
}
