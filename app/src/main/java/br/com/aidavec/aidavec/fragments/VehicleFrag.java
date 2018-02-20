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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.toolbox.ImageLoader;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.adapters.SpinAdapter;
import br.com.aidavec.aidavec.controls.RoundedImageView;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.helpers.Camera;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.helpers.VolleyHelper;
import br.com.aidavec.aidavec.models.Cidades;
import br.com.aidavec.aidavec.models.User;
import br.com.aidavec.aidavec.models.Vehicle;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class VehicleFrag extends Fragment {

    Spinner ddlMarca;
    Spinner ddlModelo;
    Spinner ddlAno;
    Spinner ddlCor;
    Spinner ddlCobertura;

    View v;
    LayoutInflater inflater;

    Button btnSend;

    RoundedImageView btnCameraA;
    RoundedImageView btnCameraB;
    RoundedImageView btnCameraC;

    public byte[] photoA;
    public byte[] photoB;
    public byte[] photoC;

    ImageLoader imageLoader;

    LinearLayout llPreview;
    Button btnEditPreview;
    ImageView imgPreview;

    Handler handlerSave = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().loggedVehicle.getVei_status() == 1)
                    Utils.Show("Seus dados foram enviados com sucesso.", true);
                else
                if (Globals.getInstance().devMode)
                        Utils.Show("Veiculo salvo com sucesso.", true);
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Erro ao salvar os dados. Tente novamente.", true);
            }
        }
    };

    Handler handlerUploadA = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Upload A com sucesso !!!", true);
                Vehicle newV = Globals.getInstance().loggedVehicle;
                newV.setVei_okfotoa(1);

                if (newV.getVei_marca().length() > 0 &&
                        newV.getVei_modelo().length() > 0 &&
                        newV.getVei_ano().length() > 0 &&
                        newV.getVei_cor().length() > 0 &&
                        newV.getVei_cobertura().length() > 0 &&
                        newV.getVei_okfotoa() > 0 &&
                        newV.getVei_okfotob() > 0 &&
                        newV.getVei_okfotoc() > 0
                        )
                    newV.setVei_status(1);

                Api.getInstance().SaveVehicle(handlerSave, newV);
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha no upload A", true);
            }
        }
    };

    Handler handlerUploadB = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Upload B com sucesso !!!", true);
                Vehicle newV = Globals.getInstance().loggedVehicle;
                newV.setVei_okfotob(1);

                if (newV.getVei_marca().length() > 0 &&
                        newV.getVei_modelo().length() > 0 &&
                        newV.getVei_ano().length() > 0 &&
                        newV.getVei_cor().length() > 0 &&
                        newV.getVei_cobertura().length() > 0 &&
                        newV.getVei_okfotoa() > 0 &&
                        newV.getVei_okfotob() > 0 &&
                        newV.getVei_okfotoc() > 0
                        )
                    newV.setVei_status(1);

                Api.getInstance().SaveVehicle(handlerSave, newV);
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha no upload B", true);
            }
        }
    };

    Handler handlerUploadC = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Upload C com sucesso !!!", true);
                Vehicle newV = Globals.getInstance().loggedVehicle;
                newV.setVei_okfotoc(1);

                if (newV.getVei_marca().length() > 0 &&
                        newV.getVei_modelo().length() > 0 &&
                        newV.getVei_ano().length() > 0 &&
                        newV.getVei_cor().length() > 0 &&
                        newV.getVei_cobertura().length() > 0 &&
                        newV.getVei_okfotoa() > 0 &&
                        newV.getVei_okfotob() > 0 &&
                        newV.getVei_okfotoc() > 0
                        )
                    newV.setVei_status(1);

                Api.getInstance().SaveVehicle(handlerSave, newV);
            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha no upload C", true);
            }
        }
    };

    Handler handlerCameraA = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Capturou A com sucesso !!!", true);
                photoA = Camera.getInstance().resultBytes;
                btnCameraA.setImageBitmap(Camera.getInstance().resultBitmap);

                if (photoA != null)
                    Api.getInstance().Upload(photoA, String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + "_A.jpg", handlerUploadA);

            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha na captura A", true);
            }
        }
    };

    Handler handlerCameraB = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Capturou B com sucesso !!!", true);
                photoB = Camera.getInstance().resultBytes;
                btnCameraB.setImageBitmap(Camera.getInstance().resultBitmap);

                if (photoB != null)
                    Api.getInstance().Upload(photoB, String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + "_B.jpg", handlerUploadB);

            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha na captura B", true);
            }
        }
    };

    Handler handlerCameraC = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (Globals.getInstance().devMode)
                    Utils.Show("Capturou C com sucesso !!!", true);
                photoC = Camera.getInstance().resultBytes;
                btnCameraC.setImageBitmap(Camera.getInstance().resultBitmap);

                if (photoC != null)
                    Api.getInstance().Upload(photoC, String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + "_C.jpg", handlerUploadC);

            } else {
                if (Globals.getInstance().devMode)
                    Utils.Show("Falha na captura C", true);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_vehicle, container, false);

        this.v = v;
        this.inflater = inflater;

        imageLoader = VolleyHelper.getInstance().getImageLoader();

        ddlMarca = (Spinner) v.findViewById(R.id.ddlMarca);
        ddlModelo = (Spinner) v.findViewById(R.id.ddlModelo);
        ddlAno = (Spinner) v.findViewById(R.id.ddlAno);
        ddlCor = (Spinner) v.findViewById(R.id.ddlCor);
        ddlCobertura = (Spinner) v.findViewById(R.id.ddlCobertura);

        btnCameraA = (RoundedImageView) v.findViewById(R.id.btnCameraA);
        btnCameraB = (RoundedImageView) v.findViewById(R.id.btnCameraB);
        btnCameraC = (RoundedImageView) v.findViewById(R.id.btnCameraC);

        llPreview = (LinearLayout) v.findViewById(R.id.llPreview);
        imgPreview = (ImageView) v.findViewById(R.id.imgPreview);
        btnEditPreview = (Button) v.findViewById(R.id.btnEditPreview);

        Button btnSend = (Button) v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String errors = validate();

                if (errors.length() <= 0) {
                    // progress here

                    SaveVehicle();
                    //
                } else {
                    if (Globals.getInstance().devMode)
                        Utils.Show(errors, true);
                }
            }
        });

        ddlMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LoadModelos();
                ddlModelo.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCameraA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Camera.getInstance().GetPicture(Globals.getInstance().context, handlerCameraA);
                imgPreview.setTag(handlerCameraA);
                imgPreview.setImageDrawable(btnCameraA.getDrawable());
                llPreview.setVisibility(View.VISIBLE);
            }
        });

        btnCameraB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Camera.getInstance().GetPicture(Globals.getInstance().context, handlerCameraB);
                imgPreview.setTag(handlerCameraB);
                imgPreview.setImageDrawable(btnCameraB.getDrawable());
                llPreview.setVisibility(View.VISIBLE);
            }
        });

        btnCameraC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Camera.getInstance().GetPicture(Globals.getInstance().context, handlerCameraC);
                imgPreview.setTag(handlerCameraC);
                imgPreview.setImageDrawable(btnCameraC.getDrawable());
                llPreview.setVisibility(View.VISIBLE);
            }
        });

        btnEditPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPreview.setVisibility(View.GONE);
                Camera.getInstance().GetPicture(Globals.getInstance().context, (Handler) imgPreview.getTag());
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
        if (Globals.getInstance().loggedVehicleTemp == null)
            Globals.getInstance().loggedVehicleTemp = new Vehicle();

        Globals.getInstance().loggedVehicleTemp.setVei_cobertura(ddlCobertura.getSelectedItem().toString());
        Globals.getInstance().loggedVehicleTemp.setVei_marca(ddlMarca.getSelectedItem().toString());
        Globals.getInstance().loggedVehicleTemp.setVei_cor(ddlCor.getSelectedItem().toString());
        Globals.getInstance().loggedVehicleTemp.setVei_ano(ddlAno.getSelectedItem().toString());
        Globals.getInstance().loggedVehicleTemp.setVei_modelo(ddlModelo.getSelectedItem().toString());

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Globals.getInstance().loggedVehicleTemp != null) {
            LookForItemMarca(Globals.getInstance().loggedVehicleTemp.getVei_marca());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LookForItemModelo(Globals.getInstance().loggedVehicleTemp.getVei_modelo());
                }
            }, 2000);
            LookForItemAno(Globals.getInstance().loggedVehicleTemp.getVei_ano());
            LookForItemCor(Globals.getInstance().loggedVehicleTemp.getVei_cor());
            LookForItemCobertura(Globals.getInstance().loggedVehicleTemp.getVei_cobertura());
        }
    }

    private void LoadData() {
        if (Globals.getInstance().loggedVehicle != null) {
            LookForItemMarca(Globals.getInstance().loggedVehicle.getVei_marca());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LookForItemModelo(Globals.getInstance().loggedVehicle.getVei_modelo());
                }
            }, 2000);
