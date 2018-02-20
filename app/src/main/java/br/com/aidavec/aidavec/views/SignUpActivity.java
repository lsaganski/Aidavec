package br.com.aidavec.aidavec.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.adapters.SpinAdapter;
import br.com.aidavec.aidavec.base.BaseActivity;
import br.com.aidavec.aidavec.controls.RoundedImageView;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.helpers.Camera;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Cidades;
import br.com.aidavec.aidavec.models.User;
import br.com.aidavec.aidavec.models.Vehicle;

/**
 * Created by Leonardo Saganski on 02/12/16.
 */
public class SignUpActivity extends BaseActivity {

    Context context;
    private Toolbar mToolbar;

    EditText txtName;
    EditText txtLastName;
    EditText txtEmail;
    EditText txtPhone;
    EditText txtPassword;
    EditText txtPasswordRep;
    Spinner ddlCity;
    Spinner ddlState;
    RoundedImageView imgProfile;
    Button btnSend;

    TextView lblTitle;
    ImageView imgTitle;
    ProgressBar progress;

    FrameLayout flUnreadNotes;

    public byte[] profilePhoto;

    String fileName;

    Handler handlerUpload = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 1) {
                    if (Globals.getInstance().devMode)
                        Utils.Show("Upload com sucesso !!!", true);
                } else {
                    if (Globals.getInstance().devMode)
                        Utils.Show("Falha no upload", true);
                }
                imgTitle.setVisibility(View.VISIBLE);
                lblTitle.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            } catch (Exception e) {
                Utils.getInstance().saveLog("SignUpActivity handlerUpload", e.getMessage().toString());
            }
        }
    };

    Handler handlerVei = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Erro ao criar veículo.", true);
            }
        }
    };

    Handler handlerSave = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 1) {
                    Utils.Show("Usuário cadastrado com sucesso. Valide seu cadastro através do e-mail que acabamos de lhe enviar.", true);
                    fileName = String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + ".jpg";
                    if (Camera.getInstance().resultBytes != null && Camera.getInstance().resultBytes.length > 0)
                        Api.getInstance().Upload(Camera.getInstance().resultBytes, fileName, handlerUpload);
                    Vehicle vei = new Vehicle();
                    vei.setUsr_id(Globals.getInstance().loggedUser.getUsr_id());
                    Api.getInstance().SaveVehicle(handlerVei, vei);

                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Utils.Show("Erro ao salvar os dados. Tente novamente.", true);
                    btnSend.setText("Enviar");
                    btnSend.setEnabled(true);

                }
            } catch (Exception e) {
                Utils.getInstance().saveLog("SignUpActivity handlerSave", e.getMessage().toString());
                btnSend.setText("Enviar");
                btnSend.setEnabled(true);

            }
        }
    };

    Handler handlerCheck = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                SaveUser();
            } else {
                imgTitle.setVisibility(View.VISIBLE);
                lblTitle.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                btnSend.setText("Enviar");
                btnSend.setEnabled(true);

                Utils.Show("E-mail já cadastrado!", true);
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
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha na captura", true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_signup);

            context = this;

            Globals.getInstance().context = this;

            setUpToolbar();

            txtName = (EditText) findViewById(R.id.txtFirstName);
            txtLastName = (EditText) findViewById(R.id.txtLastName);
            txtEmail = (EditText) findViewById(R.id.txtEmail);
            txtPhone = (EditText) findViewById(R.id.txtPhone);
            txtPassword = (EditText) findViewById(R.id.txtPassword);
            txtPasswordRep = (EditText) findViewById(R.id.txtPasswordRep);

            imgProfile = (RoundedImageView) findViewById(R.id.imgProfile);

            ddlCity = (Spinner) findViewById(R.id.ddlCity);
            ddlState = (Spinner) findViewById(R.id.ddlState);

            lblTitle = (TextView) findViewById(R.id.lblTitle);
            imgTitle = (ImageView) findViewById(R.id.imgTitle);
            progress = (ProgressBar) findViewById(R.id.prbProgress);

            flUnreadNotes = (FrameLayout) mToolbar.findViewById(R.id.flUnreadNotes);
            flUnreadNotes.setVisibility(View.GONE);

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

            btnSend = (Button) findViewById(R.id.btnSend);
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btnSend.setText("Salvando...");
                    btnSend.setEnabled(false);

                    String errors = validate();

                    if (errors.length() <= 0) {
                        imgTitle.setVisibility(View.GONE);
                        lblTitle.setVisibility(View.GONE);
                        progress.setVisibility(View.VISIBLE);

                        Api.getInstance().CheckEmailExists(handlerCheck, txtEmail.getText().toString());
                    } else {
                        Utils.Show(errors, true);
                        btnSend.setText("Enviar");
                        btnSend.setEnabled(true);
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
                    Camera.getInstance().GetPicture(context, handlerCamera);
                }
            });

            LoadDropDowns();
        } catch (Exception e) {
            Utils.getInstance().saveLog("SignUpActivity onCreate", e.getMessage().toString());
        }
    }

    private void LoadDropDowns() {
        try {
            SpinAdapter<String> adapterStates = new SpinAdapter<String>(Cidades.getEstadosList(), Globals.getInstance().context, getLayoutInflater(), false);
            adapterStates.setDropDownViewResource(R.layout.spinner_item);
            ddlState.setAdapter(adapterStates);
            ddlState.setSelection(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadCities(){
        SpinAdapter<String> adapterStates = new SpinAdapter<String>(Cidades.getCidadesByUF(ddlState.getSelectedItemPosition()), Globals.getInstance().context, getLayoutInflater(), false);
        ddlCity.setAdapter(adapterStates);
    }

    private void LookForItemState(String state) {
        for (int i = 0; i < ddlState.getCount(); i++) {
            if (Utils.CleanStr(ddlState.getItemAtPosition(i).toString()).equals(Utils.CleanStr(state))) {

                ddlState.setSelection(i);
                break;
            }
        }
    }

    private void LookForItemCity(String city) {
        int aux = 0;
        for (int i = 0; i < ddlCity.getCount(); i++) {
            if (Utils.CleanStr(ddlCity.getItemAtPosition(i).toString()).equals(Utils.CleanStr(city))) {

                aux = i;
                ddlCity.setSelection(i);
                break;
            }
        }

        ddlCity.invalidate();
        ddlCity.setSelection(aux);
    }

    private String validate() {
        String errors = "";

        if (txtName.getText().length() <= 0 ||
            txtLastName.getText().length() <= 0 ||
            txtEmail.getText().length() <= 0 ||
            txtPhone.getText().length() <= 0 ||
            txtPassword.getText().length() <= 0 ||
            ddlCity.getSelectedItemPosition() <= 0 ||
            ddlState.getSelectedItemPosition() <= 0
            ) {
            errors += "* Preencha todos os campos!\n";
        }

        if (!Utils.isValidEmail(txtEmail.getText().toString()))
            errors += "* E-mail inválido!\n";

        if (!txtPassword.getText().toString().equals(txtPasswordRep.getText().toString()))
            errors += "* As senhas não conferem!\n";

        return errors;
    }

    private void SaveUser() {
        User newUser = new User();
        newUser.setUsr_nome(txtName.getText().toString());
        newUser.setUsr_sobrenome(txtLastName.getText().toString());
        newUser.setUsr_email(txtEmail.getText().toString());
        newUser.setUsr_telefone(txtPhone.getText().toString());
        newUser.setUsr_cidade(ddlCity.getSelectedItem().toString());
        String UF = Cidades.UFS[ddlState.getSelectedItemPosition()];
        newUser.setUsr_uf(UF);
        newUser.setUsr_senha(txtPassword.getText().toString());
        newUser.setUsr_device(FirebaseInstanceId.getInstance().getToken());

        Api.getInstance().SaveUser(handlerSave, newUser);

    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            boolean needHamburger = Globals.getInstance().loggedUser != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(needHamburger);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.transparent)));
        }

    }
}