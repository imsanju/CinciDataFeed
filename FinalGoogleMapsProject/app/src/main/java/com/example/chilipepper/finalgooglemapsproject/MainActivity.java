package com.example.chilipepper.finalgooglemapsproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;

    private static String url = "https://data.cincinnati-oh.gov/resource/rg6p-b3h3.json";

    private static final String inspectionType = "insp_subtype";
    private static final String violationDescr = "violation_description";
    static final String businessName = "business_name";
    private static final String violationComments = "violation_comments";
    private static final String longitude = "longitude";
    private static final String latitude = "latitude";
    private static final String address = "address";
    private static final String city = "city";
    private static final String state = "state";
    private static final String recordNum = "recordnum_license";
    private static final String zip = "postal_code";

    private static final String QUERY_URL = "https://data.cincinnati-oh.gov/resource/rg6p-b3h3.json";

    ArrayList<Blip> blipList = new ArrayList<Blip>();
    ArrayList<Marker> jsonMarkers = new ArrayList<>();

    ListView lv ;
    EditText inputSearch;
    ArrayList<HashMap<String, String>> arraylist;
    JSONObject jsonObject;
    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.list);

        // search code
        new DownloadJSON().execute();
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        searchTextChangedListener();
        // end search code


        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Map");
        tabSpec.setContent(R.id.tabMap);
        tabSpec.setIndicator("Map");
        tabHost.addTab(tabSpec);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);


    }



    private void populateList() {
        ArrayAdapter<Blip> adapter = new BlipListAdapter();
        lv.setAdapter(adapter);

    }

    private class BlipListAdapter extends ArrayAdapter<Blip> {

        public BlipListAdapter() {
            super (MainActivity.this, R.layout.list_activity, blipList);
        }

        @Override
        public View getView(final int pos, View view, ViewGroup parent)
        {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.list_activity, parent, false);

            final Blip curBlip = blipList.get(pos);

            final TextView iType = (TextView) view.findViewById(R.id.inspectionType);
            iType.setText(curBlip.get_inspectionType());
            final TextView vDesc = (TextView) view.findViewById(R.id.violationDescr);
            vDesc.setText(curBlip.get_violationDescr());
            TextView bName = (TextView) view.findViewById(R.id.businessName);
            bName.setText(curBlip.get_businessName());

            final Dialog dialog = new Dialog(MainActivity.this);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    dialog.setContentView(R.layout.view_details);
                    Button dialogButton = (Button) dialog.findViewById(R.id.btnCancel);

                    TextView dialogName = (TextView) dialog.findViewById(R.id.txtName);
                    dialogName.setText(curBlip.get_businessName());

                    TextView dialogAddress = (TextView) dialog.findViewById(R.id.txtAddress);
                    dialogAddress.setText(curBlip.get_address());

                    TextView dialogCity = (TextView) dialog.findViewById(R.id.txtCity);
                    dialogCity.setText(curBlip.get_city());

                    TextView dialogState = (TextView) dialog.findViewById(R.id.txtState);
                    dialogState.setText(curBlip.get_state());

                    TextView dialogZip = (TextView) dialog.findViewById(R.id.txtZip);
                    dialogZip.setText(curBlip.get_zip());

                    TextView dialogRecord = (TextView) dialog.findViewById(R.id.txtRecord);
                    dialogRecord.setText(curBlip.get_recordNum());

                    TextView dialogViolation = (TextView) dialog.findViewById(R.id.txtViolation);
                    dialogViolation.setText(curBlip.get_inspectionType());

                    TextView dialogDesc = (TextView) dialog.findViewById(R.id.txtDesc);
                    dialogDesc.setText(curBlip.get_violationDescr());

                    TextView dialogComments = (TextView) dialog.findViewById(R.id.txtComments);
                    dialogComments.setText(curBlip.get_violationComments());

                    dialogButton.setText("OK");
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            return view;
        }


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        gMap = googleMap;
        JSONParser jParser = new JSONParser();
        JSONArray json = jParser.getJSONFromUrl(url);

        for (int i = 0; i < 400; i++) {

            try {
                JSONObject c = json.getJSONObject(i);

                Blip blip = new Blip(c.getString(inspectionType), c.getString(violationDescr), c.getString(businessName), c.getString(address),
                        c.getString(city), c.getString(state), c.getString(recordNum), c.getString(zip), c.getString(violationComments),
                        Double.parseDouble(c.getString(longitude)), Double.parseDouble(c.getString(latitude)));
                String crit = "CRITICAL CONTROL POINT";

                if(blip._inspectionType.equalsIgnoreCase(crit))
                {
                    // making marker orange if it is a critical control point
                    blipList.add(blip);
                    LatLng businessMarker = new LatLng(blip.get_latitude(),blip.get_longitude());
                    gMap.addMarker(new MarkerOptions().position(businessMarker).title(blip.get_businessName()).snippet(blip.get_violationDescr()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(businessMarker));

                }
                else {
                    // if it is a standard inspection it'll be yellow
                    LatLng businessMarker = new LatLng(blip.get_latitude(),blip.get_longitude());
                    gMap.addMarker(new MarkerOptions().position(businessMarker).title(blip.get_businessName()).snippet(blip.get_violationDescr()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(businessMarker));
                }


            } catch (JSONException e)
            {
                e.printStackTrace();
            }


        }

        populateList();
    }

    /*
    Retrieve JSON objects and populate the arrayList with the data to be used by the search feature
     */
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            arraylist = new ArrayList<HashMap<String, String>>();
            // Retrieve JSON Objects
            JSONParser jParser = new JSONParser();
            JSONArray jsonArray = jParser.getJSONFromUrl(url);

            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    jsonObject = jsonArray.getJSONObject(i);
                    // Populate hashmap with json data
                    map.put(businessName, jsonObject.getString(businessName));
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

        }
    }

    private void searchTextChangedListener()
    {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<HashMap<String, String>> arrayTemplist = new ArrayList<HashMap<String, String>>();
                String searchString = inputSearch.getText().toString();
                int searchStringLength = searchString.length();
                if(searchString.isEmpty())
                {
                    //new DownloadJSON().execute();
                    populateList();
                }
                else
                {
                    for (int i = 0; i < arraylist.size(); i++) {
                        String currentString = arraylist.get(i).get(MainActivity.businessName).toString();
                        if(Pattern.compile(Pattern.quote(searchString), Pattern.CASE_INSENSITIVE).matcher(currentString).find()){
                            arrayTemplist.add(arraylist.get(i));
                        }
                    }
                    searchAdapter = new SearchAdapter(MainActivity.this, arrayTemplist);
                    lv.setAdapter(searchAdapter);
                    /*
                    final Dialog dialog = new Dialog(MainActivity.this);
                    final Blip curBlip = blipList.get(pos);
                    lv.setOnItemClickListener((new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            dialog.setContentView(R.layout.view_details);
                            Button dialogButton = (Button) dialog.findViewById(R.id.btnCancel);

                            TextView dialogName = (TextView) dialog.findViewById(R.id.txtName);
                            dialogName.setText(curBlip.get_businessName());

                            TextView dialogAddress = (TextView) dialog.findViewById(R.id.txtAddress);
                            dialogAddress.setText(curBlip.get_address());

                            TextView dialogCity = (TextView) dialog.findViewById(R.id.txtCity);
                            dialogCity.setText(curBlip.get_city());

                            TextView dialogState = (TextView) dialog.findViewById(R.id.txtState);
                            dialogState.setText(curBlip.get_state());

                            TextView dialogZip = (TextView) dialog.findViewById(R.id.txtZip);
                            dialogZip.setText(curBlip.get_zip());

                            TextView dialogRecord = (TextView) dialog.findViewById(R.id.txtRecord);
                            dialogRecord.setText(curBlip.get_recordNum());

                            TextView dialogViolation = (TextView) dialog.findViewById(R.id.txtViolation);
                            dialogViolation.setText(curBlip.get_inspectionType());

                            TextView dialogDesc = (TextView) dialog.findViewById(R.id.txtDesc);
                            dialogDesc.setText(curBlip.get_violationDescr());

                            TextView dialogComments = (TextView) dialog.findViewById(R.id.txtComments);
                            dialogComments.setText(curBlip.get_violationComments());

                            dialogButton.setText("OK");
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    }));*/
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}