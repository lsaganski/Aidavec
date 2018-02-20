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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.core.Api;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.helpers.YearXAxisFormatter;
import br.com.aidavec.aidavec.models.ChartSemanal;

/**
 * Created by leonardo.saganski on 27/02/17.
 */

public class ReportWeekFrag extends Fragment {

    BarChart chart;
    public static List<ChartSemanal> listObjChart;
    Button btnLeft;
    private iClick listener;
    Object parent;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadChart();
        }
    };

    public static ReportWeekFrag newInstance(Object parent) {
        ReportWeekFrag frag = new ReportWeekFrag();
        //Bundle args = new Bundle();
        //args.putInt("someInt", page);
        //args.putString("someTitle", title);
        //fragmentFirst.setArguments(args);
        frag.parent = parent;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_report_week, container, false);

        chart = (BarChart) v.findViewById(R.id.chart);
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

        btnLeft = (Button) v.findViewById(R.id.btnLeft);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.callPage(0);
            }
        });


        return v;
    }



    @Override
    public void onResume() {
        super.onResume();

        Api.getInstance().GetChartSemanal(handler);
    }

    private void loadChart() {
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
