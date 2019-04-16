package ayushkumar.chatapp.android.developer.software;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    private Toolbar mToolbar ;

    private FirebaseAuth mAuth ;

    private ViewPager myViewPager ;

    private TabLayout myTabLayout ;

    private TabsPagerAdapter myTabsPagerAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Ayush Chat");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();

        if (currentuser == null)
        {
           LogOutUser();
        }
    }

    private void LogOutUser()
    {
        Intent ayush = new Intent(MainActivity.this, StartPageActivity.class);
        ayush.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ayush);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
            super.onCreateOptionsMenu(menu);

            getMenuInflater().inflate(R.menu.main_menu, menu);

            return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
          super.onOptionsItemSelected(item);

          if (item.getItemId() == R.id.main_logout_button)
          {
              mAuth.signOut();

              LogOutUser();
          }

        if (item.getItemId() == R.id.main_all_users)
        {
            Intent Ram_intent = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(Ram_intent);
        }

          if (item.getItemId() == R.id.main_account_settings_button)
          {
              Intent Ram = new Intent(MainActivity.this, SettingsActivity.class);
              startActivity(Ram);
          }
        return true ;
    }
}
