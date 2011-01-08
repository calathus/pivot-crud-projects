package com.ncalathus.pivot.crud.sample;

import java.util.Map;
import java.util.HashMap;

import org.apache.pivot.wtk.media.Image;

import com.ncalathus.pivot.annotation.PivotForm;
import com.ncalathus.pivot.annotation.PivotFormItem;
import com.ncalathus.pivot.annotation.PivotTableView;
import com.ncalathus.pivot.annotation.PivotTableViewColumn;

@PivotTableView(
	order = "flag,nation,gold,silver,bronze,total"
)
@PivotForm(
	label = "Olympic Standing"
)
public class OlympicStanding {
    
	static final String imageFolder = "images/flag/";
	static final Map<String, Image> flagImageFiles;
	
	static Image getImage(final String fileName) throws Exception {
		return Image.load(ClassLoader.getSystemResource(imageFolder+fileName));
	}
	static {
		try {
			flagImageFiles = new HashMap<String, Image>() {{
				put("United States", getImage("us.png"));
				put("China", getImage("cn.png"));
				put("Russia", getImage("ru.png"));
				put("Great Britain", getImage("gb.png"));
				put("Germany", getImage("de.png"));
				put("Australia", getImage("au.png"));
				put("South Korea", getImage("kr.png"));
				put("Japan", getImage("jp.png"));
				put("Italy", getImage("it.png"));
				put("France", getImage("fr.png"));
			}};
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@PivotFormItem(
		    label = "Nation",
		    flag = "",
		    
			text = "",
		    textSize = 10,
		    maximumLength = 100,
		    validator = "" // JavaScript
		)
    public String nation = null;
	
	@PivotFormItem(
	    label = "Gold",
	    flag = "",
	    
		text = "",
	    textSize = 10,
	    maximumLength = 100,
	    validator = "" // JavaScript
	)
    public int gold = 0;
	
	@PivotFormItem(
		    label = "Silver",
		    flag = "",
		    
			text = "",
		    textSize = 10,
		    maximumLength = 100,
		    validator = "" // JavaScript
		)
	public int silver = 0;
    
	@PivotFormItem(
		    label = "Bronze",
		    flag = "",
		    
			text = "",
		    textSize = 10,
		    maximumLength = 100,
		    validator = "" // JavaScript
		)
    public int bronze = 0;

	@PivotTableViewColumn(
	    name = "flag",
	    width = 25,
	    cellRenderer = "new org.apache.pivot.wtk.content.TableViewImageCellRenderer()"
	)
    public Image getFlag() {	
		return flagImageFiles.get(nation);
    }
	
	@PivotTableViewColumn(
	    name = "nation",
	    headerData = "'Nation'",
	    width = 3,
	    relative = true
	)
    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    @PivotTableViewColumn(
	    name = "gold",
	    headerData = "'Gold'",
	    width = 1,
	    relative = true
	)
    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

	@PivotTableViewColumn(
	    name = "silver",
	    headerData = "'Silver'",
	    width = 1,
	    relative = true
	)
    public int getSilver() {
        return silver;
    }

    public void setSilver(int silver) {
        this.silver = silver;
    }

	@PivotTableViewColumn(
	    name = "bronze",
	    headerData = "'Bronze'",
	    width = 1,
	    relative = true
	)
    public int getBronze() {
        return bronze;
    }

    public void setBronze(int bronze) {
        this.bronze = bronze;
    }

	@PivotTableViewColumn(
	    name = "total",
	    headerData = "'Total'",
	    width = 1,
	    relative = true
	)
    public int getTotal() {
        return (gold + silver + bronze);
    }
}
