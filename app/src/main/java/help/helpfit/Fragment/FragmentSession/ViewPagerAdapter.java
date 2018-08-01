package help.helpfit.Fragment.FragmentSession;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import help.helpfit.Common.Common;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    //private final List<Fragment> fragmentList = new ArrayList<>();
    //private final List<String> fragmentTitle = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                CreatedFragment createdFragment = new CreatedFragment();
                return createdFragment;
            case 1:
                OnGoingFragment onGoingFragment = new OnGoingFragment();
                return onGoingFragment;
            case 2:
                HistoryFragment historyFragment = new HistoryFragment();
                return historyFragment;

                default:
                    CreatedFragment createdFragment2 = new CreatedFragment();
                    return createdFragment2;
        }
        //return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        //return fragmentTitle.size();
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //return fragmentTitle.get(position);
        switch (position){
            case 0:
                if(Common.currentUser.getType().equalsIgnoreCase("Trainer")){
                    return "Created";
                }
                else {
                    return "Joined";
                }
            case 1:
                return "On Going";
            case 2:
                return "History";
        }
        return null;
    }

}
