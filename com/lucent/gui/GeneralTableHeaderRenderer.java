package com.lucent.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

class GeneralTableHeaderRenderer implements TableCellRenderer {
	DefaultTableCellRenderer renderer;
	
	GeneralTableHeaderRenderer(JTable caseTable) {
		renderer = (DefaultTableCellRenderer) 
				caseTable.getTableHeader().getDefaultRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
	}
	
	public Component getTableCellRendererComponent(
	        JTable table, Object value, boolean isSelected,
	        boolean hasFocus, int row, int col) {
		
		return renderer.getTableCellRendererComponent(
	            table, value, isSelected, hasFocus, row, col);
	}	

}
