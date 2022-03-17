package com.chenzj.myledger.view;

import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import com.chenzj.myledger.dao.CacheDao;
import com.chenzj.myledger.dao.DBHelper;
import com.chenzj.myledger.dao.UserDao;
import com.chenzj.myledger.R;
import com.chenzj.myledger.common.Constant;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.utils.BarUtils;
import com.chenzj.myledger.utils.Logger;
import com.chenzj.myledger.view.ui.myinfo.MyInfoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class LedgerActivity extends AppCompatActivity {
    private static final String TAG = LedgerActivity.class.getSimpleName();
    private DBHelper dbHelper;
    private MyInfoViewModel myInfoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger);

        //让状态栏变透明
        BarUtils.setTranslucentStatus(this);
//        AndroidBarUtils.setBarDarkMode(this, true);
        dbHelper = DBHelper.getInstance(getApplicationContext());
        //通过ViewModel传递数据
        myInfoViewModel = new ViewModelProvider(this).get(MyInfoViewModel.class);
        CacheDao cacheDao = new CacheDao(dbHelper);
        String account = cacheDao.get(Constant.CURRENT_USER);
        UserDao userDao = new UserDao(dbHelper);
        User user = userDao.findUserByAccount(account);
        myInfoViewModel.getUserMLData().setValue(user);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_statistics, R.id.navigation_my)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        Logger.d(TAG,"关闭数据库连接");
    }

}