//            LookForItemModelo(Globals.getInstance().loggedVehicle.getVei_modelo());
            LookForItemAno(Globals.getInstance().loggedVehicle.getVei_ano());
            LookForItemCor(Globals.getInstance().loggedVehicle.getVei_cor());
            LookForItemCobertura(Globals.getInstance().loggedVehicle.getVei_cobertura());
        }

        imageLoader.get(Globals.getInstance().apiPath + "images/" + String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + "_A.jpg",
                imageLoader.getImageListener(btnCameraA, R.drawable.ico_camera, R.drawable.ico_camera));

        imageLoader.get(Globals.getInstance().apiPath + "images/" + String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + "_B.jpg",
                imageLoader.getImageListener(btnCameraB, R.drawable.ico_camera, R.drawable.ico_camera));

        imageLoader.get(Globals.getInstance().apiPath + "images/" + String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + "_C.jpg",
                imageLoader.getImageListener(btnCameraC, R.drawable.ico_camera, R.drawable.ico_camera));

    }

    private void LoadDropDowns() {
        try {
            SpinAdapter<String> adapterMarcas = new SpinAdapter<String>(Vehicle.getMarcasList(), Globals.getInstance().context, this.inflater, false);
            adapterMarcas.setDropDownViewResource(R.layout.spinner_item);
            ddlMarca.setAdapter(adapterMarcas);
            ddlMarca.setSelection(1);

            SpinAdapter<String> adapterAnos = new SpinAdapter<String>(Vehicle.getAnosList(), Globals.getInstance().context, this.inflater, false);
            adapterAnos.setDropDownViewResource(R.layout.spinner_item);
            ddlAno.setAdapter(adapterAnos);
            ddlAno.setSelection(0);

            SpinAdapter<String> adapterCores = new SpinAdapter<String>(Vehicle.getCoresList(), Globals.getInstance().context, this.inflater, false);
            adapterCores.setDropDownViewResource(R.layout.spinner_item);
            ddlCor.setAdapter(adapterCores);
            ddlCor.setSelection(0);

            SpinAdapter<String> adapterCoberturas = new SpinAdapter<String>(Vehicle.getCoberturasList(), Globals.getInstance().context, this.inflater, false);
            adapterCoberturas.setDropDownViewResource(R.layout.spinner_item);
            ddlCobertura.setAdapter(adapterCoberturas);
            ddlCobertura.setSelection(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadModelos(){
        SpinAdapter<String> adapterModelos = new SpinAdapter<String>(Vehicle.getModelosbyMarca(ddlMarca.getSelectedItemPosition()-1), Globals.getInstance().context, this.inflater, false);
        ddlModelo.setAdapter(adapterModelos);
    }

    private void LookForItemMarca(String marca) {
        for (int i = 0; i < ddlMarca.getCount(); i++) {
            if (Utils.CleanStr(ddlMarca.getItemAtPosition(i).toString()).equals(Utils.CleanStr(marca))) {
                ddlMarca.setSelection(i);
                break;
            }
        }
    }

    private void LookForItemModelo(String modelo) {
        int aux = 0;
        for (int i = 0; i < ddlModelo.getCount(); i++) {
            if (Utils.CleanStr(ddlModelo.getItemAtPosition(i).toString()).equals(Utils.CleanStr(modelo))) {
                aux = i;
                ddlModelo.setSelection(i);
                break;
            }
        }

        ddlModelo.invalidate();
        ddlModelo.setSelection(aux);
    }

    private void LookForItemAno(String ano) {
        for (int i = 0; i < ddlAno.getCount(); i++) {
            if (Utils.CleanStr(ddlAno.getItemAtPosition(i).toString()).equals(Utils.CleanStr(ano))) {
                ddlAno.setSelection(i);
                break;
            }
        }
    }

    private void LookForItemCor(String cor) {
        for (int i = 0; i < ddlCor.getCount(); i++) {
            if (Utils.CleanStr(ddlCor.getItemAtPosition(i).toString()).equals(Utils.CleanStr(cor))) {
                ddlCor.setSelection(i);
                break;
            }
        }
    }

    private void LookForItemCobertura(String cobertura) {
        for (int i = 0; i < ddlCobertura.getCount(); i++) {
            if (Utils.CleanStr(ddlCobertura.getItemAtPosition(i).toString()).equals(Utils.CleanStr(cobertura))) {
                ddlCobertura.setSelection(i);
                break;
            }
        }
    }

    private String validate() {
        String errors = "";

        if (ddlMarca.getSelectedItemPosition() <= 0 ||
                ddlModelo.getSelectedItemPosition() <= 0 ||
                ddlCor.getSelectedItemPosition() <= 0 ||
                ddlAno.getSelectedItemPosition() <= 0 ||
                ddlCobertura.getSelectedItemPosition() <= 0
                ) {
            errors += "* Preencha todos os campos!\n";
        }

        return errors;
    }

    private void SaveVehicle() {
        Vehicle newV = Globals.getInstance().loggedVehicle;
        newV.setVei_marca(ddlMarca.getSelectedItem().toString());
        newV.setVei_modelo(ddlModelo.getSelectedItem().toString());
        newV.setVei_ano(ddlAno.getSelectedItem().toString());
        newV.setVei_cor(ddlCor.getSelectedItem().toString());
        newV.setVei_cobertura(ddlCobertura.getSelectedItem().toString());

        if (newV.getVei_marca().length() > 0 &&
                newV.getVei_modelo().length() > 0 &&
                newV.getVei_ano().length() > 0 &&
                newV.getVei_cor().length() > 0 &&
                newV.getVei_cobertura().length() > 0 &&
                newV.getVei_okfotoa() > 0 &&
                newV.getVei_okfotob() > 0 &&
                newV.getVei_okfotoc() > 0
                )
            newV.setVei_status(1);

        Api.getInstance().SaveVehicle(handlerSave, newV);

    }
}
