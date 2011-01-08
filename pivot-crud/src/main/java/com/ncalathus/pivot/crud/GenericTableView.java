package com.ncalathus.pivot.crud;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Comparator;

import org.apache.pivot.collections.List;
import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.TableView;
import org.apache.pivot.wtk.TableViewSortListener;

import com.ncalathus.pivot.annotation.*;

import static com.ncalathus.pivot.crud.GenericCRUDUtils.*;

public class GenericTableView<T> extends TableView {
	private final Class<T> cls;
	private final Map<String, Object> namespace = new HashMap<String, Object>();
	
	public GenericTableView(final Class<T> cls) throws Exception {
		this.cls = cls;
		PivotTableView tvanno = null;
		for(final Annotation annotation: cls.getDeclaredAnnotations()){
    	    if (annotation instanceof PivotTableView){
    	    	tvanno = (PivotTableView) annotation;
    	    	break;
    	    }
    	}
		if (tvanno == null) {
			//throw new RuntimeException("annotation @PivotTableView for class required");
			return;
		}
		final Map<String, PivotTableViewColumn> map = new HashMap<String, PivotTableViewColumn>();
		for (final Field field: cls.getFields()) {
        	final PivotTableViewColumn col = getPivotTableViewColumn(field);
        	if (col != null) map.put(col.name(), col);
        }
		for (final Method method: cls.getMethods()) {
        	final PivotTableViewColumn col = getPivotTableViewColumn(method);
        	if (col != null) map.put(col.name(), col);
        }
		
		for (final String name: tvanno.order().split(",")) {
			final PivotTableViewColumn col = map.get(name.trim());
			if (col == null) {
				throw new RuntimeException("annotation @PivotTableView contains invalid name: "+name.trim());
			}
			setTableViewColumn(col);
		}
		
        getTableViewSortListeners().add(new TableViewSortListener.Adapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void sortChanged(TableView tableView) {
            	tableView.getTableData().setComparator(new org.apache.pivot.wtk.content.TableViewRowComparator(tableView));
            }
        });
    }
	
	void setTableViewColumn(final PivotTableViewColumn col) throws Exception {
    	getColumns().add(new TableView.Column() {{
    		setName(col.name());
    		setWidth(col.width(), col.relative());    
            setMinimumWidth(col.minimumWidth());
            setMaximumWidth(col.maximumWidth());
        	
        	if (!isDefault(col.headerData())) {
        		setHeaderData(scriptEngine.eval(col.headerData()));
        	}
        	if (!isDefault(col.headerDataRenderer())) {
        		setHeaderDataRenderer((HeaderDataRenderer)scriptEngine.eval(col.headerDataRenderer()));
        	}
            if (!isDefault(col.cellRenderer())) {
        		setCellRenderer((CellRenderer)scriptEngine.eval(col.cellRenderer()));
        	}
        	if (!isDefault(col.filter())) {
        		setFilter(scriptEngine.eval(col.filter()));
        	}
        }});		
	}
	
    @SuppressWarnings("unchecked")
    public void refreshTable() {
        final ArrayList<T> ts = new ArrayList<T>(getTableData());
        final Comparator<T> comparator = getTableData().getComparator();
        ts.setComparator(comparator);
        getTableData().clear();
        setTableData(ts);
    }
	
	public void add(T... ts) {
		for (T t: ts) {
			getTableData().add(t);
		}
	}
	
	public void add(java.util.List<T> ts) {
		for (T t: ts) {
			getTableData().add(t);
		}
	}
	public void add(List<T> ts) {
		for (T t: ts) {
			getTableData().add(t);
		}
	}
	
	static PivotTableView getPivotTableView(final Class<?> cls) {
		for(final Annotation annotation: cls.getDeclaredAnnotations()){
    	    if (annotation instanceof PivotTableView){
    	    	return (PivotTableView) annotation;
    	    }
    	}
		return null;
	}
	    	
	static PivotTableViewColumn getPivotTableViewColumn(final Field field) {
		for(final Annotation annotation: field.getDeclaredAnnotations()){
    	    if (annotation instanceof PivotTableViewColumn){
    	    	return (PivotTableViewColumn) annotation;
    	    }
    	}
		return null;
	}
	
	static PivotTableViewColumn getPivotTableViewColumn(final Method method) {
		if (method.getParameterTypes().length != 0 || !method.getName().startsWith("get")) {
			return null;
		}
		for(final Annotation annotation: method.getDeclaredAnnotations()){
    	    if (annotation instanceof PivotTableViewColumn){
    	    	return (PivotTableViewColumn) annotation;
    	    }
    	}
		return null;
	}
}
