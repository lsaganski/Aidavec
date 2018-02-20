package br.com.aidavec.aidavec.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.firebase.iid.FirebaseInstanceId;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.adapters.SpinAdapter;
import br.com.aidavec.aidavec.controls.RoundedImageView;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Camera;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.helpers.VolleyHelper;
import br.com.aidavec.aidavec.models.Cidades;
import br.com.aidavec.aidavec.models.User;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class PasswordFrag extends Fragment {

    Context context;
    LayoutInflater inflater;

    EditText txtPassword;
    EditText txtPasswordRep;

    TextView lblTitle;
    ImageView imgTitle;
    ProgressBar progress;

    Handler handlerSave = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Utils.Show("Senha alterada com sucesso.", true);

                imgTitle.setVisibility(View.VISIBLE);
                lblTitle.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            } else {
                Utils.Show("Erro ao salvar os dados. Tente novamente.", true);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_password, container, false);

        Camera.getInstance().resultBytes = null;

        context = Globals.getInstance().context;
        this.inflater = inflater;

        txtPassword = (EditText) v.findViewById(R.id.txtPassword);
        txtPasswordRep = (EditText) v.findViewById(R.id.txtPasswordRep);

        lblTitle = (TextView) v.findViewById(R.id.lblTitle);
        imgTitle = (ImageView) v.findViewById(R.id.imgTitle);
        progress = (ProgressBar) v.findViewById(R.id.prbProgress);

        Button btnSend = (Button) v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String errors = validate();

                if (errors.length() <= 0) {
                    imgTitle.setVisibility(View.GONE);
                    lblTitle.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);

                    SaveUser();
                } else {
                    if (Globals.getInstance().devMode)
                        Utils.Show(errors, true);
                }
            }
        });

        return v;
    }

    private String validate() {
        String errors = "";

        if (txtPassword.getText().length() <= 0 ||
                txtPasswordRep.getText().length() <= 0
                ) {
            errors += "* Preencha todos os campos!\n";
        }

        if (!txtPassword.getText().toString().equals(txtPasswordRep.getText().toString()))
            errors += "* As senhas não conferem!\n";

        return errors;
    }

    private void SaveUser() {
        Api.getInstance().ChangePassword(handlerSave, txtPassword.getText().toString());
    }
}
