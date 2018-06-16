package com.lucent.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class SurepayCaseTableModel extends AbstractTableModel {
private static final long serialVersionUID = 1L;
	
	public static final int COLUMN_SELECTED = 0;
	public static final int COLUMN_SEQ_NO = 1;
	public static final int COLUMN_TID = 2;
	public static final int COLUMN_FEATURE_ID = 3;
	public static final int COLUMN_CUSTOMER = 4;
	public static final int COLUMN_TIMECOST = 5;
	public static final int COLUMN_RESULT = 6;
	
	public static final Object columnSource[] = {"", "No.", "Tid", "Fid", "Customer", "TimeCost", "Result" };
	private ArrayList<Object> data;
	public SurepayCaseTableModel() {
		data = new ArrayList<Object>();
	}
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnSource.length;
	}
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		Object[] caseItem = (Object[]) data.get(row);
		return caseItem[col];
	}
	
	public Class<?> getColumnClass(int col) {
		return getValueAt(0, col).getClass();
	}

	public String getColumnName(int column) {
		if (column == SurepayCaseTableModel.COLUMN_SELECTED) {
			return "All";
		}

		return columnSource[column].toString();
	}

	public void setValueAt(Object value, int row, int col) {
		Object[] caseItem = (Object[]) data.get(row);
		caseItem[col] = value;
		fireTableCellUpdated(row, col);
	}

	public boolean isCellEditable(int row, int col) {
		return col == SurepayCaseTableModel.COLUMN_SELECTED;
	}

	public void selectAll(boolean selected) {
		int len = getRowCount();
		for (int i = 0; i < len; i++) {
			setValueAt(Boolean.valueOf(selected), i, SurepayCaseTableModel.COLUMN_SELECTED);
		}
	}

	public void addRow(Object[] rowToAdd) {
		if (rowToAdd.length == columnSource.length) {
			data.add(rowToAdd);
			fireTableDataChanged();
		} else {
			System.out.println("invalid row to add: number of column doesnt match!");
		}
	}
	
	public void addRow(String str) {
		String[] prop = str.split("\\s+");
		ArrayList<Object> item = new ArrayList<Object>(getColumnCount());
		item.add(Boolean.valueOf(false));
		item.add(String.valueOf(getRowCount() + 1));
		for (String s : prop) {
			item.add(s);
		}
		item.add(String.valueOf(0));
		item.add(CaseResult.RESULT_NOT_RUN);
		
		addRow(item.toArray());
	}

	public void removeRow(int row) {
		if (row >= 0 && row < getRowCount()) {
			data.remove(row);
			fireTableDataChanged();
		}
	}
	
	public void removeAll() {
		data.clear();
		fireTableDataChanged();
	}

}
