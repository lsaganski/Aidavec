package br.com.aidavec.aidavec.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.fragments.InviteFrag;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Contacts;
import br.com.aidavec.aidavec.models.Note;

public class InviteAdapter extends ArrayAdapter<Contacts> {

	LayoutInflater inflater;
	Context context;
	Contacts item;

	public InviteAdapter(LayoutInflater inflater, Context context) {
		super(context, R.layout.cell_contact, InviteFrag.listObj);
		this.context = context;
		this.inflater = inflater;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view = convertView;

		try {
			if (view == null) {
				view = inflater.inflate(R.layout.cell_contact, parent, false);

				Holder h = new Holder();
				h.lblTitle = (TextView) view.findViewById(R.id.lblTitle);
				h.chk = (CheckBox) view.findViewById(R.id.chkSelected);

				view.setTag(h);
			}

			Holder hh = (Holder) view.getTag();

			item = InviteFrag.listObj.get(position);

			if (item != null) {

				hh.lblTitle.setText(item.getName());
				hh.chk.setChecked(item.isSelected());
				hh.chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						InviteFrag.listObj.get(position).setSelected(isChecked);
					}
				});
			}
		} catch (Exception e) {
			if (Globals.getInstance().devMode)
				Utils.Show("Contacts - Fill - " + e.getMessage(), true);
		}

		return view;
	}

	static class Holder {
		TextView lblTitle;
		CheckBox chk;
	}


}
