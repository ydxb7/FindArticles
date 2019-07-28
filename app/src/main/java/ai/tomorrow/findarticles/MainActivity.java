package ai.tomorrow.findarticles;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

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

//        NavController navController= Navigation.findNavController(MainActivity.this,R.id.nav_graph);
//
//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
//                Integer fragmentId = destination.getId();
//                mCurrentFragment = getSupportFragmentManager().findFragmentById(fragmentId);
//                Log.e(TAG, "onDestinationChanged: "+ mCurrentFragment);
//            }
//        });
    }

    // Find the navController and then call navController.navigateUp
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if(mCurrentFragment instanceof ArticleDetailFragment){
//            if(ArticleDetailFragment.myOnKeyDown(keyCode, event))
//                return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            (ArticleDetailFragment) fragment.my
//
//
//            ((PastEventListFragment)fragments.get(0)).myOnKeyDown(keyCode);
//            ((EventListFragment)fragments.get(1)).myOnKeyDown(keyCode);
//
//            //and so on...
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}