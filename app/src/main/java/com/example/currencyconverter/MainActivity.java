package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//INITIALISER LES ELEMENTS D'interface utilisateur
    TextView convertFromDropDownTextView, convertToDropDownTextView, conversionRateText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog;
    Dialog toDialog;
    Button convertButton;
    String convertFromValue, convertToValue, conversionValue;
    String[] country = {"USD",  "EUR",  "GBP",  "JPY",  "CNY",  "CHF",  "CAD",  "AUD",  "NZD",
            "SEK",  "NOK",  "DKK",  "SGD",  "HKD",  "KRW",  "TWD",  "MAD",  "RUB",  "INR",  "BRL"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Récupérer les IDs des éléments UI
        convertFromDropDownTextView = findViewById(R.id.convert_from_dropdown_menu);
        convertToDropDownTextView = findViewById(R.id.convert_to_dropdown_menu);
        conversionRateText = findViewById(R.id.conversionRateText);
        amountToConvert = findViewById(R.id.amountToConvertValueEditText);
        convertButton = findViewById(R.id.conversionButton);
        //une liste de monnaies préalablement définie
        arrayList = new ArrayList<>();
        for(String i : country){
            arrayList.add(i);
        }
        //impléménter le listener de séléction du monnaie à convertir
        convertFromDropDownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Créer une nouvelle boîte de dialogue et définir sa vue de contenu sur la mise en page de spinner
                fromDialog = new Dialog(MainActivity.this);
                fromDialog.setContentView(R.layout.from_spinner);
                fromDialog.getWindow().setLayout(650,800);
                fromDialog.show();

                //récupérer les IDs de text à éditer et la list view
                EditText editText = fromDialog.findViewById(R.id.edit_text);
                ListView listView = fromDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convertFromDropDownTextView.setText(adapter.getItem(position));
                        fromDialog.dismiss();
                        convertFromValue = adapter.getItem(position);
                    }
                });
            }
        });

        convertToDropDownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on refait la même chose avec fromDialog
                toDialog = new Dialog(MainActivity.this);
                toDialog.setContentView(R.layout.to_spinner);
                toDialog.getWindow().setLayout(650,800);
                toDialog.show();

                //EditText is the search bar
                EditText editText = toDialog.findViewById(R.id.edit_text);
                ListView listView = toDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convertToDropDownTextView.setText(adapter.getItem(position));
                        toDialog.dismiss();
                        convertToValue = adapter.getItem(position);
                    }
                });
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double amountToConvert = Double.valueOf(MainActivity.this.amountToConvert.getText().toString());

                    getConversionRate(convertFromValue, convertToValue, amountToConvert);
                }catch (Exception e){

                }
            }
        });

    }
    //la fonction qui fait à la fois la connexion à l'API et la converion de monnaie
    public String getConversionRate(String convertFrom, String convertTo, Double amountToConvert){
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.exchangerate.host/convert?from="+convertFrom+"&to="+convertTo;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Double conversionRateValue = (Double) jsonObject.get("result");
                    conversionValue = "" + round((conversionRateValue*amountToConvert), 2);
                    conversionRateText.setText(conversionValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
        return null;
    }

    public static double round(double value, int places){
        if(places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}