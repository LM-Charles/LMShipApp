package lmdelivery.longmen.com.android.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.activity.NewBookingActivity;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.data.Address;
import lmdelivery.longmen.com.android.util.CountryCode;
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

    private EditText etUnit, etPostal, etCity, etProvince, etName, etPhone, etCountry;

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

            // Retrieve the AutoCompleteTextView that will display Place suggestions.
            mAutocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.to_autocomplete_places);
            etPostal = (EditText) rootView.findViewById(R.id.et_to_postal);
            etCity = (EditText) rootView.findViewById(R.id.et_to_city);
            etUnit = (EditText) rootView.findViewById(R.id.et_to_unit);
            etProvince = (EditText) rootView.findViewById(R.id.et_to_province);
            etName = (EditText) rootView.findViewById(R.id.et_receiver_name);
            etPhone = (EditText) rootView.findViewById(R.id.et_receiver_phone);
            etCountry = (EditText) rootView.findViewById(R.id.et_to_country);
            mapView = (FrameLayout) rootView.findViewById(R.id.map_view);

            Address address = ((NewBookingActivity) getActivity()).dropOffAddr;
            etPostal.setText(address.getPostalCode());
            etCity.setText(address.getCity());
            etUnit.setText(address.getUnitNumber());
            etName.setText(address.getName());
            etPhone.setText(address.getPhone());
            mAutocompleteView.setText(address.getStreetName());
            etProvince.setText(address.getProvince());
            etCountry.setText(address.getCountry());

            // Register a listener that receives callbacks when a suggestion has been selected
            mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

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
            mMapFragment.getMapAsync(googleMap -> mMap = googleMap);
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
                ((NewBookingActivity) getActivity()).dropOffAddr.setPhone(etPhone.getText().toString());
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
                ((NewBookingActivity) getActivity()).dropOffAddr.setName(etName.getText().toString());
            }
        });

        etCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!etCountry.getText().toString().isEmpty()) {
                    ((TextInputLayout) etCountry.getParent()).setErrorEnabled(false);
                }
                ((NewBookingActivity) getActivity()).dropOffAddr.setCountry(etCountry.getText().toString());

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
        mAutocompleteView.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Util.closeKeyBoard(getActivity(),mAutocompleteView);
                handled = true;
            }
            return handled;
        });
    }

    public boolean saveAndValidate() {
        if (getActivity() != null) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setUnitNumber(etUnit.getText().toString());
        }
            boolean validateCountry = validateCountry(etCountry.getText().toString());
            boolean validateDropoffCity = validateDropoffCity(etCity.getText().toString());
            boolean validatePostalCode = validatePostalCode(etPostal.getText().toString());
            boolean validateStreet = validateStreet(mAutocompleteView.getText().toString());
            boolean validateName = validateName(etName.getText().toString());
            boolean validatePhone = validatePhone(etPhone.getText().toString());
            boolean validateProvince = validateProvince(etProvince.getText().toString());

            return validateDropoffCity && validatePostalCode && validateStreet && validateName && validatePhone && validateProvince && validateCountry;

    }

    private boolean validateName(String name) {

        if (isAdded()) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setName(name);
        }
            if (name.isEmpty()) {
                ((TextInputLayout) etName.getParent()).setError(getString(R.string.required));
                return false;
            } else {
                ((TextInputLayout) etName.getParent()).setErrorEnabled(false);
                return true;
            }

    }

    private boolean validatePhone(String phone) {
        if (isAdded()) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setPhone(phone);
        }
            if (phone.isEmpty()) {
                ((TextInputLayout) etPhone.getParent()).setError(getString(R.string.required));
                return false;
            } else if (phone.length() < 10) {
                ((TextInputLayout) etPhone.getParent()).setError(getString(R.string.error_phone_too_short));
                return false;
            } else {
                ((TextInputLayout) etPhone.getParent()).setErrorEnabled(false);
                return true;
            }

    }

    private boolean validateProvince(String province) {
        if(isAdded()) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setProvince(province);
        }
        if (province.isEmpty()) {
            ((TextInputLayout) etProvince.getParent()).setError(getString(R.string.required));
            return false;
        } else {
            ((TextInputLayout) etProvince.getParent()).setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCountry(String country) {
        if (isAdded()) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setCountry(country);
        }
        if (country.isEmpty()) {
            ((TextInputLayout) etCountry.getParent()).setError(getString(R.string.required));
            return false;
        } else if (TextUtils.isEmpty(CountryCode.getCode(country))){
            ((TextInputLayout) etCountry.getParent()).setError(getString(R.string.invalid_country));
            return false;
        }else {
            ((TextInputLayout) etCountry.getParent()).setErrorEnabled(false);
            return true;
        }
    }


    private boolean validateStreet(String street) {
        if (isAdded()) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setStreetName(street);
        }
            if (street.isEmpty()) {
                ((TextInputLayout) mAutocompleteView.getParent()).setError(getString(R.string.required));
                return false;
            } else {
                ((TextInputLayout) mAutocompleteView.getParent()).setErrorEnabled(false);
                return true;
            }

    }

    private boolean validateDropoffCity(String city) {
        if (isAdded()) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setCity(city);
        }
            if (city.isEmpty()) {
                ((TextInputLayout) etCity.getParent()).setError(getString(R.string.required));
                return false;
            } else {
                ((TextInputLayout) etCity.getParent()).setErrorEnabled(false);
                return true;
            }

    }

    private boolean validatePostalCode(String zip) {
        if (isAdded()) {
            ((NewBookingActivity) getActivity()).dropOffAddr.setPostalCode(zip);
        }

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

            String country = etCountry.getText().toString();

            if (!matcher.matches() && (country.equals("Canada")|| country.equals("加拿大"))) {
                ((TextInputLayout) etPostal.getParent()).setError(getString(R.string.err_post_wrong_format_canada));
                return false;

            } else if (!matcher2.matches() && (country.equals("China") || country.equals("中国"))) {
                ((TextInputLayout) etPostal.getParent()).setError(getString(R.string.err_post_wrong_format_china));
                return false;
            } else {
                ((TextInputLayout) etPostal.getParent()).setErrorEnabled(false);
                return true;
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    Log.e(TAG, "Response is: " + response.toString());

                    try {
                        JSONArray addrComponentArr = response.getJSONObject("result").getJSONArray("address_components");
                        String streetNumber = "", streetName = "", city = "", province = "", country = "", post = "", adminLevel2 = "", countryCode = "", sublocalityLevel1 = "";
                        for (int i = 0; i < addrComponentArr.length(); i++) {
                            JSONObject component = ((JSONObject) addrComponentArr.get(i));
                            JSONArray typeArr = component.getJSONArray("types");

                            if (typeArr.length() > 0) {
                                String type = typeArr.get(0).toString();
                                if (type.equals("administrative_area_level_2")) {
                                    adminLevel2 = component.getString("long_name");
                                } else if(type.equals("sublocality_level_1")){
                                    sublocalityLevel1 = component.getString("long_name");
                                }else if (type.equals("street_number")) {
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

                        if (countryCode.equalsIgnoreCase("CN")) {
                            mAutocompleteView.setAdapter(null);
                            mAutocompleteView.setText(((sublocalityLevel1.isEmpty()) ? "" : sublocalityLevel1) + ((streetName.isEmpty()) ? "" : streetName) + ((streetNumber.isEmpty()) ? "" : (streetNumber + " ")));
                            mAutocompleteView.setAdapter(mAdapter);
                        } else {
                            mAutocompleteView.setAdapter(null);
                            mAutocompleteView.setText(((streetNumber.isEmpty()) ? "" : (streetNumber + " ")) + ((streetName.isEmpty()) ? "" : streetName));
                            mAutocompleteView.setAdapter(mAdapter);
                        }

                        etCountry.setText(country);
                        etPostal.setText(post);
                        etCity.setText(city);
                        etProvince.setText(province);

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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.e(TAG, "That didn't work!");
                });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

}
