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
import android.widget.Toast;

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
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.activity.NewBookingActivity;
import lmdelivery.longmen.com.android.data.Address;
import lmdelivery.longmen.com.android.data.User;
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
    private static View rootView;
    private EditText etUnit, etPostal, etCity, etName, etPhone;
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
            etName = (EditText) rootView.findViewById(R.id.et_sender_name);
            etPhone = (EditText) rootView.findViewById(R.id.et_sender_phone);
            mapView = (FrameLayout) rootView.findViewById(R.id.map_view);

            Address address = ((NewBookingActivity) getActivity()).pickupAddr;
            User user = ((NewBookingActivity) getActivity()).user;
            etPostal.setText(address.getPostalCode());
            etCity.setText(address.getCity());
            etUnit.setText(address.getUnitNumber());
            if (user != null) {
                if (!TextUtils.isEmpty(user.firstName) && !TextUtils.isEmpty(user.lastName))
                    etName.setText(String.format("%s %s", user.firstName, user.lastName));
                else if (!TextUtils.isEmpty(user.firstName))
                    etName.setText(user.firstName);
                else if (!TextUtils.isEmpty(user.lastName))
                    etName.setText(user.lastName);

                if (!TextUtils.isEmpty(user.phone))
                    etPhone.setText(user.phone);
            }
            mAutocompleteView.setText(address.getStreetName());

            // Register a listener that receives callbacks when a suggestion has been selected
            mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
            mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, ((NewBookingActivity) getActivity()).mGoogleApiClient, BOUNDS_GREATER_VANCOUVER, null);//AutocompleteFilter.create(filterTypes));
            mAutocompleteView.setAdapter(mAdapter);
            setUpTextListener();
            setUpMapIfNeeded();
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }


        // Inflate the layout for this fragment
        return rootView;
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
                ((NewBookingActivity) getActivity()).pickupAddr.setCity(etCity.getText().toString());
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
                ((NewBookingActivity) getActivity()).pickupAddr.setPostalCode(etPostal.getText().toString());

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
                ((NewBookingActivity) getActivity()).pickupAddr.setUnitNumber(etUnit.getText().toString());

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
                ((NewBookingActivity) getActivity()).pickupAddr.setStreetName(mAutocompleteView.getText().toString());

            }
        });
        mAutocompleteView.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Util.closeKeyBoard(getActivity(), mAutocompleteView);
                handled = true;
            }
            return handled;
        });
    }

    public boolean saveAndValidate() {
        if (getActivity() != null) {
            ((NewBookingActivity) getActivity()).pickupAddr.setUnitNumber(etUnit.getText().toString());
        }
        try {
            boolean validatePickupCity = validatePickupCity(etCity.getText().toString());
            boolean validatePostalCode = validatePostalCode(etPostal.getText().toString());
            boolean validateStreet = validateStreet(mAutocompleteView.getText().toString());
            boolean validateName = validateName(etName.getText().toString());
            boolean validatePhone = validatePhone(etPhone.getText().toString());
            return validatePickupCity && validatePostalCode && validateStreet && validateName && validatePhone;
        } catch (Exception e) {
            return false;
        }


    }

    private boolean validateName(String name) {
        if (isAdded()) {
            ((NewBookingActivity) getActivity()).pickupAddr.setName(name);
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
            ((NewBookingActivity) getActivity()).pickupAddr.setPhone(phone);
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

    private boolean validateStreet(String street) {
        if (isAdded())
            ((NewBookingActivity) getActivity()).pickupAddr.setStreetName(street);
        if (street.isEmpty()) {
            ((TextInputLayout) mAutocompleteView.getParent()).setError(getString(R.string.required));
            return false;
        } else {
            ((TextInputLayout) mAutocompleteView.getParent()).setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePickupCity(String city) {
        if (isAdded()) {
            ((NewBookingActivity) getActivity()).pickupAddr.setCity(city);
        }
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

    private boolean validatePostalCode(String zip) {
        if (isAdded()) {
            ((NewBookingActivity) getActivity()).pickupAddr.setPostalCode(zip);
        }
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                response -> {
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

                                JSONObject location = response.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                                String lat = location.getString("lat");
                                String lng = location.getString("lng");
                                if (lat != null && lng != null && !lat.isEmpty() && !lng.isEmpty()) {

                                    Double dLat = Double.parseDouble(lat);
                                    Double dLng = Double.parseDouble(lng);
                                    LatLng latLng = new LatLng(dLat, dLng);
                                    if (mMap != null && mapView != null) {
                                        mapView.setVisibility(View.VISIBLE);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.addMarker(new MarkerOptions().position(latLng));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
            Log.e(TAG, "That didn't work!");
        });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            final SupportMapFragment mMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            // Try to obtain the map from the SupportMapFragment.
            mMapFragment.getMapAsync(googleMap -> mMap = googleMap);
        }
    }
}
