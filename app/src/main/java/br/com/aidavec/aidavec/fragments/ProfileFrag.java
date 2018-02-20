package br.com.aidavec.aidavec.fragments;

import android.content.Context;
import android.content.Intent;
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
import br.com.aidavec.aidavec.helpers.Camera;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.helpers.VolleyHelper;
import br.com.aidavec.aidavec.models.Cidades;
import br.com.aidavec.aidavec.models.User;
import br.com.aidavec.aidavec.views.LoginActivity;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class ProfileFrag extends Fragment {

    Context context;
    LayoutInflater inflater;

    EditText txtName;
    EditText txtLastName;
    EditText txtEmail;
    EditText txtPhone;
//    EditText txtPassword;
//    EditText txtPasswordRep;
    Spinner ddlCity;
    Spinner ddlState;
    RoundedImageView imgProfile;

    TextView lblTitle;
    ImageView imgTitle;
    ProgressBar progress;

    LinearLayout llPreview;
    Button btnEditPreview;
    ImageView imgPreview;

    ImageLoader imageLoader;

    String fileName;

    Handler handlerUpload = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Upload com sucesso !!!", true);
                Globals.getInstance().handlerUI.sendEmptyMessage(102);
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha no upload", true);
            }
            imgTitle.setVisibility(View.VISIBLE);
            lblTitle.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    };

    Handler handlerSave = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Usuário alterado com sucesso.", true);

                imgTitle.setVisibility(View.VISIBLE);
                lblTitle.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);

                Globals.getInstance().loggedUserTemp = null;
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Erro ao salvar os dados. Tente novamente.", true);
            }
        }
    };

    Handler handlerCamera = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Capturou com sucesso !!!", true);
                imgProfile.setImageBitmap(Camera.getInstance().resultBitmap);
                imgPreview.setImageBitmap(Camera.getInstance().resultBitmap);

                if (Camera.getInstance().resultBytes != null) {

                    imgTitle.setVisibility(View.GONE);
                    lblTitle.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);

                    fileName = String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + ".jpg";
                    Api.getInstance().Upload(Camera.getInstance().resultBytes, fileName, handlerUpload);
                }
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha na captura", true);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_profile, container, false);

        Camera.getInstance().resultBytes = null;

        context = Globals.getInstance().context;
        this.inflater = inflater;

        imageLoader = VolleyHelper.getInstance().getImageLoader();

        txtName = (EditText) v.findViewById(R.id.txtFirstName);
        txtLastName = (EditText) v.findViewById(R.id.txtLastName);
        txtEmail = (EditText) v.findViewById(R.id.txtEmail);
        txtPhone = (EditText) v.findViewById(R.id.txtPhone);
 //       txtPassword = (EditText) v.findViewById(R.id.txtPassword);
 //       txtPasswordRep = (EditText) v.findViewById(R.id.txtPasswordRep);

        llPreview = (LinearLayout) v.findViewById(R.id.llPreview);
        imgPreview = (ImageView) v.findViewById(R.id.imgPreview);
        btnEditPreview = (Button) v.findViewById(R.id.btnEditPreview);

        imgProfile = (RoundedImageView) v.findViewById(R.id.imgProfile);

        ddlCity = (Spinner) v.findViewById(R.id.ddlCity);
        ddlState = (Spinner) v.findViewById(R.id.ddlState);

        lblTitle = (TextView) v.findViewById(R.id.lblTitle);
        imgTitle = (ImageView) v.findViewById(R.id.imgTitle);
        progress = (ProgressBar) v.findViewById(R.id.prbProgress);

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String v = txtPhone.getText().toString();

                //    if (v.length() > 1) {
                //        v = v.substring(1, v.length()-1) + v.charAt(0);
                //    }

                if (v.length() > 0 && v.charAt(0) != '(')
                    txtPhone.setText("(" + v);

                if (v.length() > 3 && v.charAt(3) != ')')
                    txtPhone.setText(v.substring(0, 3) + ')' + v.substring(3, v.length()));

                txtPhone.setSelection(txtPhone.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

        ddlState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LoadCities();
                ddlCity.setSelection(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llPreview.setVisibility(View.VISIBLE);
            }
        });

        btnEditPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPreview.setVisibility(View.GONE);
                Camera.getInstance().GetPicture(context, handlerCamera);
            }
        });

        llPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPreview.setVisibility(View.GONE);
            }
        });

        LoadDropDowns();

        LoadData();

        return v;
    }

    @Override
    public void onPause() {
        if (Globals.getInstance().loggedUserTemp == null)
            Globals.getInstance().loggedUserTemp = new User();

        Globals.getInstance().loggedUserTemp.setUsr_cidade(ddlCity.getSelectedItem().toString());
        Globals.getInstance().loggedUserTemp.setUsr_uf(Cidades.UFS[ddlState.getSelectedItemPosition()]);
        Globals.getInstance().loggedUserTemp.setUsr_email(txtEmail.getText().toString());
        Globals.getInstance().loggedUserTemp.setUsr_nome(txtName.getText().toString());
        Globals.getInstance().loggedUserTemp.setUsr_sobrenome(txtLastName.getText().toString());
        Globals.getInstance().loggedUserTemp.setUsr_telefone(txtPhone.getText().toString());

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Globals.getInstance().loggedUserTemp != null) {
            txtName.setText(Globals.getInstance().loggedUserTemp.getUsr_nome());
            txtLastName.setText(Globals.getInstance().loggedUserTemp.getUsr_sobrenome());
            txtEmail.setText(Globals.getInstance().loggedUserTemp.getUsr_email());
            txtPhone.setText(Globals.getInstance().loggedUserTemp.getUsr_telefone());

            LookForItemState(Globals.getInstance().loggedUserTemp.getUsr_uf());
            LookForItemCity(Globals.getInstance().loggedUserTemp.getUsr_cidade());
        }
    }

    private void LoadData() {
        txtName.setText(Globals.getInstance().loggedUser.getUsr_nome());
        txtLastName.setText(Globals.getInstance().loggedUser.getUsr_sobrenome());
        txtEmail.setText(Globals.getInstance().loggedUser.getUsr_email());
        txtPhone.setText(Globals.getInstance().loggedUser.getUsr_telefone());
 //       txtPassword.setText("");
 //       txtPasswordRep.setText("");

        LookForItemState(Globals.getInstance().loggedUser.getUsr_uf());
        LookForItemCity(Globals.getInstance().loggedUser.getUsr_cidade());

        imageLoader.get(Globals.getInstance().apiPath + "images/" + String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + ".jpg",
                imageLoader.getImageListener(imgProfile, R.drawable.ico_camera, R.drawable.ico_camera));

        imageLoader.get(Globals.getInstance().apiPath + "images/" + String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + ".jpg",
                imageLoader.getImageListener(imgPreview, R.drawable.ico_camera, R.drawable.ico_camera));

    }

    private void LoadDropDowns() {
        try {
            SpinAdapter<String> adapterStates = new SpinAdapter<String>(Cidades.getEstadosList(), Globals.getInstance().context, this.inflater, false);
            adapterStates.setDropDownViewResource(R.layout.spinner_item);
            ddlState.setAdapter(adapterStates);
            ddlState.setSelection(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadCities(){
        SpinAdapter<String> adapterStates = new SpinAdapter<String>(Cidades.getCidadesByUF(ddlState.getSelectedItemPosition()), Globals.getInstance().context, this.inflater, false);
        ddlCity.setAdapter(adapterStates);
    }

    private void LookForItemState(String state) {
        int i = Cidades.getPosEstado(state);
        ddlState.setSelection(i);

/*        for (int i = 0; i < ddlState.getCount(); i++) {
            if (Utils.CleanStr(ddlState.getItemAtPosition(i).toString()).equals(Utils.CleanStr(state))) {

                ddlState.setSelection(i);
                break;
            }
        }*/
    }

    private void LookForItemCity(String city) {
        int i = Cidades.getPosCidade(ddlState.getSelectedItemPosition(), city);
//        ddlCity.invalidate();
        ddlCity.setSelection(i);

/*        int aux = 0;
        for (int i = 0; i < ddlCity.getCount(); i++) {
            if (Utils.CleanStr(ddlCity.getItemAtPosition(i).toString()).equals(Utils.CleanStr(city))) {

                aux = i;
                ddlCity.setSelection(i);
                break;
            }
        }

        ddlCity.invalidate();
        ddlCity.setSelection(aux);*/
    }

    private String validate() {
        String errors = "";

        if (txtName.getText().length() <= 0 ||
                txtLastName.getText().length() <= 0 ||
                txtEmail.getText().length() <= 0 ||
                txtPhone.getText().length() <= 0 ||
                ddlCity.getSelectedItemPosition() <= 0 ||
                ddlState.getSelectedItemPosition() <= 0
                ) {
            errors += "* Preencha todos os campos!\n";
        }

        if (!Utils.isValidEmail(txtEmail.getText().toString()))
            errors += "* E-mail inválido!\n";

//        if (!txtPassword.getText().toString().equals(txtPasswordRep.getText().toString()))
//            errors += "* As senhas não conferem!\n";

        return errors;
    }

    private void SaveUser() {
        User newUser = new User();
        newUser.setUsr_id(Globals.getInstance().loggedUser.getUsr_id());
        newUser.setUsr_nome(txtName.getText().toString());
        newUser.setUsr_sobrenome(txtLastName.getText().toString());
        newUser.setUsr_email(txtEmail.getText().toString());
        newUser.setUsr_telefone(txtPhone.getText().toString());
        newUser.setUsr_cidade(ddlCity.getSelectedItem().toString());
        String UF = Cidades.UFS[ddlState.getSelectedItemPosition()];
        newUser.setUsr_uf(UF);
 //       if (txtPassword.getText().toString().length() > 0)
 //           newUser.setUsr_senha(txtPassword.getText().toString());
        newUser.setUsr_status(Globals.getInstance().loggedUser.getUsr_status());
        newUser.setUsr_device(FirebaseInstanceId.getInstance().getToken());

        Api.getInstance().SaveUser(handlerSave, newUser);

    }



}
