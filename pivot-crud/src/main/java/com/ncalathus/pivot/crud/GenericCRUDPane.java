package com.ncalathus.pivot.crud;

import java.net.URL;

import org.apache.pivot.collections.List;
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
	private static final String imageFolder = "images/";

	private final Class<T> cls;
	
	final MainTableView tableView;
	final FormContent formContent;
	final FormCommandsPane formCommandsPane;

    public abstract Object getId(T t);
	
	private static URL getURL(final String fileName) {
		return ClassLoader.getSystemResource(imageFolder+fileName);
	}

	public GenericCRUDPane(final Class<T> cls) throws Exception {
		super(Orientation.VERTICAL);
		this.cls = cls;
		
		setSplitRatio(0.4f);
		
		// need to create components in this order, tableView, formContent, formCommandsPane
		this.tableView = new MainTableView();
		this.formContent = new FormContent();
		this.formCommandsPane = new FormCommandsPane();
		
		setLeft(new Border() {{
        	setContent(
    			new ScrollPane() {{
    				setHorizontalScrollBarPolicy(ScrollPane.ScrollBarPolicy.FILL);
    				setView(tableView);
    				setColumnHeader(new TableViewHeader() {{
    	    			setTableView(tableView);
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
	
	//
	public void addItem(T t) {
		tableView.add(t);
	}

	//
	// TableView
	//
	class MainTableView extends GenericTableView<T> {
		final Action addItemAction;
		final Action updateItemAction;
		final Action removeItemAction;
		
		MainTableView() throws Exception {
			super(cls);
			
		    // Action invoked to add a new element
		    this.addItemAction = new Action(false) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	final T t = formContent.createItem();
		        	final List<T> tableData = (List<T>)getTableData();
		        	final int index = tableData.add(t);
	                setSelectedIndex(index);

		            refreshTable();
		        }
		    };
		    
		    // Action invoked to add a new element
		    this.updateItemAction = new Action(false) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	final T t = formContent.createItem();
		        	final Object  id = getId(t);
		        	final List<T> tableData = (List<T>)getTableData();
		        	for (T t0: tableData) {
		        		Object id0 = getId(t0);
		        		if (id.equals(id0)) {
		        			formContent.updateItem(t0, t);
		        			break;
		        		}
		        	}
		        	setEnabled(false);
		            refreshTable();
		        }
		    };
		    
		    // Action invoke to remove selected elements
		    this.removeItemAction = new Action(false) {
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

			getTableViewSelectionListeners().add(new TableViewSelectionListener.Adapter() {
                @Override
                public void selectedRangesChanged(TableView tableView, Sequence<Span> previousSelectedRanges) {
                    int firstSelectedIndex = getFirstSelectedIndex();
                    boolean selected = firstSelectedIndex != -1;
                    addItemAction.setEnabled(!selected);
                    updateItemAction.setEnabled(!selected);
		        	removeItemAction.setEnabled(selected);
		        	formContent.newItemAction.setEnabled(!selected);
		        	formContent.editItemAction.setEnabled(selected);
		        	formContent.cancelItemAction.setEnabled(selected);
                   
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
	        formContent.loadData(t);
	    }
	}
	
	//
	// Form 
	//
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
                setAction(formContent.newItemAction);
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Add item");
                setButtonData(new ButtonData() {{
                	setText("Add");
                    setIcon(getURL("add.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(tableView.addItemAction);
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Edit item");
                setButtonData(new ButtonData() {{
                	setText("Edit");
                    setIcon(getURL("add.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(formContent.editItemAction);
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Update item");
                setButtonData(new ButtonData() {{
                	setText("Update");
                    setIcon(getURL("add.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(tableView.updateItemAction);
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Remove item");
                setButtonData(new ButtonData() {{
                	setText("Delete");
                    setIcon(getURL("delete.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(tableView.removeItemAction);
            }}); // INSTANCE, name: <LinkButton>
            add(new LinkButton() {{
                setEnabled(true);
                setTooltipText("Cancel New item");
                setButtonData(new ButtonData() {{
                	setText("Cancel");
                    setIcon(getURL("delete.png"));
                }}); // INSTANCE, name: <content:ButtonData>
                setAction(formContent.cancelItemAction);
            }}); // INSTANCE, name: <LinkButton>
		}
	}
	
	class FormContent extends GenericForm<T> {
		final Action newItemAction;
		final Action editItemAction;
		final Action cancelItemAction;
		
		FormContent() throws Exception {
			super(cls);
			
		    this.editItemAction = new Action(false) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	edit(true);
		        	
		        	tableView.addItemAction.setEnabled(false);
		        	tableView.updateItemAction.setEnabled(true);
		        	tableView.removeItemAction.setEnabled(false);
		        	newItemAction.setEnabled(false);
		        	editItemAction.setEnabled(false);
		        	cancelItemAction.setEnabled(true);
		        }
		    };
		    this.newItemAction = new Action(true) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	tableView.setSelectedIndex(-1);
        	        clear(true);
        	        
		        	tableView.addItemAction.setEnabled(true);
		        	tableView.updateItemAction.setEnabled(false);
		        	tableView.removeItemAction.setEnabled(false);
		        	newItemAction.setEnabled(false);
		        	editItemAction.setEnabled(false);
		        	cancelItemAction.setEnabled(true);
		        }
		    };
		    this.cancelItemAction = new Action(false) {
		        @Override
		        @SuppressWarnings("unchecked")
		        public void perform(Component source) {
		        	tableView.setSelectedIndex(-1);
        	        clear(false);
        	        
		        	tableView.addItemAction.setEnabled(false);
		        	tableView.updateItemAction.setEnabled(false);
		        	tableView.removeItemAction.setEnabled(false);
		        	newItemAction.setEnabled(true);
		        	editItemAction.setEnabled(false);
		        	cancelItemAction.setEnabled(false);
		        }
		    };
		}
	}
}
