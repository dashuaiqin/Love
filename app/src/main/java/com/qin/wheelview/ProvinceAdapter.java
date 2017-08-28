package com.qin.wheelview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.qin.love.R;

/**
 * Adapter for countries
	 */
public class ProvinceAdapter extends AbstractWheelTextAdapter {
		// Countries names
		private String countries[] = AddressData.PROVINCES;

		/**
		 * Constructor
		 */
		public ProvinceAdapter(Context context) {
			super(context, R.layout.item_province_layout, NO_RESOURCE);
			setItemTextResource(R.id.wheelcity_province_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}