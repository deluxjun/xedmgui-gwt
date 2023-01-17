package com.speno.xedm.gui.frontend.client.stats.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.Util;

/**
 *  RepositoriesPie Panel
 * @author 박상기
 * @since 1.0
 */
public class RepositoriesPiePanel extends VLayout {	
	private static final String TITLE = "name";
	private static final String TOTAL = "maxSpace";
	private static final String LEFT = "spaceLeft";	
	private static final int ColCount = 3;
	
	/**
	 * RepositoriesPie Panel 생성
	 * @param result
	 */
	public RepositoriesPiePanel(List<Map<String, Object>> result) {
		super();
		setWidth100();
		setHeight100();
//		setOverflow(Overflow.SCROLL);
//		setAlign(VerticalAlignment.TOP);
		setBorder("1px solid #E1E1E1");
		
		HLayout colHL = new HLayout();		
//		colHL.setShowResizeBar(true);
		for(int j=0; j<result.size(); j++) {
			if( j != 0 && (j%ColCount) == 0) {
				addMember(colHL);
				colHL = new HLayout();
				colHL.setShowResizeBar(true);
			}
			
			HashMap<String, Object> map = (HashMap<String,Object>)result.get(j);
			VLayout widgetVL = new VLayout();
			widgetVL.addMember(new PieWidget( I18N.message((String)map.get(TITLE)), map));
			widgetVL.addMember(prepareLegend(map));
			colHL.addMember(widgetVL);
		}
		
		if(!(colHL.getMembers().length > 0)){
			System.out.println(colHL.getMembers().length);
		}
		addMember(colHL);
	}

	private DynamicForm prepareLegend(HashMap<String, Object> map) {
		NumberFormat fmt = NumberFormat.getFormat("###");
		
		String maxSize = (String)map.get(TOTAL);
		String spaceLeft = (String)map.get(LEFT);
		
		double dMaxSize =  fmt.parse(maxSize);
		double dSpaceLeft =  fmt.parse(spaceLeft);
		double totCount = dMaxSize + dSpaceLeft;

		DynamicForm systemForm = new DynamicForm();
		systemForm.setWidth("90%");
		systemForm.setColWidths(100, "*");
		systemForm.setTitleOrientation(TitleOrientation.LEFT);
		systemForm.setWrapItemTitles(false);
		systemForm.setNumCols(2);
		systemForm.setHeight(50);

		systemForm.setLayoutAlign(Alignment.CENTER);
		systemForm.setLayoutAlign(VerticalAlignment.TOP);
		systemForm.setAlign(Alignment.CENTER);

		StaticTextItem[] items = new StaticTextItem[2];
		
		//---------------------------------------------------------------------------------------------------
		items[0] = ItemFactory.newStaticTextItem(TOTAL, I18N.message("maxspace"), null);
		items[0].setValue(Util.formatSize(fmt.parse(maxSize)) + " ( "
				+ Util.formatPercentage((fmt.parse(maxSize) * 100 / totCount), 2) + " )");
		
		items[0].setRequired(true);
		items[0].setShouldSaveValue(false);
		items[0].setWrap(false);
		items[0].setWrapTitle(false);
		//---------------------------------------------------------------------------------------------------
		items[1] = ItemFactory.newStaticTextItem(LEFT, I18N.message("spaceleft"), null);
		items[1].setValue(Util.formatSize(fmt.parse(spaceLeft)) + " ( "
				+ Util.formatPercentage((fmt.parse(spaceLeft) * 100 / totCount), 2) + " )");
		
		items[1].setRequired(true);
		items[1].setShouldSaveValue(false);
		items[1].setWrap(false);
		items[1].setWrapTitle(false);
		//---------------------------------------------------------------------------------------------------

		systemForm.setItems(items);
		return systemForm;
	}
}
