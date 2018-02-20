package br.com.aidavec.aidavec.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Note;

public class NoteAdapter extends ArrayAdapter<Note> {

	LayoutInflater inflater;
	Context context;
	public List<Note> listObj;
	Note item;
	Handler handler;
//	ImageLoader imageLoader;

	public NoteAdapter(LayoutInflater inflater, Context context, List<Note> listObj, Handler handler) {
		super(context, R.layout.cell_note, listObj);
		this.context = context;
		this.inflater = inflater;
		this.listObj = listObj;
		this.handler = handler;

	//	imageLoader = AppController.getInstance().getImageLoader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		try {
			if (view == null) {
				view = inflater.inflate(R.layout.cell_note, parent, false);

				Holder h = new Holder();
				h.lblTitle = (TextView) view.findViewById(R.id.lblTitle);
//				h.lblMessage = (TextView) view.findViewById(R.id.lblMessage);
//				h.imgDefault = (ImageView) view.findViewById(R.id.imgDefault);
				view.setTag(h);
			}

			Holder hh = (Holder) view.getTag();

			item = listObj.get(position);

			if (item != null) {

				hh.lblTitle.setText(item.getNot_titulo());
//				hh.lblMessage.setText(item.getNot_mensagem());

//				if (item.getIcon() != null && item.getIcon().length() > 0) {
//					imageLoader.get(item.getIcon(), imageLoader.getImageListener(hh.imgDefault, R.drawable.ico_photo, R.drawable.ico_photo));
//				}
			}
		} catch (Exception e) {
			if (Globals.getInstance().devMode)
				Utils.Show("Category - Fill - " + e.getMessage(), true);
		}

		return view;
	}

	static class Holder {
//		ImageView imgRounded;
		TextView lblTitle;
		TextView lblMessage;
		//      CheckBox chk;
	}


}
