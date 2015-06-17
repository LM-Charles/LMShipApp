package lmdelivery.longmen.com.android.UIFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
 * Use the {@link PickupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickupFragment extends Fragment {


    private static final java.lang.String TAG = PickupFragment.class.getName();
    private static final LatLngBounds BOUNDS_GREATER_VANCOUVER = new LatLngBounds(new LatLng(49.004506, -123.305074), new LatLng(49.292849, -122.710439));
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;


    private EditText etUnit, etPostal, etCity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PickupFragment.
     */
    public static PickupFragment newInstance() {
        PickupFragment fragment = new PickupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PickupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pickup, container, false);
        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.autocomplete_places);
        etPostal = (EditText) rootView.findViewById(R.id.et_postal);
        etCity = (EditText) rootView.findViewById(R.id.et_city);
        etUnit = (EditText) rootView.findViewById(R.id.et_unit);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);


        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        Set<Integer> filterTypes = new HashSet<>();
        filterTypes.add(Place.TYPE_STREET_ADDRESS);
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, ((NewBookingActivity) getActivity()).mGoogleApiClient, BOUNDS_GREATER_VANCOUVER, null);//AutocompleteFilter.create(filterTypes));
        mAutocompleteView.setAdapter(mAdapter);
        // Inflate the layout for this fragment
        return rootView;
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

            getPlaceDetailById(placeId);
            mAutocompleteView.setText("");
            Util.closeKeyBoard(getActivity().getApplicationContext(), mAutocompleteView);

            Logger.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    private void getPlaceDetailById(String placeID) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
        url += placeID + "&key=" + Constant.GOOGLE_PLACE_API_SERVER_KEY;

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
                            String streetNumber = "", streetName = "", city = "", province = "", country = "", post = "", adminLevel2 = "";
                            for (int i = 0; i < addrComponentArr.length(); i++) {

                                JSONObject component = ((JSONObject) addrComponentArr.get(i));
                                JSONArray typeArr = component.getJSONArray("types");
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
                                        province = component.getString("long_name");
                                    }else if(type.equals("country")){
                                        country = component.getString("long_name");
                                    }else if(type.equals("postal_code_prefix")){
                                        post = component.getString("long_name");
                                    }else if(type.equals("postal_code")){
                                        post = component.getString("long_name");
                                    }


                                }
                            }

                            if(!adminLevel2.equals("Greater Vancouver")){
                                Toast.makeText(getActivity(), "We can only pick up address in Great Vancouver for now", Toast.LENGTH_SHORT).show();
                            }else{
                                etPostal.setText(post);
                                etCity.setText(city);
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
