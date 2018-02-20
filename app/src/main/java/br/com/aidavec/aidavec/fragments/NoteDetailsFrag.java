package br.com.aidavec.aidavec.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Answers;
import br.com.aidavec.aidavec.models.Note;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class NoteDetailsFrag extends Fragment implements View.OnClickListener {

    public static Note obj;
    public static List<Answers> listObjAnswers;

    LinearLayout llMensagem;
    LinearLayout llDireto;
    LinearLayout llMultiplo;

    TextView lblTitulo;
    TextView lblMensagem;

    Button btnSim;
    Button btnNao;
    Button btnA;
    Button btnB;
    Button btnC;
    Button btnD;
    Button btnE;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Utils.Show("Sua resposta foi salva. Obrigado.", true);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_contentframe, new NoteFrag(), "TAG").addToBackStack(null).commit();
        }
    };

    Handler handlerAnswers = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setUI();

            loadEvents();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_note_details, container, false);

        Globals.getInstance().clearCountUnreadNotes();

        loadComponents(v);

        Api.getInstance().GetAnswers(handlerAnswers, obj.getNot_id());

        return v;
    }

    private void loadComponents(View v) {
        llMensagem = (LinearLayout) v.findViewById(R.id.llMessage);
        llDireto = (LinearLayout) v.findViewById(R.id.llDireto);
        llMultiplo = (LinearLayout) v.findViewById(R.id.llMultiplo);

        lblTitulo = (TextView) v.findViewById(R.id.lblTitulo);
        lblMensagem = (TextView) v.findViewById(R.id.lblMensagem);

        btnSim = (Button) v.findViewById(R.id.btnSim);
        btnNao = (Button) v.findViewById(R.id.btnNao);

        btnA = (Button) v.findViewById(R.id.btnA);
        btnB = (Button) v.findViewById(R.id.btnB);
        btnC = (Button) v.findViewById(R.id.btnC);
        btnD = (Button) v.findViewById(R.id.btnD);
        btnE = (Button) v.findViewById(R.id.btnE);
    }

    private void setUI(){
        lblTitulo.setText(obj.getNot_titulo());
        lblMensagem.setText(obj.getNot_mensagem());

        if (obj != null) {

            if (obj.getNot_tipo().equals("0")) {
                llDireto.setVisibility(View.GONE);
                llMultiplo.setVisibility(View.GONE);
            } else if (obj.getNot_tipo().equals("1")) {
                llDireto.setVisibility(View.VISIBLE);
                llMultiplo.setVisibility(View.GONE);
                btnSim.setVisibility(obj.getNot_opcaoa().length() > 0 ? View.VISIBLE : View.GONE);
                btnNao.setVisibility(obj.getNot_opcaob().length() > 0 ? View.VISIBLE : View.GONE);
                btnSim.setText(obj.getNot_opcaoa());
                btnNao.setText(obj.getNot_opcaob());
                btnSim.setEnabled(listObjAnswers.get(0).getRes_resposta().length() <= 0);
                btnNao.setEnabled(listObjAnswers.get(0).getRes_resposta().length() <= 0);
                btnSim.setAlpha((listObjAnswers.get(0).getRes_resposta().length() > 0 && !listObjAnswers.get(0).getRes_resposta().toString().equals(btnSim.getText().toString())) ? 0.5f : 1f);
                btnNao.setAlpha((listObjAnswers.get(0).getRes_resposta().length() > 0 && !listObjAnswers.get(0).getRes_resposta().toString().equals(btnNao.getText().toString())) ? 0.5f : 1f);
            } else {
                llDireto.setVisibility(View.GONE);
                llMultiplo.setVisibility(View.VISIBLE);
                btnA.setVisibility(obj.getNot_opcaoa().length() > 0 ? View.VISIBLE : View.GONE);
                btnB.setVisibility(obj.getNot_opcaob().length() > 0 ? View.VISIBLE : View.GONE);
                btnC.setVisibility(obj.getNot_opcaoc().length() > 0 ? View.VISIBLE : View.GONE);
                btnD.setVisibility(obj.getNot_opcaod().length() > 0 ? View.VISIBLE : View.GONE);
                btnE.setVisibility(obj.getNot_opcaoe().length() > 0 ? View.VISIBLE : View.GONE);
                btnA.setText(obj.getNot_opcaoa());
                btnB.setText(obj.getNot_opcaob());
                btnC.setText(obj.getNot_opcaoc());
                btnD.setText(obj.getNot_opcaod());
                btnE.setText(obj.getNot_opcaoe());
                btnA.setEnabled(listObjAnswers.get(0).getRes_resposta().length() <= 0);
                btnB.setEnabled(listObjAnswers.get(0).getRes_resposta().length() <= 0);
                btnC.setEnabled(listObjAnswers.get(0).getRes_resposta().length() <= 0);
                btnD.setEnabled(listObjAnswers.get(0).getRes_resposta().length() <= 0);
                btnE.setEnabled(listObjAnswers.get(0).getRes_resposta().length() <= 0);
                btnA.setAlpha((listObjAnswers.get(0).getRes_resposta().length() > 0 && !listObjAnswers.get(0).getRes_resposta().toString().equals(btnA.getText().toString())) ? 0.5f : 1f);
                btnB.setAlpha((listObjAnswers.get(0).getRes_resposta().length() > 0 && !listObjAnswers.get(0).getRes_resposta().toString().equals(btnB.getText().toString())) ? 0.5f : 1f);
                btnC.setAlpha((listObjAnswers.get(0).getRes_resposta().length() > 0 && !listObjAnswers.get(0).getRes_resposta().toString().equals(btnC.getText().toString())) ? 0.5f : 1f);
                btnD.setAlpha((listObjAnswers.get(0).getRes_resposta().length() > 0 && !listObjAnswers.get(0).getRes_resposta().toString().equals(btnD.getText().toString())) ? 0.5f : 1f);
                btnE.setAlpha((listObjAnswers.get(0).getRes_resposta().length() > 0 && !listObjAnswers.get(0).getRes_resposta().toString().equals(btnE.getText().toString())) ? 0.5f : 1f);
            }
        }
    }

    private void loadEvents(){
        btnSim.setOnClickListener(this);

        btnNao.setOnClickListener(this);

        btnA.setOnClickListener(this);

        btnB.setOnClickListener(this);

        btnC.setOnClickListener(this);

        btnD.setOnClickListener(this);

        btnE.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button btn = (Button) v;
        Answers answer = listObjAnswers.get(0);
        answer.setRes_resposta(btn.getText().toString());
        Api.getInstance().SaveAnswer(handler, answer);
    }
}
