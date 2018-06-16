package com.lucent.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class GeneralTableCellRenderer extends DefaultTableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int runningRow;
	
	public GeneralTableCellRenderer() {
		runningRow = -1;
	}
	
	public int getRunningRow() {
		return runningRow;
	}
	
	public void setRunningRow(int row) {
		runningRow = row;
	}
	
	public void resetRunningRow() {
		runningRow = -1;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(row % 2 == 0) {
			setBackground(Color.white);
		} else{
			setBackground(Color.LIGHT_GRAY);
		}	
		
		setHorizontalAlignment(JLabel.CENTER);
		
		if (row == runningRow) {
			setBackground(Color.GRAY);
		}
		
		if (column == SurepayCaseTableModel.COLUMN_RESULT) {
			int result = ((CaseResult) value).getValue();
			if (result == CaseResult.SUCCESS) {
				setForeground(Color.GREEN);
			}
			if (result == CaseResult.FAILURE ||
					result == CaseResult.ERROR) {
				setForeground(Color.RED);
			}
			if (result == CaseResult.NOT_RUN) {
				setForeground(Color.BLACK);
			}
		} else {
			setForeground(Color.BLACK);
		}
			
		return super.getTableCellRendererComponent(table, value, 
				isSelected, hasFocus, row, column);		
	}

}
