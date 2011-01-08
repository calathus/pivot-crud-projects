package com.ncalathus.pivot.crud;

import java.util.Collection;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.Map;

public class GenericCRUDUtils {
    public static final String DEFAULT_LANGUAGE = "javascript";
    public static final ScriptEngine scriptEngine;
    
	static {
		final Map<String, Object> namespace = new HashMap<String, Object>();
		ScriptEngineManager scriptEngineManager = GenericCRUDUtils.createScriptEngineManager(namespace);
        scriptEngine = scriptEngineManager.getEngineByName(DEFAULT_LANGUAGE);
	}
	
    //
	public static ScriptEngineManager createScriptEngineManager(final Map<String, Object> namespace) {
		final ScriptEngineManager scriptEngineManager = new javax.script.ScriptEngineManager();
        scriptEngineManager.setBindings(new Bindings() {
            @Override
            public Object get(Object key) {
                return namespace.get(key.toString());
            }

            @Override
            public Object put(String key, Object value) {
                return namespace.put(key, value);
            }

            @Override
            public void putAll(java.util.Map<? extends String, ? extends Object> map) {
                for (String key : map.keySet()) {
                    put(key, map.get(key));
                }
            }

            @Override
            public Object remove(Object key) {
                return namespace.remove(key.toString());
            }

            @Override
            public void clear() {
                namespace.clear();
            }

            @Override
            public boolean containsKey(Object key) {
                return namespace.containsKey(key.toString());
            }

            @Override
            public boolean containsValue(Object value) {
                boolean contains = false;
                for (String key : namespace) {
                    if (namespace.get(key).equals(value)) {
                        contains = true;
                        break;
                    }
                }

                return contains;
            }

            @Override
            public boolean isEmpty() {
                return namespace.isEmpty();
            }

            @Override
            public java.util.Set<String> keySet() {
                java.util.HashSet<String> keySet = new java.util.HashSet<String>();
                for (String key : namespace) {
                    keySet.add(key);
                }

                return keySet;
            }

            @Override
            public java.util.Set<Entry<String, Object>> entrySet() {
                java.util.HashMap<String, Object> hashMap = new java.util.HashMap<String, Object>();
                for (String key : namespace) {
                    hashMap.put(key, namespace.get(key));
                }

                return hashMap.entrySet();
            }

            @Override
            public int size() {
                return namespace.getCount();
            }

            @Override
            public Collection<Object> values() {
                java.util.ArrayList<Object> values = new java.util.ArrayList<Object>();
                for (String key : namespace) {
                    values.add(namespace.get(key));
                }
                return values;
            }
        });
        return scriptEngineManager;
	}	
	
	public static boolean isDefault(String s) {
		return s == null || s.equals("");
	}

}
