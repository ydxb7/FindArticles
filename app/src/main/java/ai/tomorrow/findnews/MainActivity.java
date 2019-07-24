package ai.tomorrow.findnews;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import ai.tomorrow.findnews.ArticleDetail.ArticleDetailFragment;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
