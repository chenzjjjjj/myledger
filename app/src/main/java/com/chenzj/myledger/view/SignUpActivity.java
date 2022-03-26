package com.chenzj.myledger.view;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.chenzj.myledger.dao.UserDao;
import com.chenzj.myledger.R;
import com.chenzj.myledger.model.User;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EditText account =  this.findViewById(R.id.AccountEdit);
        EditText userName =  this.findViewById(R.id.UserNameEdit);
        EditText passWord =  this.findViewById(R.id.PassWordEdit);
        EditText passWordAgain = this.findViewById(R.id.PassWordAgainEdit);
        Button signUpButton =  this.findViewById(R.id.SignUpButton);
        Button backLoginButton = this.findViewById(R.id.BackLoginButton);

        backLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                //跳转新Activity
//                startActivity(intent);
                //关闭原本的Activity
                SignUpActivity.this.finish();
            }
        });

        // 立即注册按钮监听器
        signUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strAccount = account.getText().toString().trim();
                        String strUserName = userName.getText().toString().trim();
                        String strPassWord = passWord.getText().toString().trim();
                        String strPassWordAgain = passWordAgain.getText().toString().trim();
                        //注册格式粗检
                        if (strAccount.length() > 30) {
                            Toast.makeText(SignUpActivity.this, "用户名长度必须小于30！", Toast.LENGTH_SHORT).show();
                        }
                        else if (strUserName.length() > 40) {
                            Toast.makeText(SignUpActivity.this, "昵称长度必须小于40！", Toast.LENGTH_SHORT).show();
                        } else if (strAccount.length() < 4) {
                            Toast.makeText(SignUpActivity.this, "用户名长度必须大于4！", Toast.LENGTH_SHORT).show();
                        } else if (strPassWord.length() > 16) {
                            Toast.makeText(SignUpActivity.this, "密码长度必须小于16！", Toast.LENGTH_SHORT).show();
                        } else if (strPassWord.length() < 6) {
                            Toast.makeText(SignUpActivity.this, "密码长度必须大于6！", Toast.LENGTH_SHORT).show();
                        } else if (!strPassWord.equals(strPassWordAgain)) {
                            Toast.makeText(SignUpActivity.this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                        } else {
                            UserDao userDao = new UserDao(SignUpActivity.this);
                            User user = userDao.findUserByAccount(strAccount);
                            if (user == null) {
                                user = new User();
                                user.setAccount(strAccount);
                                user.setUsername(strUserName);
                                user.setPassword(strPassWord);
                                userDao.add(user);
                                Toast.makeText(SignUpActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                // 跳转到登录界面
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        SignUpActivity.this.finish();
                                    }
                                }, 1000);
                            }else {
                                Toast.makeText(SignUpActivity.this, "账号已存在！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }
}