package hu.weblapp.csabapark;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Kedvencek extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kedvencek);
        final ListView kedvencekLista=findViewById(R.id.kedvencekLista);
        kedvencekLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String kedvencLink=kedvencekLista.getItemAtPosition(i).toString();
                Intent kedvencIntent=new Intent(Kedvencek.this, Home.class);
                kedvencIntent.putExtra("kedvencLink", kedvencLink);
                startActivity(kedvencIntent);
            }
        });


        ArrayList<String> items=new ArrayList<>();
        try {
            File config=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"csabaparkTemp/config.txt");
            InputStreamReader fis=new InputStreamReader(new FileInputStream(config));
            BufferedReader is=new BufferedReader(fis);
            String line;
            do{
                line=is.readLine();
                if(!line.contains("Kedvencek")) {
                    if (line != null) {
                        System.out.println(line);
                        items.add(line);
                    }
                }
            }while (line!=null);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        ArrayAdapter<String> kedvencAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        kedvencekLista.setAdapter(kedvencAdapter);
    }
}
