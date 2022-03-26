package com.chenzj.myledger.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import com.chenzj.myledger.dao.CacheDao;
import com.chenzj.myledger.R;
import androidx.appcompat.app.AppCompatActivity;
import com.chenzj.myledger.common.Constant;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.thread.DataInitThread;
import com.chenzj.myledger.utils.ExcelUtils;
import com.chenzj.myledger.utils.StringUtils;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CacheDao cacheDao = new CacheDao(this);
        if (StringUtils.isBlank(cacheDao.get(Constant.IS_INIT))) {
            new DataInitThread(this).start();
        }
        String account = cacheDao.get(Constant.CURRENT_USER);
        Button button = findViewById(R.id.button_first);
        if (StringUtils.isNotBlank(account)){ // 如果存在则表示用户已登录
            button.setVisibility(View.GONE); //隐藏按钮
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, LedgerActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();

                }
            }, 400);
        }else {
            ExcelUtils.setFilePath(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
            button.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}