package com.fiuba.tdp.petadopt.fragments.addPet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.activities.MainActivity;
import com.fiuba.tdp.petadopt.fragments.addPet.map.ChooseLocationMapFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.map.LocationChosenDelegate;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("ALL")
public class AddPetFragment extends Fragment {


    private EditText nameEditText;
    private EditText ageEditText;
    private int[] imgViews = {
            R.id.imgView1,
            R.id.imgView2,
            R.id.imgView3,
            R.id.imgView4,
            R.id.imgView5
    };
    private static int RESULT_LOAD_IMAGE = 1;
    private ArrayList<Uri> imageUris;

    public AddPetFragment() {
    }

    private Pet pet = new Pet();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_add_pet, container, false);

        setUpPetFillingCallbacks(rootView);

        populateSpinner(rootView, R.id.pet_type, R.array.pet_type_array);
        populateSpinner(rootView, R.id.pet_gender, R.array.pet_gender_array);
        populateSpinner(rootView, R.id.pet_main_color, R.array.pet_color_array);
        populateSpinner(rootView, R.id.pet_second_color, R.array.pet_color_array);
        TextView locationView = (TextView) rootView.findViewById(R.id.chosen_location);
        final Button button = (Button) rootView.findViewById(R.id.choose_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseLocationMapFragment mapFragment = new ChooseLocationMapFragment();
                mapFragment.setLocationChosenDelegate(new LocationChosenDelegate() {
                    @Override
                    public void locationWasChosen(LatLng location, String address) {
                        pet.setLocation(location);
                        TextView locationView = (TextView) rootView.findViewById(R.id.chosen_location);
                        locationView.setText(address);
                    }
                });
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.content_frame, mapFragment, "Choose location");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        setupAddImageButton(rootView);
        setupSubmitButton(rootView);
        return rootView;
    }

    private void setupAddImageButton(View rootView) {
        final Button imageButton = (Button) rootView.findViewById(R.id.pet_submit_image);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGallery(v);
            }
        });
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public String getPath(final Uri uri) {

        Context context = this.getActivity();
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private InputStream getInputStreamFromURI(Uri contentURI) {
        InputStream is = null;
        try {
            is = this.getActivity().getContentResolver().openInputStream(contentURI);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return is;
    }

    private void setupSubmitButton(final View rootView) {
        final Button button = (Button) rootView.findViewById(R.id.pet_submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CheckBox vaccinated = (CheckBox) rootView.findViewById(R.id.pet_vaccinated);
                final EditText descriptionEditText = (EditText) rootView.findViewById(R.id.pet_description);
                pet.setVaccinated(vaccinated.isChecked());
                pet.setDescription(descriptionEditText.getText().toString());
                ValidationStatus status = validateFields();
                if (status.isError) {
                    Toast toast = Toast.makeText(getContext(), status.prettyPrintFields(), Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                final ProgressDialog progress = new ProgressDialog(v.getContext());
                progress.setTitle(R.string.loading);
                progress.show();
                PetsClient.instance().createPet(pet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        for (Uri uri : imageUris){
                            try {
                                PetsClient.instance().uploadImage(response.getString("id"), getPath(uri), new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int code, Header[] headers, JSONObject body) {
                                        String items = "";
                                        try {
                                            items = body.toString();
                                            Log.v("image response", items);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        };
                        progress.dismiss();
                        Toast toast = Toast.makeText(getContext(), R.string.pet_creation_success, Toast.LENGTH_SHORT);
                        toast.show();
                        ((MainActivity) getActivity()).goBackToHome();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progress.dismiss();
                        Toast toast = Toast.makeText(getContext(), R.string.pet_creation_error, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e("Error creating pet", pet.toJson());
                    }
                });


            }
        });
    }

    public void launchGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        imageUris = new ArrayList<Uri>();
        for (int imgView1 : imgViews) {
            ImageView imgView = (ImageView) this.getView().findViewById(imgView1);
            imgView.setImageURI(null);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();
                    ImageView imgView = (ImageView) this.getView().findViewById(imgViews[0]);

                    imgView.setImageURI(mImageUri);
                    imageUris.add(mImageUri);

                }else{
                    if(data.getClipData()!=null){
                        ClipData mClipData=data.getClipData();

                        for(int i=0;i<mClipData.getItemCount();i++){

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            imageUris.add(uri);

                            ImageView imgView = (ImageView) this.getView().findViewById(imgViews[i]);

                            imgView.setImageURI(uri);

                        }
                    }

                }


            } else {
                Toast.makeText(this.getActivity(), "No elegiste ninguna imagen",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), "Algo anduvo mal", Toast.LENGTH_LONG)
                    .show();
        }

    }


    private ValidationStatus validateFields() {
        ValidationStatus status = new ValidationStatus();
        String ageText = ageEditText.getText().toString();
        if (ageText == null || ageText.equals("")) {
            status.isError = true;
            status.addErrorField(getString(R.string.add_pet_age_not_completed));
        }
        String nameText = ageEditText.getText().toString();
        if (nameText == null || nameText.equals("")) {
            status.isError = true;
            status.addErrorField(getString(R.string.add_pet_name_not_completed));
        }
        ;
        return status;
    }

    private void setUpPetFillingCallbacks(View rootView) {
        Spinner spinner = (Spinner) rootView.findViewById(R.id.pet_type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.pet_type_array);
                pet.setType(petTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        spinner.setSelection(0);

        spinner = (Spinner) rootView.findViewById(R.id.pet_gender);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.pet_gender_array);
                pet.setGender(petTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);

        spinner = (Spinner) rootView.findViewById(R.id.pet_main_color);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.pet_color_array);
                pet.setFirstColor(petTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        spinner.setSelection(0);

        spinner = (Spinner) rootView.findViewById(R.id.pet_second_color);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.pet_color_array);
                pet.setSecondColor(petTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final EditText editText = (EditText) rootView.findViewById(R.id.pet_name);
        nameEditText = editText;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pet.setName(editText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final EditText ageEditText = (EditText) rootView.findViewById(R.id.pet_age);
        this.ageEditText = ageEditText;
        ageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pet.setAge(ageEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void populateSpinner(View rootView, int viewId, int arrayId) {
        Spinner spinner = (Spinner) rootView.findViewById(viewId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                arrayId, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private class ValidationStatus {
        public Boolean isError;
        public ArrayList<String> erroringFields;

        public ValidationStatus() {
            isError = false;
            erroringFields = new ArrayList<>();
        }

        public void addErrorField(String fieldName) {
            erroringFields.add(fieldName);
        }

        public String prettyPrintFields() {
            String prettyString = "";
            for (String error : erroringFields) {
                prettyString = prettyString + error + ", ";
            }
            return prettyString.substring(0, prettyString.length() - 2);
        }
    }
}
