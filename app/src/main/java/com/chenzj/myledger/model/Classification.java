package com.chenzj.myledger.model;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/2/19 17:04
 */
public class Classification {
    private int classify_id ;
    private String classify_name;
    private int is_use;
    private int type;

    public int getClassify_id() {
        return classify_id;
    }

    public void setClassify_id(int classify_id) {
        this.classify_id = classify_id;
    }

    public String getClassify_name() {
        return classify_name;
    }

    public void setClassify_name(String classify_name) {
        this.classify_name = classify_name;
    }

    public int getIs_use() {
        return is_use;
    }

    public void setIs_use(int is_use) {
        this.is_use = is_use;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
