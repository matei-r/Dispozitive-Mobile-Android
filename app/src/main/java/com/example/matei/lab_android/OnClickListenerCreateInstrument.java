package com.example.matei.lab_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by matei on 10/28/2016.
 */

public class OnClickListenerCreateInstrument implements View.OnClickListener {
    @Override
    public void onClick(View view){
        final Context context = view.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.instrument_input_form,null,false);
        final EditText editTextInstrumentName = (EditText) formElementsView.findViewById(R.id.editTextInstrumentName);
        final Spinner chooseInstrumentType = (Spinner) formElementsView.findViewById(R.id.chooseInstrumentType);

        Spinner spinner = (Spinner) formElementsView.findViewById(R.id.chooseInstrumentType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.type_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("Create instrument")
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id){
                                String instrumentName = editTextInstrumentName.getText().toString();
                                String instrumentType = chooseInstrumentType.getSelectedItem().toString();
                                Instrument instrument = new Instrument();
                                instrument.name = instrumentName;
                                instrument.type = instrumentType;
                                boolean createSuccessful = new TableControllerInstrument(context).create(instrument);
                                if(createSuccessful){
                                    Toast.makeText(context,"Instrument saved successfuly to database.",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(context,"Error in addind instrument to database!",Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();
                            }
                        }).show();
    }
}
