package lmdelivery.longmen.com.android.UIFragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class PickupFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final java.lang.String TAG = PickupFragment.class.getName();
    private static final LatLngBounds BOUNDS_GREATER_VANCOUVER = new LatLngBounds(new LatLng(49.004506, -123.305074), new LatLng(49.292849, -122.710439));
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;
    private static View rootView;
    private EditText etUnit, etPostal, etCity;
    private GoogleMap mMap;
    private FrameLayout mapView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_pickup, container, false);
            // Retrieve the AutoCompleteTextView that will display Place suggestions.
            mAutocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.autocomplete_places);
            etPostal = (EditText) rootView.findViewById(R.id.et_postal);
            etCity = (EditText) rootView.findViewById(R.id.et_city);
            etUnit = (EditText) rootView.findViewById(R.id.et_unit);
            mapView = (FrameLayout) rootView.findViewById(R.id.map_view);

            // Register a listener that receives callbacks when a suggestion has been selected
            mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

            // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
            // the entire world.
            Set<Integer> filterTypes = new HashSet<>();
            filterTypes.add(Place.TYPE_STREET_ADDRESS);
            mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, ((NewBookingActivity) getActivity()).mGoogleApiClient, BOUNDS_GREATER_VANCOUVER, null);//AutocompleteFilter.create(filterTypes));
            mAutocompleteView.setAdapter(mAdapter);

            setUpTextLinstener();
            setUpMapIfNeeded();
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }


        // Inflate the layout for this fragment
        return rootView;
    }


    private void setUpTextLinstener() {
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etCity.getText().toString().isEmpty()) {
                    ((TextInputLayout) etCity.getParent()).setErrorEnabled(false);
                    ((NewBookingActivity) getActivity()).pickupAddr.setCity(etCity.getText().toString());
                }
            }
        });

        etPostal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etPostal.getText().toString().isEmpty()) {
                    ((TextInputLayout) etPostal.getParent()).setErrorEnabled(false);
                    ((NewBookingActivity) getActivity()).pickupAddr.setPostalCode(etPostal.getText().toString());
                }
            }
        });

        etUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etUnit.getText().toString().isEmpty()) {
                    ((TextInputLayout) etUnit.getParent()).setErrorEnabled(false);
                    ((NewBookingActivity) getActivity()).pickupAddr.setUnitNumber(etUnit.getText().toString());
                }
            }
        });

        mAutocompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mAutocompleteView.getText().toString().isEmpty()) {
                    ((TextInputLayout) mAutocompleteView.getParent()).setErrorEnabled(false);
                    ((NewBookingActivity) getActivity()).pickupAddr.setStreetName(mAutocompleteView.getText().toString());
                }
            }
        });
    }

    public boolean saveAndValidate() {
        if (getActivity()!=null) {
            ((NewBookingActivity) getActivity()).pickupAddr.setUnitNumber(etUnit.getText().toString());
            boolean cityValid = validatePickupCity();
            boolean postValid = validatePostalCode();
            boolean streetValid = validateStreet();
            return cityValid && postValid && streetValid;
        }
        return false;
    }

    private boolean validateStreet() {
        String street = mAutocompleteView.getText().toString();
        ((NewBookingActivity) getActivity()).pickupAddr.setStreetName(street);
        if (street.isEmpty()) {
            ((TextInputLayout) mAutocompleteView.getParent()).setError(getString(R.string.required));
            return false;
        } else {
            ((TextInputLayout) mAutocompleteView.getParent()).setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePickupCity() {
        if(isAdded()) {
            String city = etCity.getText().toString();
            ((NewBookingActivity) getActivity()).pickupAddr.setCity(city);
            if (city.isEmpty()) {
                ((TextInputLayout) etCity.getParent()).setError(getString(R.string.required));
                return false;
            } else if (!Constant.citiesInVan.contains(city.toUpperCase())) {
                ((TextInputLayout) etCity.getParent()).setError(getString(R.string.err_not_in_van));
                return false;
            } else {
                ((TextInputLayout) etCity.getParent()).setErrorEnabled(false);
                return true;
            }
        }
        else
            return false;
    }

    private boolean validatePostalCode() {
        if(isAdded()) {
            String zip = etPostal.getText().toString();
            ((NewBookingActivity) getActivity()).pickupAddr.setPostalCode(zip);

            if (zip.isEmpty()) {
                ((TextInputLayout) etPostal.getParent()).setError(getString(R.string.required));
                return false;
            }

            String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";

            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(zip);

            if (!matcher.matches()) {
                ((TextInputLayout) etPostal.getParent()).setError(getString(R.string.err_post_wrong_format_canada));
                return false;
            } else {
                ((TextInputLayout) etPostal.getParent()).setErrorEnabled(false);
                return true;
            }
        }else{
            return false;
        }
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
            mapView.setVisibility(View.GONE);
            mAutocompleteView.setText("");
            Util.closeKeyBoard(getActivity().getApplicationContext(), mAutocompleteView);

            Logger.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    private void getPlaceDetailById(final String placeID) {
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
                        if (isAdded()) {
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
                                        if (type.equals("administrative_area_level_2")) {
                                            adminLevel2 = component.getString("long_name");
                                        } else if (type.equals("street_number")) {
                                            streetNumber = component.getString("long_name");
                                        } else if (type.equals("route")) {
                                            streetName = component.getString("long_name");
                                        } else if (type.equals("locality")) {
                                            city = component.getString("long_name");
                                        } else if (type.equals("administrative_area_level_1")) {
                                            province = component.getString("long_name");
                                        } else if (type.equals("country")) {
                                            country = component.getString("long_name");
                                        } else if (type.equals("postal_code_prefix")) {
                                            post = component.getString("long_name");
                                        } else if (type.equals("postal_code")) {
                                            post = component.getString("long_name");
                                        }
                                    }
                                }

                                if (!Constant.citiesInVan.contains(city.toUpperCase())) {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.can_only_pick_up_in_van), Toast.LENGTH_LONG).show();
                                } else {
                                    etPostal.setText(post);
                                    etCity.setText(city);
                                    mAutocompleteView.setAdapter(null);
                                    mAutocompleteView.setText(((streetNumber.isEmpty()) ? "" : (streetNumber + " ")) + ((streetName.isEmpty()) ? "" : streetName));
                                    mAutocompleteView.setAdapter(mAdapter);

                                    //show map
                                    try {
                                        JSONObject location = response.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                                        String lat = location.getString("lat");
                                        String lng = location.getString("lng");
                                        if (lat != null && lng != null && !lat.isEmpty() && !lng.isEmpty()) {
                                            try {
                                                Double dLat = Double.parseDouble(lat);
                                                Double dLng = Double.parseDouble(lng);
                                                LatLng latLng = new LatLng(dLat, dLng);
                                                if (mMap != null && mapView != null) {
                                                    mapView.setVisibility(View.VISIBLE);
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                                    mMap.addMarker(new MarkerOptions().position(latLng));
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            final SupportMapFragment mMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            // Try to obtain the map from the SupportMapFragment.
            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                }
            });
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
