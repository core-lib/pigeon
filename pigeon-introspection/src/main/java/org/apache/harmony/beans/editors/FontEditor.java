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

package org.apache.harmony.beans.editors;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import com.googlecode.openbeans.PropertyChangeEvent;
import com.googlecode.openbeans.PropertyChangeListener;
import com.googlecode.openbeans.PropertyEditor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class FontEditor extends Panel implements PropertyEditor {

	List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

	private Font value;

	private Object source;

	public FontEditor(Object source) {
		if (source == null) {
			throw new NullPointerException();
		}
		this.source = source;
	}

	public FontEditor() {
		super();
	}

	public Component getCustomEditor() {
		return this;
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	public String getJavaInitializationString() {
		if (value != null) {
			StringBuilder sb = new StringBuilder("new Font("); //$NON-NLS-1$
			sb.append(value.getName());
			sb.append(',');
			sb.append(value.getStyle());
			sb.append(',');
			sb.append(value.getSize() + ")"); //$NON-NLS-1$
			return sb.toString();
		}
		return null;
	}

	public String[] getTags() {
		return null;
	}

	public void setValue(Object newValue) {
		Object oldValue = value;
		value = (Font) newValue;
		PropertyChangeEvent changeAllEvent = new PropertyChangeEvent(this, "value", oldValue, value); //$NON-NLS-1$
		PropertyChangeListener[] copy = listeners.toArray(new PropertyChangeListener[0]);
		for (PropertyChangeListener listener : copy) {
			listener.propertyChange(changeAllEvent);
		}
	}

	public boolean isPaintable() {
		return true;
	}

	public void paintValue(Graphics gfx, Rectangle box) {
		Font font = (Font) getValue();
		if (font != null) {
			gfx.setFont(font);
			gfx.drawBytes("Hello".getBytes(), box.x, box.y, box.x + box.width, //$NON-NLS-1$
					box.y + box.height);
		}
	}

	public String getAsText() {
		return null;
	}

	public Object getValue() {
		return value;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		throw new IllegalArgumentException(text == null ? text : value.toString());
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	public void firePropertyChange() {
		if (listeners.isEmpty()) {
			return;
		}

		List<PropertyChangeListener> copy = new ArrayList<PropertyChangeListener>(listeners.size());
		synchronized (listeners) {
			copy.addAll(listeners);
		}

		PropertyChangeEvent changeAllEvent = new PropertyChangeEvent(source, null, null, null);
		for (Iterator<PropertyChangeListener> listenersItr = copy.iterator(); listenersItr.hasNext();) {
			PropertyChangeListener listna = listenersItr.next();
			listna.propertyChange(changeAllEvent);
		}
	}
}
