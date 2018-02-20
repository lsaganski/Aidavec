package br.com.aidavec.aidavec.fragments;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.controls.Ponteiro;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.core.Parameters;
import br.com.aidavec.aidavec.helpers.MyMarkerView;
import br.com.aidavec.aidavec.helpers.YearXAxisFormatter;
import br.com.aidavec.aidavec.models.ChartHome;
import br.com.aidavec.aidavec.models.ReportData;
import br.com.aidavec.aidavec.models.Waypoint;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class HomeFrag extends Fragment {

    ReportData data;

    TextView lblTotalDIstancia;
    TextView lblTotalPontos;
    TextView lblMotion;
    TextView lblValidMotion;
    ImageView imgGreenCar;
    ImageView imgYellowCar;
    ImageView imgRedCar;
    Button btnHelp;

    LinearLayout llMotion;
    LinearLayout llValidMotion;

    FrameLayout flPonteiro;
    Ponteiro imgPonteiro;

    String msgHelp = "";
    String msgTitle = "";

//    BarChart mChart;
//    public static List<ChartHome> listObjChart;
//    int qtdBars = 8;

    final Handler handlerRefresh = new Handler();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadData();
        }
    };

    Handler handlerVei = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadVei();
        }
    };

/*    Handler handlerChart = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadChartData();
        }
    };
*/
    Handler handlerUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 101)
                Api.getInstance().GetReport(handler); // Atualizar o total de pontos do servidor
            else if (msg.what == 102)
                RefreshMotion();
            else if (msg.what == 103)
                RefreshValidMotion();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_home, container, false);

        Globals.getInstance().handlerUIHome = handlerUI;

        br.com.aidavec.aidavec.helpers.Utils.getInstance().verifyWIFI();

        lblTotalDIstancia = (TextView) v.findViewById(R.id.lblDistancia);
        lblTotalPontos = (TextView) v.findViewById(R.id.lblPontos);
        lblMotion = (TextView) v.findViewById(R.id.lblMotion);
        lblValidMotion = (TextView) v.findViewById(R.id.lblValidMotion);

        llMotion = (LinearLayout) v.findViewById(R.id.llMotion);
        llValidMotion = (LinearLayout) v.findViewById(R.id.llValidMotion);

        llMotion.setVisibility(Globals.getInstance().devMode ? View.VISIBLE : View.GONE);
        llValidMotion.setVisibility(Globals.getInstance().devMode ? View.VISIBLE : View.GONE);

        imgGreenCar = (ImageView) v.findViewById(R.id.imgGreenCar);
        imgYellowCar = (ImageView) v.findViewById(R.id.imgYellowCar);
        imgRedCar = (ImageView) v.findViewById(R.id.imgRedCar);
        imgPonteiro = (Ponteiro) v.findViewById(R.id.imgPonteiro);
//        mChart = (BarChart) v.findViewById(R.id.chart);
        flPonteiro = (FrameLayout) v.findViewById(R.id.flPonteiro);
        btnHelp = (Button) v.findViewById(R.id.btnHelp);

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_scale);
        btnHelp.startAnimation(anim);

//        loadChart();

        if (!br.com.aidavec.aidavec.helpers.Utils.verificaConexao()) {
            lblTotalPontos.setText(String.format("%.2f", Globals.getInstance().savedPontos / 1000));
            lblTotalDIstancia.setText(String.format("%.2f", Globals.getInstance().savedDistancia / 1000));
        } else {
            Api.getInstance().GetReport(handler);
        }



        return v;
    }

/*    private void loadChart() {
        try {
            mChart.setDrawGridBackground(false);
            mChart.getDescription().setEnabled(false);
            mChart.setDrawValueAboveBar(true);
            mChart.setMaxVisibleValueCount(qtdBars);
            mChart.setPinchZoom(false);
            mChart.setDrawBarShadow(false);
            mChart.setAutoScaleMinMaxEnabled(true);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setDrawGridLines(false);
            xAxis.setTextSize(10f);
            xAxis.setValueFormatter(new YearXAxisFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return ms[((int) value % ms.length) + 4];
                }
            });


            YAxis leftAxis = mChart.getAxisLeft();

            Api.getInstance().GetChartHome(handlerChart);

            mChart.animateX(2500);

            Legend l = mChart.getLegend();
            l.setForm(Legend.LegendForm.LINE);
        } catch (Exception e) {
            br.com.aidavec.aidavec.helpers.Utils.getInstance().saveLog("HomeFrag - loadChart", e.getMessage());
        }
    }
*/
    private void RefreshMotion() {
        lblMotion.setText(Globals.getInstance().lastMotion);
    }

    private void RefreshValidMotion() {
        lblValidMotion.setText(Globals.getInstance().lastValidMotion);
    }

