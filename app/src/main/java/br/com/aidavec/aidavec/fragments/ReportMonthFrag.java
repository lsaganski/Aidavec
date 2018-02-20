package br.com.aidavec.aidavec.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.helpers.YearXAxisFormatter;
import br.com.aidavec.aidavec.models.ChartHome;

/**
 * Created by leonardo.saganski on 27/02/17.
 */

public class ReportMonthFrag extends Fragment {

    BarChart mChart;
    public static List<ChartHome> listObjChart;
    int qtdBars = 8;
    Button btnRight;
    private iClick listener;
    Object parent;

    Handler handlerChart = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadChartData();
        }
    };

    public static ReportMonthFrag newInstance(Object parent) {
        ReportMonthFrag frag = new ReportMonthFrag();
        //Bundle args = new Bundle();
        //args.putInt("someInt", page);
        //args.putString("someTitle", title);
        //fragmentFirst.setArguments(args);
        frag.parent = parent;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_report_month, container, false);

        mChart = (BarChart) v.findViewById(R.id.chart);
        btnRight = (Button) v.findViewById(R.id.btnRight);

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.callPage(1);
            }
        });

        loadChart();

        return v;
    }

    private void loadChart() {
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

        } catch (Exception e) {
            br.com.aidavec.aidavec.helpers.Utils.getInstance().saveLog("HomeFrag - loadChart", e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Api.getInstance().GetChartHome(handlerChart);
    }

    private void loadChartData() {
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

            mChart.animateX(2500);

            Legend l = mChart.getLegend();
            l.setForm(Legend.LegendForm.LINE);

        } catch (Exception e) {
            br.com.aidavec.aidavec.helpers.Utils.getInstance().saveLog("HomeFrag - loadChartData", e.getMessage());

        }
    }

    public interface iClick {
        public void callPage(int page);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.parent instanceof iClick) {
            listener = (iClick) this.parent;
        } else {
            throw new ClassCastException();
        }
    }
}
