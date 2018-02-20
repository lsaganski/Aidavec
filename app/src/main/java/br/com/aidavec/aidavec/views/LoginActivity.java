package br.com.aidavec.aidavec.views;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.adapters.SpinAdapter;
import br.com.aidavec.aidavec.base.BaseActivity;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.models.Cidades;
import br.com.aidavec.aidavec.models.Logg;
import br.com.aidavec.aidavec.models.User;
import br.com.aidavec.aidavec.models.Vehicle;
import br.com.aidavec.aidavec.services.AidavecSyncBroadcast;

public class LoginActivity extends BaseActivity {

    Context context;

    EditText txtUserName;
    EditText txtPassword;
    Spinner ddlApiServer;

    TextView lblTitle;
    ProgressBar progress;

/*    Handler handlerVehicle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                Globals.getInstance().saveVehicleInPrefs();
                lblTitle.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);

            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Erro ao logar. Tente novamente.", true);
            }

        }
    };*/

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Globals.getInstance().saveUserInPrefs();

                Api.getInstance().GetVehicle(null);

                Api.getInstance().SaveLog(new Logg("LOGIN"));

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);

                //setupAlarm();

                finish();

            } else if (msg.what == 2) {
                Utils.Show("Seu cadastro ainda não foi validado. Verifique seu e-mail.", true);
            } else if (msg.what == 9) {
                Utils.Show("Erro no servidor.", true);
            } else {
                Utils.Show("Usuário ou senha inválidos. Tente novamente.", true);
            }

            lblTitle.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);


        }
    };

    Handler handlerForgot = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Utils.Show("Enviamos um nova senha provisória para o e-mail informado. Altere sua senha provisória assim que possível.", true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        txtUserName = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        progress = (ProgressBar) findViewById(R.id.prbProgress);
        final Button btnEnter = (Button) findViewById(R.id.btnEnter);
        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        Button btnForgot = (Button) findViewById(R.id.btnForgot);
        ddlApiServer = (Spinner) findViewById(R.id.ddlApiServer);

        ddlApiServer.setVisibility(Globals.getInstance().devMode ? View.VISIBLE : View.GONE);

        txtPassword.setImeActionLabel(getResources().getString(R.string.btnEnter), EditorInfo.IME_ACTION_GO);

        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);

                    btnEnter.performClick();
                }

                return true;
            }
        });

        txtUserName.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String errors = validate();

                if (errors.length() <= 0) {
                    lblTitle.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                    Api.getInstance().Login(handler, txtUserName.getText().toString(), txtPassword.getText().toString());
                } else {
                    if (Globals.getInstance().devMode)
                        Utils.Show(errors, true);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtUserName.getText().toString().length() <= 0)
                    Utils.Show("Informe o seu e-mail", true);
                else {
                    Api.getInstance().ForgotPassword(handlerForgot, txtUserName.getText().toString());
                }
            }
        });

        ddlApiServer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Globals.getInstance().apiPath = Globals.getInstance().apiPaths.get(position);
                Globals.getInstance().prefsEditor.putString("ApiServerAddress", Globals.getInstance().apiPath);
                Globals.getInstance().prefsEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LoadDropDowns();
        LookForItemApiServer(Globals.getInstance().apiPath);
    }

    private void LoadDropDowns() {
        try {
            SpinAdapter<String> adapterApiServers = new SpinAdapter<String>(Globals.getInstance().apiPaths, Globals.getInstance().context, getLayoutInflater(), false);
            adapterApiServers.setDropDownViewResource(R.layout.spinner_item);
            ddlApiServer.setAdapter(adapterApiServers);
            ddlApiServer.setSelection(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LookForItemApiServer(String str) {
        for (int i = 0; i < ddlApiServer.getCount(); i++) {
            if (Utils.CleanStr(ddlApiServer.getItemAtPosition(i).toString()).equals(Utils.CleanStr(str))) {

                ddlApiServer.setSelection(i);
                break;
            }
        }
    }

    private String validate() {
        String errors = "";

        if (txtUserName.getText().length() <= 0 || txtPassword.getText().length() <= 0)
            errors += "Preencha todos os campos.";

        return errors;
    }






    @Override
    public void onBackPressed() {
        finish();
    }
}
