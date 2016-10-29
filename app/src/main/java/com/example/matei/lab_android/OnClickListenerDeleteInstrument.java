package com.example.matei.lab_android;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

/**
 * Created by matei on 10/29/2016.
 */

public class OnClickListenerDeleteInstrument implements View.OnClickListener{

    @Override
    public void onClick(View view){
        final Context context = view.getContext();
        TableControllerInstrument tbi = new TableControllerInstrument(context);
        tbi.delete(view.getId());
    }
}
