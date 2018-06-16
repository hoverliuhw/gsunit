package com.lucent.gui;

import java.util.Iterator;
import java.util.function.Consumer;

import com.lucent.model.SurepayCase;

public class CaseIterator<E> implements Iterator<E> {
	private int cur;
	private SurepayCaseTableModel tableModel;
	
	public CaseIterator(SurepayCaseTableModel model) {
		cur = 0;
		this.tableModel = model;
	}

	public void forEachRemaining(Consumer<? super E> arg0) {
		throw new UnsupportedOperationException("forEachRemaining");
	}

	public boolean hasNext() {
		// TODO Auto-generated method stub
		boolean hasNext = false;
		int sum = tableModel.getRowCount();
		int i = cur;
		while (i < sum) {
			hasNext = (Boolean) tableModel.getValueAt(i, SurepayCaseTableModel.COLUMN_SELECTED);
			if (hasNext) {
				break;
			}
		}
		return hasNext;
	}

	@SuppressWarnings("unchecked")
	public E next() {
		// TODO Auto-generated method stub
		int sum = tableModel.getRowCount();
		SurepayCase spCase = null;
		while (cur < sum) {
			if ((Boolean) tableModel.getValueAt(cur, SurepayCaseTableModel.COLUMN_SELECTED)) {
				String tid = (String) tableModel.getValueAt(cur, SurepayCaseTableModel.COLUMN_TID);
				String fid = (String) tableModel.getValueAt(cur, SurepayCaseTableModel.COLUMN_FEATURE_ID);
				String cus = (String) tableModel.getValueAt(cur, SurepayCaseTableModel.COLUMN_CUSTOMER);
				spCase = new SurepayCase(tid, fid, cus);
				cur++;
				break;
			}
			cur++;
		}
		
		return (E) spCase;
	}

	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

}
