package br.com.aidavec.aidavec.helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;

/**
 * Created by Leonardo Saganski on 14/09/15.
 */
public class YearXAxisFormatter implements IAxisValueFormatter
{

    public String[] ms = new String[]{
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    public static String[] ns = new String[]{
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
    };

    public String[] ds = new String[]{
            "SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM"
    };

    public YearXAxisFormatter() {
        // maybe do something here or provide parameters in constructor

        Calendar cal = Calendar.getInstance();
        int m = cal.get(Calendar.MONTH);

        while (m < 11) {
            ms = new String[]{ms[11],ms[0],ms[1],ms[2],ms[3],ms[4],ms[5],ms[6],ms[7],ms[8],ms[9],ms[10]};
            ns = new String[]{ns[11],ns[0],ns[1],ns[2],ns[3],ns[4],ns[5],ns[6],ns[7],ns[8],ns[9],ns[10]};

            m++;
        }
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        float percent = value / axis.mAxisRange;
        return ms[(int) (ms.length * percent)];
    }
}
