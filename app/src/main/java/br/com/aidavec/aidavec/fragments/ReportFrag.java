package br.com.aidavec.aidavec.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.adapters.PageReportAdapter;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.helpers.YearXAxisFormatter;
import br.com.aidavec.aidavec.models.ChartSemanal;
import br.com.aidavec.aidavec.models.ReportData;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class ReportFrag extends Fragment  {

//    BarChart chart;
//    public static List<ChartSemanal> listObjChart;
    public static List<ReportData> listObjData;

    TextView lblTotalPontos;
    TextView lblTotalPontosCampanha;
    TextView lblDia;
    TextView lblSemana;
    TextView lblMes;
    ViewPager vpPager;

    FragmentStatePagerAdapter adapterViewPager;

/*    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadChart();
        }
    };
*/
    Handler handlerData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_report, container, false);

        try {
            lblTotalPontos = (TextView) v.findViewById(R.id.lblTotalPontos);
            lblTotalPontosCampanha = (TextView) v.findViewById(R.id.lblTotalPontosCampanha);
            lblDia = (TextView) v.findViewById(R.id.lblDia);
            lblSemana = (TextView) v.findViewById(R.id.lblSemana);
            lblMes = (TextView) v.findViewById(R.id.lblMes);

/*            chart = (BarChart) v.findViewById(R.id.chart);
            chart.getDescription().setEnabled(false);

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            chart.setMaxVisibleValueCount(60);

            // scaling can now only be done on x- and y-axis separately
            chart.setPinchZoom(false);

            chart.setDrawBarShadow(false);
            chart.setDrawGridBackground(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setValueFormatter(new YearXAxisFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return ds[(int) value % ds.length];
                }
            });

            chart.getAxisLeft().setDrawGridLines(false);
*/
            if (!br.com.aidavec.aidavec.helpers.Utils.verificaConexao()) {
                lblTotalPontos.setText(String.format("%.2f", Globals.getInstance().savedPontos / 1000));
                lblTotalPontosCampanha.setText(String.format("%.2f", Globals.getInstance().savedPontosCampanha / 1000));
                lblDia.setText(String.format("%.2f", Globals.getInstance().savedDia / 1000));
                lblSemana.setText(String.format("%.2f", Globals.getInstance().savedSemana / 1000));
                lblMes.setText(String.format("%.2f", Globals.getInstance().savedMes / 1000));

            } else {
                Api.getInstance().GetReport(handlerData);
//                Api.getInstance().GetChartSemanal(handler);
            }



            vpPager = (ViewPager) v.findViewById(R.id.vpPager);
            if (adapterViewPager == null) {
                adapterViewPager = new PageReportAdapter(getActivity().getSupportFragmentManager(), vpPager, getContext());
                vpPager.setAdapter(adapterViewPager);
            } else {
                adapterViewPager.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Utils.getInstance().saveLog("ReportFrag - onCreateView", e.getMessage());
        }

        return v;
    }



    private void loadData() {
        try {
            Globals.getInstance().savedPontos = listObjData.get(0).getTotalPontos();
            Globals.getInstance().savedPontosCampanha = listObjData.get(0).getTotalPontosCampanha();
            Globals.getInstance().savedDia = listObjData.get(0).getKmDia();
            Globals.getInstance().savedSemana = listObjData.get(0).getKmSemana();
            Globals.getInstance().savedMes = listObjData.get(0).getKmMes();
            lblTotalPontos.setText(String.format("%.2f", listObjData.get(0).getTotalPontos() / 1000));
            lblTotalPontosCampanha.setText(String.format("%.2f", listObjData.get(0).getTotalPontosCampanha() / 1000));
            lblDia.setText(String.format("%.2f", listObjData.get(0).getKmDia() / 1000));
            lblSemana.setText(String.format("%.2f", listObjData.get(0).getKmSemana() / 1000));
            lblMes.setText(String.format("%.2f", listObjData.get(0).getKmMes() / 1000));
        } catch (Exception e) {
            Utils.getInstance().saveLog("ReportFrag - loadData", e.getMessage());
        }
    }

/*    private void loadChart() {
        try {
            ArrayList<BarEntry> ys = new ArrayList<BarEntry>();

            ys.add(new BarEntry(0, ((float) listObjChart.get(0).getSegunda()) / 1000));
            ys.add(new BarEntry(1, ((float) listObjChart.get(0).getTerca()) / 1000));
            ys.add(new BarEntry(2, ((float) listObjChart.get(0).getQuarta()) / 1000));
            ys.add(new BarEntry(3, ((float) listObjChart.get(0).getQuinta()) / 1000));
            ys.add(new BarEntry(4, ((float) listObjChart.get(0).getSexta()) / 1000));
            ys.add(new BarEntry(5, ((float) listObjChart.get(0).getSabado()) / 1000));
            ys.add(new BarEntry(6, ((float) listObjChart.get(0).getDomingo()) / 1000));

            BarDataSet set = new BarDataSet(ys, "Data Set");

            set.setColors(ColorTemplate.getHoloBlue());
            set.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set);

            BarData data = new BarData(dataSets);
            chart.setData(data);
            chart.setFitBars(true);

            // add a nice and smooth animation
            chart.animateY(2500);

            chart.getLegend().setEnabled(false);
        } catch (Exception e) {
            Utils.getInstance().saveLog("ReportFrag - loadChart", e.getMessage());
        }
    }
*/

}
