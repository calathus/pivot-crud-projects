package com.ncalathus.pivot.crud;

import static com.ncalathus.pivot.crud.GenericCRUDUtils.isDefault;
import static com.ncalathus.pivot.crud.GenericCRUDUtils.scriptEngine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.validation.Validator;

import com.ncalathus.pivot.annotation.*;

public class GenericForm<T> extends BoxPane {
	
	private final Class<T> cls;
	private final Map<String, Object> namespace = new HashMap<String, Object>();
	private PivotForm fmanno = null;
	
	public GenericForm(final Class<T> cls) throws Exception {
		this.cls = cls;
		for(final Annotation annotation: cls.getDeclaredAnnotations()){
    	    if (annotation instanceof PivotForm){
    	    	fmanno = (PivotForm) annotation;
    	    	break;
    	    }
    	}
		if (fmanno == null) {
			// ??
			//throw new RuntimeException("annotation @PivotForm for class required");
			return;
		}
        setOrientation(Orientation.VERTICAL);
        setStyles("{fill:true,             padding:{top:2, left:4, bottom:12, right:4}}");        
        
        add(new Label() {{
            setText(fmanno.label());
            setStyles("{font:{bold:true}}");
        }}); // INSTANCE, name: <Label>
        
        add(new Form() {{
			setStyles(fmanno.styles());
			
	        getSections().add(new Form.Section() {{
	        	
	        	for (final Field field: cls.getFields()) {
	            	final PivotFormItem item = getPivotFormItem(field);
	            	if (item == null) {
	            		continue;
	            	}
	            	
	            	add(new TextInput() {{
	            		namespace.put(field.getName(), this);
	            		
	            		setText(item.text());
	                    setTextSize(item.textSize());
	                    setMaximumLength(item.maximumLength());
	                    
	                    // validator(): WRITABLE_PROPERTY
	                    if (!isDefault(item.validator())) {
	                    	setValidator((Validator)scriptEngine.eval(item.validator()));
	                    	//setValidator(new RegexTextValidator()); // INSTANCE, name: <validation:RegexTextValidator>
	                    }
	                    
	                    Form.setLabel(this, item.label());
	                    
	                    if (!isDefault(item.flag())) {
	                    	Form.setFlag(this, item.flag());
	                    }
	                    //Form.setFlag(this, "{messageType:'info', message:'This field only accepts numeric values from 0 through 9.'}");
	            		
	                }});
	        	}
	        }}); // INSTANCE, name: <Form.Section>		
        }});
    }
	
	public void edit(boolean enabled) {
		for (final Field field: cls.getFields()) {
        	final PivotFormItem item = getPivotFormItem(field);
        	if (item == null) {
        		continue;
        	}
        	final TextInput textInout = (TextInput)namespace.get(field.getName());
        	try {
        		textInout.setEnabled(enabled);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	public T createItem() {
		try {
			T t = cls.newInstance();
			for (final Field field: cls.getFields()) {
	        	final PivotFormItem item = getPivotFormItem(field);
	        	if (item == null) {
	        		continue;
	        	}
	        	final TextInput textInout = (TextInput)namespace.get(field.getName());
	        	try {
	        		field.set(t, convert(textInout.getText(), field.getType()));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			return t;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public T updateItem(T t0, T t) {
		try {
			for (final Field field: cls.getFields()) {
	        	final PivotFormItem item = getPivotFormItem(field);
	        	if (item == null) {
	        		continue;
	        	}
	        	try {
	        		field.set(t0, field.get(t));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			return t;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private Object convert(String value, Class<?> cls) {
		if (value == null) {
			return null;
		}
		if (value.equals("") && !cls.equals(String.class)) {
			return null;
		}
		return ConvertUtils.convert(value, cls);
	}
	
	public void clear(boolean enabled) {
		for (final Field field: cls.getFields()) {
        	final PivotFormItem item = getPivotFormItem(field);
        	if (item == null) {
        		continue;
        	}
        	final TextInput textInout = (TextInput)namespace.get(field.getName());
        	try {
        		textInout.setEnabled(enabled);
        		textInout.setText(" "); // this is dirty trick.. "" does not work!!
        		//textInout.clear();
        		//System.out.println(">> clear "+textInout.getText()+", name: "+field.getName());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	public void loadData(T t) {
		for (final Field field: cls.getFields()) {
        	final PivotFormItem item = getPivotFormItem(field);
        	if (item == null) {
        		continue;
        	}
        	final TextInput textInout = (TextInput)namespace.get(field.getName());
        	try {
        		textInout.setEnabled(false);
        		Object v = field.get(t);
				if (v != null) {
					textInout.setText(""+v);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	static PivotFormItem getPivotFormItem(final Field field) {
		for(Annotation annotation: field.getDeclaredAnnotations()){
    	    if (annotation instanceof PivotFormItem){
    	    	return (PivotFormItem) annotation;
    	    }
    	}
		return null;
	}
}
