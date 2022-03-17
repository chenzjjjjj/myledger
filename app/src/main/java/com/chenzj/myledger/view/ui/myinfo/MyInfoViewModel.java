package com.chenzj.myledger.view.ui.myinfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.chenzj.myledger.model.User;

public class MyInfoViewModel extends ViewModel {

    private MutableLiveData<User> userMLData;

    public MutableLiveData<User> getUserMLData() {
        if (userMLData == null){
            userMLData = new MutableLiveData<>();
            userMLData.setValue(new User());
        }
        return userMLData;
    }
}