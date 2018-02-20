package br.com.aidavec.aidavec.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import br.com.aidavec.aidavec.fragments.ReportMonthFrag;
import br.com.aidavec.aidavec.fragments.ReportWeekFrag;

/**
 * Created by leonardo.saganski on 27/02/17.
 */

public class PageReportAdapter extends FragmentStatePagerAdapter implements ReportMonthFrag.iClick, ReportWeekFrag.iClick {
    private static int NUM_ITEMS = 2;
    private ViewPager vp;
    Context context;

    public PageReportAdapter(FragmentManager fragmentManager, ViewPager vp, Context context) {
        super(fragmentManager);
        this.vp = vp;
        this.context = context;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ReportMonthFrag.newInstance(this);
            case 1:
                return ReportWeekFrag.newInstance(this);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Mensal";
            case 1:
                return "Semanal";
            default:
                return "";
        }
    }


    @Override
    public void callPage(int page) {
        vp.setCurrentItem(page);
    }
}