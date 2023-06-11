package group.imagestudio9000;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.os.Bundle;
import android.view.Window;

import com.google.firebase.FirebaseApp;

import group.imagestudio9000.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    HomeFragment fragHome;
    BrowseFragment fragBrowse;
    AddFragment fragAdd;
    AboutFragment fragAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        fragHome = new HomeFragment();
        fragBrowse = new BrowseFragment();
        fragAdd = new AddFragment();
        fragAbout = new AboutFragment();

        replaceFragment(fragHome);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.nav_Home:
                    replaceFragment(fragHome);
                    break;
                case R.id.nav_Browse:
                    replaceFragment(fragBrowse);
                    break;
                case R.id.nav_Add:
                    replaceFragment(fragAdd);
                    break;
                case R.id.nav_About:
                    replaceFragment(fragAbout);
                    break;
            }

            return true;

        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();
    }

}