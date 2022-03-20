package com.chenzj.myledger.view.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.chenzj.myledger.model.MonthTotal;
import com.chenzj.myledger.model.User;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<MonthTotal> MonthTotalMLData;

    public MutableLiveData<MonthTotal> getMonthTotalMLData() {
        if (MonthTotalMLData == null){
            MonthTotalMLData = new MutableLiveData<>();
            MonthTotalMLData.setValue(new MonthTotal());
        }
        return MonthTotalMLData;
    }
}