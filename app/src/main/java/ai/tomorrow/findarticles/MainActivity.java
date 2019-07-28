package ai.tomorrow.findarticles;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import ai.tomorrow.findarticles.ArticleDetail.ArticleDetailFragment;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private Fragment mCurrentFragment;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the navController from nav_host_fragment
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // By calling NavigationUI.setupActionBarWithNavController
        NavigationUI.setupActionBarWithNavController(this, navController);

    }

    // Find the navController and then call navController.navigateUp
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

}
