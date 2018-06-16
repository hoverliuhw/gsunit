package com.lucent.gui;

import com.lucent.gui.SurepayCaseTableModel;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

class SelectAllTableHeaderRenderer implements TableCellRenderer {
	JCheckBox selectAll;
	SurepayCaseTableModel tableModel;
	JTableHeader tableHeader;
	public SelectAllTableHeaderRenderer(JTable table) {
		selectAll = new JCheckBox();
		tableModel = (SurepayCaseTableModel) table.getModel();
		tableHeader = table.getTableHeader();
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int column = tableHeader.columnAtPoint(e.getPoint());
				if (column == SurepayCaseTableModel.COLUMN_SELECTED) {
					boolean selected = !selectAll.isSelected();
					selectAll.setSelected(selected);
					tableHeader.repaint();
					tableModel.selectAll(selected);
				}
			}
		});
		table.getColumnModel().getColumn(SurepayCaseTableModel.COLUMN_SELECTED)
				.setPreferredWidth(selectAll.getWidth());
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		JComponent component;
		if (column == SurepayCaseTableModel.COLUMN_SELECTED) {
			component = selectAll;
			selectAll.setHorizontalAlignment(SwingConstants.CENTER);
		} else {
			component = (JLabel) value;
		}

		return component;
	}

}
