package lmdelivery.longmen.com.android.fragments;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.activity.NewBookingActivity;
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

    private EditText etUnit, etPostal, etCity, etProvince, etName, etPhone;
    private Spinner spinner;

    private static View rootView;
    private GoogleMap mMap;
    private FrameLayout mapView;

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
    public void onResume() {
        super.onResume();
//        Logger.e(TAG, "onResume");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Logger.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Logger.e(TAG, "onCreateView");
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_destination, container, false);

            spinner = (Spinner) rootView.findViewById(R.id.spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.dropoff_country_array, R.layout.spinner_item);

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
            etName = (EditText) rootView.findViewById(R.id.et_receiver_name);
            etPhone = (EditText) rootView.findViewById(R.id.et_receiver_phone);
            mapView = (FrameLayout) rootView.findViewById(R.id.map_view);

            // Register a listener that receives callbacks when a suggestion has been selected
            mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);


            // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
            // the entire world.
            Set<Integer> filterTypes = new HashSet<>();
            filterTypes.add(Place.TYPE_STREET_ADDRESS);
            mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, ((NewBookingActivity) getActivity()).mGoogleApiClient, BOUNDS_CANADA, null);//AutocompleteFilter.create(filterTypes));
            mAutocompleteView.setAdapter(mAdapter);
            setUpTextListener();
            setUpMapIfNeeded();
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        return rootView;
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

    private void setUpTextListener() {

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etPhone.getText().toString().isEmpty()) {
                    ((TextInputLayout) etPhone.getParent()).setErrorEnabled(false);
                }
                ((NewBookingActivity) getActivity()).pickupAddr.setPhone(etPhone.getText().toString());
            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etName.getText().toString().isEmpty()) {
                    ((TextInputLayout) etName.getParent()).setErrorEnabled(false);
                }
                ((NewBookingActivity) getActivity()).pickupAddr.setName(etName.getText().toString());
            }
        });

        etProvince.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etProvince.getText().toString().isEmpty()) {
                    ((TextInputLayout) etProvince.getParent()).setErrorEnabled(false);
                }
                ((NewBookingActivity) getActivity()).dropOffAddr.setProvince(etProvince.getText().toString());

            }
        });

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
                }
                ((NewBookingActivity) getActivity()).dropOffAddr.setCity(etCity.getText().toString());

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
                }
                ((NewBookingActivity) getActivity()).dropOffAddr.setPostalCode(etPostal.getText().toString());

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
                }
                ((NewBookingActivity) getActivity()).dropOffAddr.setUnitNumber(etUnit.getText().toString());

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
                }
                ((NewBookingActivity) getActivity()).dropOffAddr.setStreetName(mAutocompleteView.getText().toString());

            }
        });
    }

    public boolean saveAndValidate() {
        if (getActivity() != null) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setUnitNumber(etUnit.getText().toString());
            ((NewBookingActivity) getActivity()).dropOffAddr.setCountry(spinner.getSelectedItem().toString());
            boolean validateDropoffCity = validateDropoffCity();
            boolean validatePostalCode = validatePostalCode();
            boolean validateStreet = validateStreet();
            boolean validateName = validateName();
            boolean validatePhone = validatePhone();
            boolean validateProvince = validateProvince();

            return validateDropoffCity && validatePostalCode && validateStreet && validateName && validatePhone && validateProvince;
        } else
            return false;
    }

    private boolean validateName() {
        if (isAdded()) {
            String name = etName.getText().toString();
            ((NewBookingActivity) getActivity()).dropOffAddr.setName(name);
            if (name.isEmpty()) {
                ((TextInputLayout) etName.getParent()).setError(getString(R.string.required));
                return false;
            } else {
                ((TextInputLayout) etName.getParent()).setErrorEnabled(false);
                return true;
            }
        } else
            return false;
    }

    private boolean validatePhone() {
        if (isAdded()) {
            String phone = etPhone.getText().toString();
            ((NewBookingActivity) getActivity()).dropOffAddr.setPhone(phone);
            if (phone.isEmpty()) {
                ((TextInputLayout) etPhone.getParent()).setError(getString(R.string.required));
                return false;
            } else {
                ((TextInputLayout) etPhone.getParent()).setErrorEnabled(false);
                return true;
            }
        } else
            return false;
    }

    private boolean validateProvince() {
        String province = etProvince.getText().toString();
        ((NewBookingActivity) getActivity()).dropOffAddr.setProvince(province);
        if (province.isEmpty()) {
            ((TextInputLayout) etProvince.getParent()).setError(getString(R.string.required));
            return false;
        } else {
            ((TextInputLayout) etProvince.getParent()).setErrorEnabled(false);
            return true;
        }
    }


    private boolean validateStreet() {
        if (isAdded()) {
            String street = mAutocompleteView.getText().toString();
            ((NewBookingActivity) getActivity()).dropOffAddr.setStreetName(street);
            if (street.isEmpty()) {
                ((TextInputLayout) mAutocompleteView.getParent()).setError(getString(R.string.required));
                return false;
            } else {
                ((TextInputLayout) mAutocompleteView.getParent()).setErrorEnabled(false);
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean validateDropoffCity() {
        if (isAdded()) {
            String city = etCity.getText().toString();
            ((NewBookingActivity) getActivity()).dropOffAddr.setCity(city);
            if (city.isEmpty()) {
                ((TextInputLayout) etCity.getParent()).setError(getString(R.string.required));
                return false;
            } else {
                ((TextInputLayout) etCity.getParent()).setErrorEnabled(false);
                return true;
            }
        }
        return false;
    }

    private boolean validatePostalCode() {
        if (isAdded()) {
            String zip = etPostal.getText().toString();
            ((NewBookingActivity) getActivity()).dropOffAddr.setPostalCode(zip);

            if (zip.isEmpty()) {
                ((TextInputLayout) etPostal.getParent()).setError(getString(R.string.required));
                return false;
            }

            String cadRex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
            String chnRex = "^([0-9]){6}$";
            Pattern pattern = Pattern.compile(cadRex, Pattern.CASE_INSENSITIVE);
            Pattern pattern2 = Pattern.compile(chnRex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(zip);
            Matcher matcher2 = pattern2.matcher(zip);

            if (!matcher.matches() && spinner.getSelectedItem().equals("Canada")) {
                ((TextInputLayout) etPostal.getParent()).setError(getString(R.string.err_post_wrong_format_canada));
                return false;

            } else if (!matcher2.matches() && spinner.getSelectedItem().equals("China")) {
                ((TextInputLayout) etPostal.getParent()).setError(getString(R.string.err_post_wrong_format_canada));
                return false;
            } else {
                ((TextInputLayout) etPostal.getParent()).setErrorEnabled(false);
                return true;
            }
        } else
            return false;
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

    private void getPlaceDetailById(String placeID, boolean isChinese) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
        url += placeID + "&key=" + Constant.GOOGLE_PLACE_API_SERVER_KEY;
        if (isChinese)
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
                                    if (type.equals("administrative_area_level_2")) {
                                        adminLevel2 = component.getString("long_name");
                                    } else if (type.equals("street_number")) {
                                        streetNumber = component.getString("long_name");
                                    } else if (type.equals("route")) {
                                        streetName = component.getString("long_name");
                                    } else if (type.equals("locality")) {
                                        city = component.getString("long_name");
                                    } else if (type.equals("administrative_area_level_1")) {
                                        province = component.getString("short_name");
                                    } else if (type.equals("country")) {
                                        country = component.getString("long_name");
                                        countryCode = component.getString("short_name");
                                    } else if (type.equals("postal_code_prefix")) {
                                        post = component.getString("long_name");
                                    } else if (type.equals("postal_code")) {
                                        post = component.getString("long_name");
                                    }
                                }
                            }

                            if (!countryCode.equalsIgnoreCase("CN") && !countryCode.equalsIgnoreCase("CA")) {
                                Toast.makeText(getActivity(), AppController.getAppContext().getString(R.string.only_ship_to_canada_china), Toast.LENGTH_LONG).show();
                            } else {
                                if (countryCode.equalsIgnoreCase("CN")) {
                                    spinner.setSelection(1, true);
                                } else {
                                    spinner.setSelection(0, true);
                                }
                                etPostal.setText(post);
                                etCity.setText(city);
                                etProvince.setText(province);
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
                                            if (mMap != null) {
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
