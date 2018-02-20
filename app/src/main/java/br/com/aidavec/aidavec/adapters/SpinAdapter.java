package br.com.aidavec.aidavec.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.com.aidavec.aidavec.R;

public class SpinAdapter<T> extends ArrayAdapter<T> {
	
	List<T> listItem = new ArrayList<T>();
	T item;
	LayoutInflater inflater;
	Context context;
	boolean modal;

	public SpinAdapter(List<T> listItem, Context context, LayoutInflater inflater, boolean modal) {
		super(context, R.layout.spinner_item, listItem);
		this.listItem = listItem;
		this.context = context;
		this.inflater = inflater;
		this.modal = modal;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View itemView = convertView;
		if (itemView == null) {
			itemView = inflater.inflate(R.layout.spinner_item,
					parent, false);
		}

		item = listItem.get(position);

		if (item != null) {
			TextView lbl = (TextView) itemView.findViewById(R.id.lblTitle);

			//if (modal)
//				lbl.setTextSize(12);

//			lbl.setGravity(Gravity.CENTER_HORIZONTAL);
//			lbl.setTextColor(Color.WHITE);
//			lbl.setBackground(getContext().getResources().getDrawable(R.drawable.back_grad_blue));
			lbl.setText(item.toString());

		}

		return itemView;
	}
}
