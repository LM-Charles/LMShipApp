package lmdelivery.longmen.com.android.UIFragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.NewBookingActivity;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;
import lmdelivery.longmen.com.android.widget.PlaceAutocompleteAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends Fragment {

    private static final String TAG = DestinationFragment.class.getSimpleName();
    private static final LatLngBounds BOUNDS_CANADA = new LatLngBounds(new LatLng(48.257666, -139.824677), new LatLng(60.133349, -52.900856));
    private static final LatLngBounds BOUNDS_CHINA = new LatLngBounds(new LatLng(16.764773, 75.665473), new LatLng(54.700624, 135.870546));
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private EditText etUnit, etPostal, etCity, etProvince;
    private Spinner spinner;
    private FloatingActionButton fab;
    private CardView cardView;
    private TextInputLayout tilAddress;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DestinationFragment.
     */
    public static DestinationFragment newInstance() {
        DestinationFragment fragment = new DestinationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DestinationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();
        Logger.e(TAG, "onResume");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.e(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_destination, container, false);

        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.dropoff_country_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.to_autocomplete_places);
        etPostal = (EditText) rootView.findViewById(R.id.et_to_postal);
        etCity = (EditText) rootView.findViewById(R.id.et_to_city);
        etUnit = (EditText) rootView.findViewById(R.id.et_to_unit);
        etProvince = (EditText) rootView.findViewById(R.id.et_to_province);
        cardView = (CardView) rootView.findViewById(R.id.card_to);
        tilAddress = (TextInputLayout) rootView.findViewById(R.id.til_address);
        etPostal.addTextChangedListener(watcher);
        etCity.addTextChangedListener(watcher);
        etUnit.addTextChangedListener(watcher);
        etProvince.addTextChangedListener(watcher);

//        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(validateToForm()){
//                    ((NewBookingActivity) getActivity()).scrollTo(Constant.TAB_POTSITION_TO+1);
//                }
//            }
//        });

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);


        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        Set<Integer> filterTypes = new HashSet<>();
        filterTypes.add(Place.TYPE_STREET_ADDRESS);
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, ((NewBookingActivity) getActivity()).mGoogleApiClient, BOUNDS_CANADA, null);//AutocompleteFilter.create(filterTypes));
        mAutocompleteView.setAdapter(mAdapter);
        // Inflate the layout for this fragment
        return rootView;
    }



    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            showFab();
        }
    };

    private void showFab(){
        boolean isValid = true;
        if(etPostal.getText().toString().isEmpty()){
            isValid = false;
        }

        if(etCity.getText().toString().isEmpty()){
            isValid = false;
        }

        if(etProvince.getText().toString().isEmpty()){
            isValid = false;
        }

        if(mAutocompleteView.getText().toString().isEmpty()){
            isValid = false;
        }

//        if(isValid){
//            fab.setVisibility(View.VISIBLE);
//            fab.setScaleX(0);
//            fab.setScaleY(0);
//            fab.animate().scaleX(1).scaleY(1).setDuration(Constant.FAB_ANIMTION_DURATION).setInterpolator(new OvershootInterpolator()).start();
//        }else{
//            if(fab.getVisibility()==View.VISIBLE){
//                fab.animate().scaleX(0).scaleY(0).setDuration(Constant.FAB_ANIMTION_DURATION).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
//                        fab.setVisibility(View.GONE);
//                    }
//                }).start();
//            }
//        }
    }

    public boolean validateToForm(){
        boolean isValid = true;
        if(etPostal.getText().toString().isEmpty()){
            etPostal.setError("Postal code cannot be empty");
            isValid = false;
        }

        if(etCity.getText().toString().isEmpty()){
            etCity.setError("City cannot be empty");
            isValid = false;
        }


        if(etProvince.getText().toString().isEmpty()){
            etProvince.setError("Province cannot be empty");
            isValid = false;
        }

        if(mAutocompleteView.getText().toString().isEmpty()){
            mAutocompleteView.setError("Street address cannot be empty");
            isValid = false;
        }

        if(isValid){
            fab.setVisibility(View.VISIBLE);
            fab.setScaleX(0);
            fab.setScaleY(0);
            fab.animate().scaleX(1).scaleY(1).setDuration(Constant.FAB_ANIMTION_DURATION).setInterpolator(new OvershootInterpolator()).start();
        }else{
            if(fab.getVisibility()==View.VISIBLE){
                fab.animate().scaleX(0).scaleY(0).setDuration(Constant.FAB_ANIMTION_DURATION).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fab.setVisibility(View.GONE);
                    }
                }).start();
            }
        }

        return isValid;
    }


    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            Logger.i(TAG, "Autocomplete item selected: " + item.description);

            boolean isChinese = item.description.toString().matches("^[\\u2E80-\\u9FFF]+$");


            getPlaceDetailById(placeId, isChinese);
            mAutocompleteView.setText("");
            Util.closeKeyBoard(getActivity().getApplicationContext(), mAutocompleteView);

            Logger.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    private void getPlaceDetailById(String placeID,boolean isChinese) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
        url += placeID + "&key=" + Constant.GOOGLE_PLACE_API_SERVER_KEY;
        if(isChinese)
            url += "&language=zh-CN";

        Logger.e(TAG, url);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.e(TAG, "Response is: " + response.toString());
                        try {
                            JSONArray addrComponentArr = response.getJSONObject("result").getJSONArray("address_components");
                            String streetNumber = "", streetName = "", city = "", province = "", country = "", post = "", adminLevel2 = "", countryCode = "";
                            for (int i = 0; i < addrComponentArr.length(); i++) {

                                JSONObject component = ((JSONObject) addrComponentArr.get(i));
                                JSONArray typeArr = component.getJSONArray("types");
                                if (typeArr.length() > 0)
                                    Logger.e(TAG, typeArr.get(0).toString());
                                if (typeArr.length() > 0) {
                                    String type = typeArr.get(0).toString();
                                    if(type.equals("administrative_area_level_2")){
                                        adminLevel2 = component.getString("long_name");
                                    }
                                    else if(type.equals("street_number")){
                                        streetNumber = component.getString("long_name");
                                    }
                                    else if(type.equals("route")){
                                        streetName = component.getString("long_name");
                                    }else if(type.equals("locality")){
                                        city = component.getString("long_name");
                                    }else if(type.equals("administrative_area_level_1")){
                                        province = component.getString("short_name");
                                    }else if(type.equals("country")){
                                        country = component.getString("long_name");
                                        countryCode = component.getString("short_name");
                                    }else if(type.equals("postal_code_prefix")){
                                        post = component.getString("long_name");
                                    }else if(type.equals("postal_code")){
                                        post = component.getString("long_name");
                                    }
                                }
                            }

                            if(!countryCode.equalsIgnoreCase("CN") && !countryCode.equalsIgnoreCase("CA")){
                                Toast.makeText(getActivity(), "We only support shipping to Canada or China for now", Toast.LENGTH_SHORT).show();
                            }else{
                                etPostal.setText(post);
                                etCity.setText(city);
                                etProvince.setText(province);
                                mAutocompleteView.setAdapter(null);
                                mAutocompleteView.setText(((streetNumber.isEmpty())?"":(streetNumber + " ")) + ((streetName.isEmpty())?"":streetName));
                                mAutocompleteView.setAdapter(mAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

}
