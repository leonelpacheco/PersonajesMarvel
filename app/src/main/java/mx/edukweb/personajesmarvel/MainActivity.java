package mx.edukweb.personajesmarvel;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import mx.edukweb.personajesmarvel.Provider.JSONParser;


public class MainActivity extends ActionBarActivity {

    private ProgressDialog pDialog;

    // Clase JSONParser
    JSONParser jsonParser = new JSONParser();
    String total2;
    TextView total;
    EditText personaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        personaje = (EditText) findViewById(R.id.txtPersonaje);
        total = (TextView) findViewById(R.id.txtDescripcion);
        Button buscar = (Button) findViewById(R.id.button);


        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AttemptLogin().execute();
            }
        });

    }//Fin de clase


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Buscando datos del personaje de Marvel...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            String username = "";
            String password = "";
            String publicKey= "1069ed20c9e6d1bbfe9e941e338443a8";
            String privateKey="5dbd5703549738363063bd1a70958629f775adb9";
            long  timestamp =  System.currentTimeMillis();
            String temp = timestamp+privateKey+publicKey;
            String hash = md52(temp);
            int limit = 1;
            String URL ="http://gateway.marvel.com:80/v1/public/characters?ts="+timestamp+"name=spider-man&apikey="+publicKey+"&hash="
                    +hash;

            String personajeabuscar = personaje.getText().toString();

            String URL2 = String.format("http://gateway.marvel.com/v1/public/characters?name=%s&ts=%d&apikey=%s&hash=%s&limit=%ds",personajeabuscar, timestamp, publicKey, hash, limit);



            // La respuesta del JSON es
            String TAG_SUCCESS = "success";
            String TAG_MESSAGE = "message";

            //cript(password);
            try {

                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                Log.d("checar el cifrado!", password);
                Log.d("request!", "starting");


                List params2 = new ArrayList();
                params.add(new BasicNameValuePair("name", "spider-man"));
                JSONObject json2 = jsonParser.makeHttpRequest(URL2, "GET",
                        params2);
                Log.d("datos del json funcionaaaaaaaaa", json2.toString());

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(URL, "POST",params
                );

                // check your log for json response
                Log.d("datos del json que descarga", json.toString());

                // json success tag
                JSONObject data = json2.getJSONObject("data");
                total2= data.getString("total");

                JSONArray desc= data.getJSONArray("results");

                total2= desc.getJSONObject(0).getString("description");


                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());

//mostrar los datos descargados

                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            total.setText(total2);
            if (file_url != null) {
                Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }


    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



    public static final String md52(final String s) {
        final String MD5 = "MD5";
        try {
// Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
// Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }




}//Fin de clase
