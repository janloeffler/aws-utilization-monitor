/**
 *
 */
package de.zalando.platform.awsutilizationmonitor.api.view;

import java.util.ArrayList;

import de.zalando.platform.awsutilizationmonitor.api.view.StatsTable.StatsTableRow;

/**
 * @author jloeffler
 *
 */
public class StatsTable extends ArrayList<StatsTableRow> {

	public class StatsTableRow implements Comparable<StatsTableRow> {

		private int amount;

		private String text;

		/**
		 * @param amount
		 * @param text
		 */
		public StatsTableRow(int amount, String text) {
			this.amount = amount;
			this.text = text;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(StatsTableRow row) {
			if (this.amount == row.amount)
				return this.getText().compareTo(row.getText());
			else
				return Integer.compare(this.amount, row.amount) * -1;
		}

		/**
		 * @return the amount
		 */
		public int getAmount() {
			return amount;
		}

		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}

		/**
		 * @param amount
		 *            the amount to set
		 */
		public void setAmount(int amount) {
			this.amount = amount;
		}

		/**
		 * @param text
		 *            the text to set
		 */
		public void setText(String text) {
			this.text = text;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public void add(int amount, String text) {
		this.add(new StatsTableRow(amount, text));
	}

	public String printSorted() {
		StringBuilder sb = new StringBuilder();

		this.sort(null);

		for (StatsTableRow row : this) {
			sb.append(row.text);
		}

		return sb.toString();
	}
}
