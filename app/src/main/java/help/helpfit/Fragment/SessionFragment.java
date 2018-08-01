package help.helpfit.Fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import help.helpfit.Fragment.FragmentSession.ViewPagerAdapter;
import help.helpfit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionFragment extends Fragment {

    private TabLayout theTab;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;


    public SessionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, container, false);
        // Inflate the layout for this fragment

        theTab = view.findViewById(R.id.SessionTab);
        viewPager = view.findViewById(R.id.sessionViewPager);

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        theTab.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(theTab));

        return view;
    }

}
