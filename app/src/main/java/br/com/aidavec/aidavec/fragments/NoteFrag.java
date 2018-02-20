package br.com.aidavec.aidavec.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.adapters.NoteAdapter;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.models.Answers;
import br.com.aidavec.aidavec.models.Note;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class NoteFrag extends Fragment {

    public static List<Note> listObj;

    ListView lstView;
    NoteAdapter adapter;
    LayoutInflater inflater;

    public static Handler settingsHandler;
    Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
            refreshUpdates();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_note, container, false);

        lstView = (ListView) v.findViewById(R.id.lstView);
        this.inflater = inflater;

        Api.getInstance().GetNotes(handler);

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NoteDetailsFrag.obj = listObj.get(i);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_contentframe, new NoteDetailsFrag(), "TAG").addToBackStack(null).commit();
            }
        });

        Globals.getInstance().clearCountUnreadNotes();

        return v;
    }

    public void refreshUpdates() {
        if (listObj != null && listObj.size() > 0) {
            adapter = new NoteAdapter(this.inflater, getContext(), listObj, handler);
            lstView.setAdapter(adapter);
        }
    }
}
