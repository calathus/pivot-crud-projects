package com.ncalathus.pivot.crud;

import java.net.URL;

import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.Action;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.LinkButton;
import org.apache.pivot.wtk.Orientation;
import org.apache.pivot.wtk.ScrollPane;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.SplitPane;
import org.apache.pivot.wtk.TableView;
import org.apache.pivot.wtk.TableViewHeader;
import org.apache.pivot.wtk.TableViewSelectionListener;
import org.apache.pivot.wtk.content.ButtonData;

public abstract class GenericCRUDPane<T> extends SplitPane {
	private final Class<T> cls;
	private final Map<String, Object> namespace = new HashMap<String, Object>();
	private static final String imageFolder = "images/";
	
	private static final String 
		MAIN_FORM = "mainform",
		MAIN_TABLE_VIEW = "maintableView";
	
	public GenericCRUDPane(final Class<T> cls) throws Exception {
		super(Orientation.VERTICAL);
		this.cls = cls;
		
		setSplitRatio(0.4f);
		
		// need to create components in this order, tableView, formContent, formCommandsPane
		final MainTableView tableView = new MainTableView();
		final FormContent formContent = new FormContent();
		final FormCommandsPane formCommandsPane = new FormCommandsPane();
		
		setLeft(new Border() {{
        	setContent(
    			new ScrollPane() {{
    				setHorizontalScrollBarPolicy(ScrollPane.ScrollBarPolicy.FILL);
    				setView(tableView);
    				setColumnHeader(new TableViewHeader() {{
    	    			setTableView(getTableView());
    	    			setSortMode(TableViewHeader.SortMode.MULTI_COLUMN);
    	    		}});
    			}});
		}});
		
        setRight(new BoxPane() {{
        	setOrientation(Orientation.VERTICAL);
        	add(formCommandsPane);
            add(new ScrollPane() {{
            	setHorizontalScrollBarPolicy(ScrollPane.ScrollBarPolicy.FILL);
            	setView(formContent);
            }});
        }});   
           
	}
	
    public abstract Object getId(T t);
		

	public GenericTableView<T> getTableView() {
		return (GenericTableView<T>)namespace.get(MAIN_TABLE_VIEW);
	}
	public GenericForm<T> getMainForm() {
		return (GenericForm<T>)namespace.get(MAIN_FORM);
	}

	private final Action getCRUDAction(final String key) {
		return (Action)namespace.get(key);
	}
	
	private static URL getURL(final String fileName) {
		return ClassLoader.getSystemResource(imageFolder+fileName);
	}

	//
	// TableView
	//
	class MainTableView extends GenericTableView<T> {
		MainTableView() throws Exception {
			super(cls);
			
			//
			namespace.put(MAIN_TABLE_VIEW, this);
			
		    // Action invoked to add a new element
		    namespace.put(ADD_ITE_ACTION, new Action(false) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	final T t = getMainForm().createItem();
		        	final List<T> tableData = (List<T>)getTableData();
		        	final int index = tableData.add(t);
	                setSelectedIndex(index);

		            refreshTable();
		        }
		    });
		    
