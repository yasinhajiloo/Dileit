package com.example.dileit.viewmodel.vminterface;

import com.example.dileit.model.entity.WordHistory;

import java.util.List;

public interface InternalInterface {
    void onListRecived(List<WordHistory> wordHistories);
}