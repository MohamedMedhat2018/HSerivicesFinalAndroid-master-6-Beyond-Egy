package com.ahmed.homeservices.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface OnLoggedFromAnotherDeviec {

    void onLoggedFromAnotherDeviec(boolean isLoggedFromAnotherDevice, DataSnapshot  dataSnapshot);

}