		    // Action invoked to add a new element
		    final Action updateItemAction = new Action(false) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	final GenericForm<T> form = getMainForm();
		        	final T t = form.createItem();
		        	final Object  id = getId(t);
		        	final List<T> tableData = (List<T>)getTableData();
		        	for (T t0: tableData) {
		        		Object id0 = getId(t0);
		        		if (id.equals(id0)) {
		        			form.updateItem(t0, t);
		        			break;
		        		}
		        	}
		        	setEnabled(false);
		            refreshTable();
		        }
		    };
		    namespace.put(UPDATE_ITE_ACTION, updateItemAction);
		    
		    // Action invoke to remove selected elements
		    final Action removeItemAction = new Action(false) {
		        @Override
		        public void perform(Component source) {
		            int selectedIndex = getFirstSelectedIndex();
		            int selectionLength = getLastSelectedIndex() - selectedIndex + 1;
		            getTableData().remove(selectedIndex, selectionLength);
		            
		            int element_size = getTableData().getLength();
		            if (selectedIndex >= element_size) {
		                selectedIndex = element_size - 1;
		            }

		            setSelectedIndex(selectedIndex);

		            if (selectedIndex == -1) {
		                refreshDetail();
		                //symbolTextInput.requestFocus();
		            }
		        }
		    };
		    namespace.put(REMOVE_ITE_ACTION, removeItemAction);

			getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
                @Override
                public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
                    int firstSelectedIndex = getFirstSelectedIndex();
                    boolean selected = firstSelectedIndex != -1;
                    getCRUDAction(NEW_ITEM_ACTION).setEnabled(!selected);
                    getCRUDAction(ADD_ITE_ACTION).setEnabled(!selected);
                    getCRUDAction(EDIT_ITE_ACTION).setEnabled(selected);
                    getCRUDAction(UPDATE_ITE_ACTION).setEnabled(!selected);
		        	removeItemAction.setEnabled(selected);
                    getCRUDAction(CANCEL_ITE_ACTION).setEnabled(selected);
                   
                    refreshDetail();
                }
            });
		}
        		
		T newInstance() {
			try {
				return cls.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		
	    @SuppressWarnings("unchecked")
	    private void refreshDetail() {
	        T t = null;
	        int firstSelectedIndex = getFirstSelectedIndex();
	        if (firstSelectedIndex != -1) {
	            int lastSelectedIndex = getLastSelectedIndex();

	            if (firstSelectedIndex == lastSelectedIndex) {
	                List<T> tableData = (List<T>)getTableData();
	                t = tableData.get(firstSelectedIndex);
	            } else {
	                t = newInstance();
	            }
	        } else {
	        	t = newInstance();
	        }
	        //new Exception().printStackTrace();
	        System.out.println(">> refreshDetail "+t.toString());
	        getMainForm().loadData(t);
	    }
	}
	
	//
	// Form 
	//
	private static final String
	NEW_ITEM_ACTION = "newItemAction",
	ADD_ITE_ACTION = "addItemAction",
	EDIT_ITE_ACTION = "editItemAction",
	UPDATE_ITE_ACTION = "updateItemAction",
	REMOVE_ITE_ACTION = "removeItemAction",
	CANCEL_ITE_ACTION = "cancelItemAction";

	class FormCommandsPane extends BoxPane {
		FormCommandsPane() {
        	setOrientation(Orientation.HORIZONTAL);
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("New item");
                setButtonData(new ButtonData() {{
                	setText("New");
                    setIcon(getURL("add.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(getCRUDAction(NEW_ITEM_ACTION));
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Add item");
                setButtonData(new ButtonData() {{
                	setText("Add");
                    setIcon(getURL("add.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(getCRUDAction(ADD_ITE_ACTION));
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Edit item");
                setButtonData(new ButtonData() {{
                	setText("Edit");
                    setIcon(getURL("add.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(getCRUDAction(EDIT_ITE_ACTION));
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Update item");
                setButtonData(new ButtonData() {{
                	setText("Update");
                    setIcon(getURL("add.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(getCRUDAction(UPDATE_ITE_ACTION));
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Remove item");
                setButtonData(new ButtonData() {{
                	setText("Delete");
                    setIcon(getURL("delete.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(getCRUDAction(REMOVE_ITE_ACTION));
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Cancel New item");
                setButtonData(new ButtonData() {{
                	setText("Cancel");
                    setIcon(getURL("delete.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(getCRUDAction(CANCEL_ITE_ACTION));
            }}); // INSTANCE, name: <LinkButton>
		}
	}
	
	class FormContent extends GenericForm<T> {
		FormContent() throws Exception {
			super(cls);
			
        	//
        	namespace.put(MAIN_FORM, this);
        	
		    namespace.put(EDIT_ITE_ACTION, new Action(false) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	edit(true);
		        	
		        	setEnabled(false);
		        	setEnabled0(ADD_ITE_ACTION, false);
		        	setEnabled0(REMOVE_ITE_ACTION, false);
		        	setEnabled0(NEW_ITEM_ACTION, false);
		        	
		        	setEnabled0(UPDATE_ITE_ACTION, true);
		        	setEnabled0(CANCEL_ITE_ACTION, true);
		        }
		    });
		    namespace.put(NEW_ITEM_ACTION, new Action(true) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	getTableView().setSelectedIndex(-1);
        	        clear(true);
        	        
		        	setEnabled(false);
		        	setEnabled0(EDIT_ITE_ACTION, false);
		        	setEnabled0(UPDATE_ITE_ACTION, false);
		        	setEnabled0(REMOVE_ITE_ACTION, false);
		        	
		        	setEnabled0(ADD_ITE_ACTION, true);
		        	setEnabled0(CANCEL_ITE_ACTION, true);
		        }
		    });
		    namespace.put(CANCEL_ITE_ACTION, new Action(false) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	getTableView().setSelectedIndex(-1);
        	        clear(false);
        	        
		        	setEnabled(false);
		        	setEnabled0(NEW_ITEM_ACTION, true);
		        	
		        	setEnabled0(ADD_ITE_ACTION, false);
		        	setEnabled0(EDIT_ITE_ACTION, false);
		        	setEnabled0(UPDATE_ITE_ACTION, false);
		        	setEnabled0(REMOVE_ITE_ACTION, false);
		        }
		    });
		}
		void setEnabled0(String actionName, boolean b) {
			getCRUDAction(actionName).setEnabled(b);
		}
	}
}
