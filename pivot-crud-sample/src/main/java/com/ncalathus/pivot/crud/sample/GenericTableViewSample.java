package com.ncalathus.pivot.crud.sample;

import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.converter.CodeEmitterRuntime;

import com.ncalathus.pivot.crud.GenericCRUDPane;

public class GenericTableViewSample implements Application {
    private Window window = null;

    @Override
    public void startup(Display display, Map<String, String> properties) throws Exception {
        final Object obj = create();
        if (obj instanceof Window) {;
            window = (Window)obj;
        } else if (obj instanceof Component) {
            window = new Window();
            window.setContent((Component)obj);
            window.setTitle("custom_table_view.bxml");
        } else {
            System.out.println("getComponent returned object with type: "+obj.getClass());
        }
        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return false;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    public static void main(String[] args) {
        DesktopApplicationContext.main(GenericTableViewSample.class, args);
    }
        
    static Window create() throws Exception {
        return new Window() {{
            final Map<String, Object> namespace = new HashMap<String, Object>();
            setTitle("Generic Table Views");
            setMaximized(true);
            final GenericCRUDPane<OlympicStanding> crudPane = new GenericCRUDPane<OlympicStanding>(OlympicStanding.class) {
            	public Object getId(OlympicStanding t) {
            		return t.getNation();
            	}
            };
            setContent(crudPane);
            
            for (OlympicStanding os: createOlympicStandings()) {
            	crudPane.addItem(os);
            }

            CodeEmitterRuntime.initialize(this, namespace);
        }};
    }
    
    static java.util.List<OlympicStanding> createOlympicStandings() throws Exception {
        java.util.List<OlympicStanding> ts = new java.util.ArrayList<OlympicStanding>();
        
        // tableViewSortListeners(): LISTENER_LIST_PROPERTY
        ts.add(new OlympicStanding() {{
            setNation("China");
            setGold(51);
            setSilver(21);
            setBronze(28);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("United States");
            setGold(36);
            setSilver(38);
            setBronze(36);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("Russia");
            setGold(23);
            setSilver(21);
            setBronze(28);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("Great Britain");
            setGold(19);
            setSilver(13);
            setBronze(15);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("Germany");
            setGold(16);
            setSilver(10);
            setBronze(15);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("Australia");
            setGold(14);
            setSilver(15);
            setBronze(17);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("South Korea");
            setGold(13);
            setSilver(10);
            setBronze(8);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("Japan");
            setGold(9);
            setSilver(6);
            setBronze(11);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("Italy");
            setGold(8);
            setSilver(10);
            setBronze(10);
        }}); // INSTANCE, name: <tableviews:OlympicStanding>
        ts.add(new OlympicStanding() {{
            setNation("France");
            setGold(7);
            setSilver(16);
            setBronze(17);
        }});
        return ts;
	}
}