/*    private void loadChartData() {
        try {
            ArrayList<BarEntry> values = new ArrayList<BarEntry>();

            for (int i = 0; i < qtdBars; i++) {

                float val = (float) listObjChart.get(0).getVals()[i]; //Integer.valueOf(YearXAxisFormatter.ns[i+(12-qtdBars)])-1];
                values.add(new BarEntry(i, val / 1000));
            }

            BarDataSet set1;

            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                set1 = new BarDataSet(values, "Data Set");
                set1.setValues(values);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(values, "Km percorridos nos últimos meses");

                set1.setValueTextSize(12f);
                set1.setFormLineWidth(1f);
                set1.setColors(ColorTemplate.getHoloBlue());

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1); // add the datasets

                // create a data object with the datasets
                BarData data = new BarData(dataSets);

                // set data
                mChart.setData(data);
            }
        } catch (Exception e) {
            br.com.aidavec.aidavec.helpers.Utils.getInstance().saveLog("HomeFrag - loadChartData", e.getMessage());

        }
    }
*/
    private void loadData() {
        try {
            Api.getInstance().GetVehicle(handlerVei);

            refreshDistance();

            new Thread() {
                @Override
                public void run() {
                    repeat();
                }

                public void repeat() {
                    handlerRefresh.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Globals.getInstance().loggedUser != null) {
                                refreshDistance();
                                repeat();
                            }
                        }
                    }, Parameters.REFRESH_HOME_UI_INTERVAL);

                }
            }.start();
        } catch (Exception e) {
            if (Globals.getInstance().loggedUser != null)
                br.com.aidavec.aidavec.helpers.Utils.getInstance().saveLog("HomeFrag - loadData", e.getMessage());

        }
    }

    private void refreshDistance() {
        try {
            data = ReportFrag.listObjData.get(0);
            double val = data.getTotalPontos();
            val += Globals.getInstance().db.getSumDistance();
            //data.setTotalPontos(val);

            Globals.getInstance().savedPontos = val;
            Globals.getInstance().savedDistancia = val;

            lblTotalDIstancia.setText(String.format("%.2f", val / 1000));
            lblTotalPontos.setText(String.format("%.2f", val / 1000));
        } catch (Exception e) {
            if (Globals.getInstance().loggedUser != null)
                br.com.aidavec.aidavec.helpers.Utils.getInstance().saveLog("HomeFrag - refreshDistance", e.getMessage());

        }
    }

    private void loadVei() {
        try {
            msgTitle = "VEÍCULO NÃO VALIDADO";
            msgHelp = "Para estar apto à participar de uma campanha é necessário primeiramente validar seu veículo. Vá em configurações->veículos, selecione a marca, modelo, ano e cor do seu veículo e também o nível de cobertura desejada de anúncio. Selecione as imagens e documento e clique em enviar. \n" +
                    " \n" +
                    "Qualquer dúvida entre em contato com  nossa equipe pelo e-mail: suporte@aidavec.com.br.";

            if (Globals.getInstance().loggedVehicle != null) {
                if (Globals.getInstance().loggedVehicle.getVei_status() == 0) {
                    imgGreenCar.setVisibility(View.GONE);
                    imgYellowCar.setVisibility(View.GONE);
                    imgRedCar.setVisibility(View.VISIBLE);
                } else {
                    if (data.getTotalPontos() > Parameters.MIN_DISTANCE_TO_VALID_VEHICLE) {
                        imgGreenCar.setVisibility(View.VISIBLE);
                        imgYellowCar.setVisibility(View.GONE);
                        imgRedCar.setVisibility(View.GONE);

                        msgTitle = "VEÍCULO APTO";
                        msgHelp = "Parabéns, você já pode participar das campanhas. Fique atento ao menu notificações. Assim que for selecionado, você receberá as informações sobre a campanha no aplicativo e poderá responder se aceita a campanha proposta.  \n" +
                                " \n" +
                                "Qualquer dúvida entre em contato com  nossa equipe pelo e-mail: suporte@aidavec.com.br.";
                    } else {
                        imgGreenCar.setVisibility(View.GONE);
                        imgYellowCar.setVisibility(View.VISIBLE);
                        imgRedCar.setVisibility(View.GONE);

                        msgTitle = "CONTINUE DIRIGINDO";
                        msgHelp = "Recebemos as informações do seu veículo, continue dirigindo. Em breve, ao atingir 200km percorridos, seu veículo mudará para o estado verde (apto). \n" +
                                " \n" +
                                "Qualquer dúvida entre em contato com  nossa equipe pelo e-mail: suporte@aidavec.com.br.";
                    }
                }
            } else {
                imgGreenCar.setVisibility(View.GONE);
                imgYellowCar.setVisibility(View.GONE);
                imgRedCar.setVisibility(View.VISIBLE);
            }

            btnHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    br.com.aidavec.aidavec.helpers.Utils.getInstance().getYesNoWithExecutionStop(msgTitle, msgHelp, getContext());
                }
            });

//            if (data.getTotalPontos() > Parameters.MIN_DISTANCE_TO_VALID_VEHICLE) {
//                flPonteiro.setVisibility(View.GONE);
//                mChart.setVisibility(View.VISIBLE);
//            } else {
                flPonteiro.setVisibility(View.VISIBLE);
//                mChart.setVisibility(View.GONE);

                double val = data.getTotalPontos();
                val += Globals.getInstance().db.getSumDistance();

                imgPonteiro.pontos = ((float) val) / 1000;
                imgPonteiro.animatePonteiro();
//            }
        } catch (Exception e) {
            br.com.aidavec.aidavec.helpers.Utils.getInstance().saveLog("HomeFrag - loadVei", e.getMessage());

        }

    }
}


