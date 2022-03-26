package com.chenzj.myledger.view;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import com.chenzj.myledger.R;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.Ledger;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.utils.ExcelUtils;
import com.chenzj.myledger.utils.PermissionUtils;
import com.chenzj.myledger.view.ui.component.HeaderBarView;
import com.chenzj.myledger.view.ui.myinfo.MyInfoViewModel;

import java.util.List;

public class ExportAndImportActivity extends AppCompatActivity {
    private Button exportBtn;
    private LedgerDao ledgerDao;
    private MyInfoViewModel myInfoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_and_import);
        exportBtn = findViewById(R.id.btn_export);
        ledgerDao = new LedgerDao(this);
        myInfoViewModel = new ViewModelProvider(this).get(MyInfoViewModel.class);
        User user = myInfoViewModel.getUserMLData().getValue();
        //获取文件写入权限
        PermissionUtils.checkPermission(this);
//        PermissionUtils.isGrantExternalRW(this);
        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        HeaderBarView headerBarView = findViewById(R.id.title_bar);
        headerBarView.setOnViewClick(new HeaderBarView.onViewClick() {
            @Override
            public void leftClick(View view) {
                ExportAndImportActivity.this.finish();
            }

            @Override
            public void rightClick(View view) {

            }
        });

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Ledger> ledgers = ledgerDao.findAllLedgers(user.getUserId());
                boolean isSuccess = ExcelUtils.exportExcel(ledgers);

                if (isSuccess) {
                    Toast.makeText(ExportAndImportActivity.this, "导出成功:/storage/emulated/0/Download/", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ExportAndImportActivity.this, "导出失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}