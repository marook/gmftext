/*
 * Copyright 2009 Markus Pielmeier
 *
 * This file is part of gmftext.
 *
 * gmftext is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * gmftext is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with gmftext.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.bughome.gmftext.generator.template.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class ObjectRegistry {
	
	private final static Logger LOGGER = Logger.getLogger(ObjectRegistry.class);
	
	private static ObjectRegistry INSTANCE;
	
	public static ObjectRegistry getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ObjectRegistry();
		}
		
		return INSTANCE;
	}
	
	private final Map<String, Object> globalObjects =
		new HashMap<String, Object>();
	
	private final Map<ObjectKey, Object> targetObjects =
		new HashMap<ObjectKey, Object>();
	
	private Object _getObject(final String key){
		if(!globalObjects.containsKey(key)){
			throw new IllegalArgumentException("No object for key " + key);
		}
		
		return globalObjects.get(key);
	}
	
	private Object _setObject(final String key,
			final Object object){
		if(globalObjects.containsKey(key)){
			throw new IllegalStateException("Object for key already registered: "
					+ key);
		}
		
		LOGGER.debug("Registered object for key " + key);
		
		globalObjects.put(key, object);
		
		return object;
	}
	
	public static Object getObject(final String key){
		return getInstance()._getObject(key);
	}

	public static Object setObject(final String key,
			final Object object){
		return getInstance()._setObject(key, object);
	}
	
	private Object _getObject(final Object target, final String key){
		final ObjectKey objectKey = new ObjectKey(target, key);
		
		if(!targetObjects.containsKey(objectKey)){
			throw new IllegalArgumentException("No object for key " + objectKey);
		}
		
		return targetObjects.get(key);
	}
	
	private Object _setObject(final Object target,
			final String key,
			final Object object){
		final ObjectKey objectKey = new ObjectKey(target, key);

		if(targetObjects.containsKey(objectKey)){
			throw new IllegalStateException("Object for key already registered: "
					+ key);
		}
		
		LOGGER.debug("Registered object for key " + objectKey);
		
		targetObjects.put(objectKey, object);
		
		return object;
	}
	
	public static Object getObject(final Object target, final String key){
		return getInstance()._getObject(target, key);
	}
	
	public static Object setObject(final Object target,
			final String key,
			final Object object){
		return getInstance()._setObject(target, key, object);
	}
	
	private static class ObjectKey {
	
		private final Object target;
		
		private final String key;
		
		private ObjectKey(final Object target, final String key){
			this.target = target;
			this.key = key;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result
					+ ((target == null) ? 0 : target.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ObjectKey other = (ObjectKey) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (!target.equals(other.target))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "[" + key + " / " + target + "]";
		}
		
	}
	
}
