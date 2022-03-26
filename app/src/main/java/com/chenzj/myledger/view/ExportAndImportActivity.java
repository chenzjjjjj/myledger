package com.chenzj.myledger.view;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;
import com.chenzj.myledger.R;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.Ledger;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.utils.ExcelUtils;
import com.chenzj.myledger.utils.PermissionUtils;
import com.chenzj.myledger.view.ui.component.HeaderBarView;
import com.chenzj.myledger.view.ui.myinfo.MyInfoViewModel;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ExportAndImportActivity extends AppCompatActivity {
    private Button exportBtn;
    private Button importBtn;
    private LedgerDao ledgerDao;
    private MyInfoViewModel myInfoViewModel;
    private int REQUEST_CODE_EXCEL = 1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_and_import);
        exportBtn = findViewById(R.id.btn_export);
        importBtn = findViewById(R.id.btn_import);
        ledgerDao = new LedgerDao(this);
        myInfoViewModel = new ViewModelProvider(this).get(MyInfoViewModel.class);
        user = myInfoViewModel.getUserMLData().getValue();
        //获取文件写入权限
        PermissionUtils.checkPermission(this);
//        PermissionUtils.isGrantExternalRW(this);

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

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemFile();
            }
        });
    }

    public void openSystemFile() {
        // .xls & .xlsx
        String[] mimeTypes = {"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // 类型设置为excel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件"), REQUEST_CODE_EXCEL);
        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
            Toast.makeText(ExportAndImportActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_CODE_EXCEL
                && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    DocumentFile documentFile = DocumentFile.fromSingleUri(ExportAndImportActivity.this, uri);
                    String fileName = documentFile.getName();
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    List<Ledger> ledgerList = ExcelUtils.importExcel(inputStream, fileName);
                    ledgerDao.addPatchLedger(ledgerList, user.getUserId());
                    Toast.makeText(ExportAndImportActivity.this, "导入成功！"+fileName, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}