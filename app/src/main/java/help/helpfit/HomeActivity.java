package help.helpfit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import help.helpfit.Fragment.AccountFragment;
import help.helpfit.Fragment.HomeFragment;
import help.helpfit.Fragment.SessionFragment;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private BottomNavigationView bottomNav;

    private HomeFragment homeFrag;
    private SessionFragment sessionFrag;
    private AccountFragment accountFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Home");

        bottomNav = findViewById(R.id.mainBottomNav);

        //init fragment

        homeFrag = new HomeFragment();
        sessionFrag = new SessionFragment();
        accountFrag = new AccountFragment();
        replaceFragment(homeFrag);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Home_menu:
                        replaceFragment(homeFrag);
                        getSupportActionBar().setTitle("Home");
                        return true;
                    case R.id.Session_menu:
                        replaceFragment(sessionFrag);
                        getSupportActionBar().setTitle("Session");
                        return true;
                    case R.id.Account_menu:
                        replaceFragment(accountFrag);
                        getSupportActionBar().setTitle("Account");
                        return true;
                        default:
                            return false;
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragementTransaction = getSupportFragmentManager().beginTransaction();
        fragementTransaction.replace(R.id.main_container, fragment);
        fragementTransaction.commit();
    }
}
