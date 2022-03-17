package com.chenzj.myledger.view;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.chenzj.myledger.dao.CacheDao;
import com.chenzj.myledger.dao.UserDao;
import com.chenzj.myledger.R;
import com.chenzj.myledger.common.Constant;
import com.chenzj.myledger.model.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //
        EditText userName = this.findViewById(R.id.UserNameEdit);
        EditText password = this.findViewById(R.id.PassWordEdit);
        Button loginBtn = this.findViewById(R.id.LoginButton);
        Button signUpBtn = this.findViewById(R.id.SignUpButton);
        UserDao userDao = new UserDao(LoginActivity.this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUserName = userName.getText().toString().trim();
                String strPassWord = password.getText().toString().trim();
                User user = userDao.findUserByAccount(strUserName);
                if (user == null){
                    Toast.makeText(LoginActivity.this,"账号不存在！",Toast.LENGTH_SHORT).show();
                }
                else if ( strPassWord.equals(user.getPassword()) ){
                    Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                    CacheDao cacheDao = new CacheDao(LoginActivity.this);
                    cacheDao.set(Constant.CURRENT_USER, strUserName); //记录当前用户
                    Intent intent = new Intent(LoginActivity.this, LedgerActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish(); //关闭登录页面
                } else {
                    Toast.makeText(LoginActivity.this,"用户名或者密码不正确！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

}