package com.umonsoft.tabis.Interfaces;

public interface VolleyGetRecordHistory {
    void onSuccess(String user_name,int record_id,String prevdepart,String nextdepart,String description,int addingtype);
}
