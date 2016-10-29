package com.example.matei.lab_android;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import static com.example.matei.lab_android.DatabaseHandler.DATABASE_NAME;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonCreateInstrument = (Button) findViewById(R.id.buttonCreateInstrument);
        buttonCreateInstrument.setOnClickListener(new OnClickListenerCreateInstrument());
        readRecords();
        //getApplicationContext().deleteDatabase(DATABASE_NAME);
    }

    public void readRecords(){

        TableLayout tableLayout = (TableLayout) findViewById(R.id.display_table);

        List<Instrument> instruments = new TableControllerInstrument(this).read();

        if(instruments.size() > 0){

            for(Instrument instr : instruments){

                TableRow row = new TableRow(this);

                int id = instr.id;
                String name = instr.name;
                String type = instr.type;

                String textViewContents = "Name : " + name + " - Type : " + type;

                TextView textViewInstrumentItem = new TextView(this);
                textViewInstrumentItem.setPadding(0, 20, 0, 20);
                textViewInstrumentItem.setText(textViewContents);
                textViewInstrumentItem.setTag(Integer.toString(id));

                Button button = new Button(this);
                button.setText("Delete record");
                button.setId(id);
                button.setOnClickListener((View.OnClickListener) new OnClickListenerDeleteInstrument());

                row.addView(textViewInstrumentItem);
                row.addView(button);

                tableLayout.addView(row,id);
            }
        }
    }
}
