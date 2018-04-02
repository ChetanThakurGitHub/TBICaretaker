package tbi.com.chat.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import tbi.com.R;
import tbi.com.chat.adapter.ChatAdapter;
import tbi.com.chat.model.BlockUsers;
import tbi.com.chat.model.Chatting;
import tbi.com.fcm.FcmNotificationBuilder;
import tbi.com.helper.ImageRotator;
import tbi.com.model.MySuffererList;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.Utils;
import tbi.com.vollyemultipart.VolleyMultipartRequest;
import tbi.com.vollyemultipart.VolleySingleton;

import static tbi.com.helper.ImagePicker.decodeBitmap;


public class MessageCaretakerFragment extends Fragment implements View.OnClickListener {
    //private static final String TAG = .class.getSimpleName();
    public static final int PICK_IMAGE_REQUEST_CODE = 234; // the number doesn't matter
    private static final String ARG_PARAM1 = "param1";
    private static final int RESULT_OK = -1;
    private static final String TEMP_IMAGE_NAME = "tempImage.jpg";
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;
    private static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;
    private static int minHeightQuality = DEFAULT_MIN_HEIGHT_QUALITY;
    private String mParam1;
    private ImageView iv_for_send, iv_for_pickImage, iv_for_image, iv_for_delete, iv_for_block;
    private EditText et_for_sendTxt;
    private TextView tv_for_noChat;
    private RecyclerView recycler_view;
    private Session session;
    private ArrayList<Chatting> chattings;
    private ChatAdapter chatAdapter;
    private String fullname, uID, chatNode, profileImage, OtherFirebaseToken, blockBy = "", noticiationStaus;
    private DatabaseReference chatRef;
    private Uri imageUri, photoURI;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private ArrayList<String> keys;
    private int notification = 0;
    private boolean isCamera;

    public MessageCaretakerFragment() {
        // Required empty public constructor
    }

    public static MessageCaretakerFragment newInstance(String param1) {
        MessageCaretakerFragment fragment = new MessageCaretakerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


 /*   @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (notification != 1) {
            finish();
        } else {
            if (session.getUserType().equals("1")) {
                Intent intent = new Intent(ChatActivity.this, FreelancerActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ChatActivity.this, UserMainActivity.class);
                startActivity(intent);
            }
        }
        MyFirebaseMessagingService.CHAT_HISTORY = "";
    }*/

    private static boolean hasCameraAccess(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean appManifestContainsPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions;
            }
            if (requestedPermissions == null) {
                return false;
            }

            if (requestedPermissions.length > 0) {
                List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
                return requestedPermissionsList.contains(permission);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
    }

    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    public static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm != null
                && (bm.getWidth() < minWidthQuality || bm.getHeight() < minHeightQuality)
                && i < sampleSizes.length);
        //Log.i(TAG, "Final bitmap width = " + (bm != null ? bm.getWidth() : "No final bitmap"));
        return bm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_message_caretaker, container, false);

        //MyFirebaseMessagingService.CHAT_HISTORY = "1";
        storage = FirebaseStorage.getInstance();
        keys = new ArrayList<>();
        session = new Session(getContext());

        /*uID = getIntent().getStringExtra("USER_ID");
        fullname = getIntent().getStringExtra("FULLNAME");
        profileImage = getIntent().getStringExtra("PROFILE_PIC");*/

       /* notification = 0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("uid") != null) {
                notification = 1;
                uID = bundle.getString("uid");
                fullname = bundle.getString("title");
                profileImage = bundle.getString("profilepic");
            }
        }*/
        mySuffererAPI();


        initView(view);

        iv_for_send.setOnClickListener(this);
        iv_for_pickImage.setOnClickListener(this);
        /*iv_for_delete.setOnClickListener(this);
        iv_for_block.setOnClickListener(this);*/

        chattings = new ArrayList<Chatting>();
        chatAdapter = new ChatAdapter(chattings, getContext());
        recycler_view.setAdapter(chatAdapter);

        /*getBlockList();
        getMessageList();*/

        /*if (imageUri == null) {
            iv_for_image.setVisibility(View.GONE);
            et_for_sendTxt.setVisibility(View.VISIBLE);
        } else {
            iv_for_image.setVisibility(View.VISIBLE);
            et_for_sendTxt.setVisibility(View.GONE);
        }*/

        /*if (keys.size() < 0) {
            iv_for_delete.setClickable(true);
        } else {
            iv_for_delete.setClickable(false);
        }*/

     /*   FirebaseDatabase.getInstance().getReference().child("users").child(uID).child("firebaseToken").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    OtherFirebaseToken = dataSnapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").child(uID).child("notificationStatus").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (
                        dataSnapshot.getValue() != null) {
                    noticiationStaus = dataSnapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getMessageList(final Dialog pDialog) {
        chattings.clear();
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chatting messageOutput = dataSnapshot.getValue(Chatting.class);
                Log.e("Chatting", "User: " + messageOutput.uid + " message: " + messageOutput.message);

                pDialog.dismiss();
                chattings.add(messageOutput);

               /* if (messageOutput.deleteby.equals("") || messageOutput.deleteby.equals(uID)) {
                    chattings.add(messageOutput);
                    //tv_for_noChat.setVisibility(View.GONE);
                    keys.add(dataSnapshot.getKey());
                   // iv_for_delete.setClickable(true);
                }*/
                recycler_view.scrollToPosition(chattings.size() - 1);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                recycler_view.scrollToPosition(chattings.size() - 1);
                // keys.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (chattings.isEmpty()) {
            pDialog.dismiss();
        }
    }

    private void initView(View view) {
        iv_for_send = view.findViewById(R.id.iv_for_send);
        et_for_sendTxt = view.findViewById(R.id.et_for_sendTxt);
        recycler_view = view.findViewById(R.id.recycler_view);
        iv_for_pickImage = view.findViewById(R.id.iv_for_pickImage);
    }

    private void writeToDBProfiles(Chatting chatModel) {

        chatRef.push().setValue(chatModel);

        /*if (noticiationStaus != null && !noticiationStaus.equals("")) {
            if (noticiationStaus.equals("1")) {
                String fToken = FirebaseInstanceId.getInstance().getToken();
                String message;
                if (chatModel.message.contains("firebasestorage.googleapis.com/v0/b/getu")) {
                    message = "Image";
                } else {
                    message = chatModel.message;
                }
                sendPushNotificationToReceiver(session.getFullName(), message, session.getUserID(), fToken, OtherFirebaseToken);
            }
        }*/
    }

    private void userImageClick() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.take_picture);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout layout_for_camera = dialog.findViewById(R.id.layout_for_camera);
        LinearLayout layout_for_gallery = dialog.findViewById(R.id.layout_for_gallery);
        EnableRuntimePermission();

        layout_for_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picImage(dialog);
            }
        });
        layout_for_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(i, Constant.GALLERY);
                isCamera = false;
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /*Dharmraj acharya */
    private void picImage(Dialog dialog) {
        if (!appManifestContainsPermission(getContext(), Manifest.permission.CAMERA) || hasCameraAccess(getContext())) {
            Intent takePhotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra("return-data", true);
            Uri uri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName()
                    + ".fileprovider", getTemporalFile(getContext()));
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            // takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTemporalFile(context)));
            startActivityForResult(takePhotoIntent, 7);
            isCamera = true;
            dialog.dismiss();
        }
    }

//Uri code

    public void EnableRuntimePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            Toast.makeText(getContext(), "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA}, Constant.RequestPermissionCode);
        }
    } // camera parmission

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.GALLERY && resultCode == RESULT_OK && null != data) {
            imageUri = data.getData();
        } else {
            if (requestCode == 7 && resultCode == RESULT_OK) {

                Bitmap bm = null;
                File imageFile = getTemporalFile(getContext());
                photoURI = Uri.fromFile(imageFile);

                bm = getImageResized(getContext(), photoURI);
                int rotation = ImageRotator.getRotation(getContext(), photoURI, isCamera);
                bm = ImageRotator.rotate(bm, rotation);


                File file = new File(getActivity().getExternalCacheDir(), UUID.randomUUID() + ".jpg");
                imageUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()
                        + ".fileprovider", file);


                if (file != null) {
                    try {
                        OutputStream outStream = null;
                        outStream = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.PNG, 80, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    } // onActivityResult

    private void uploadImage() {

        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference storageReference = storage.getReference();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Uri fireBaseUri = taskSnapshot.getDownloadUrl();
                            //Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            Chatting chatModel = new Chatting();
                            chatModel.message = fireBaseUri.toString();
                            chatModel.timeStamp = ServerValue.TIMESTAMP;
                            chatModel.uid = session.getUserID();
                            chatModel.firebaseToken = FirebaseInstanceId.getInstance().getToken();
                            chatModel.name = session.getFullName();
                            chatModel.deleteby = "";


                            //writeToDBProfiles(chatModel, chatModel2, session.getUserID());


                            imageUri = null;
                            photoURI = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.e("TAG", "onFailure: " + e.getMessage());
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void sendPushNotificationToReceiver(String name, String message, String userID, String token, String otherFirebaseToken) {
        FcmNotificationBuilder.initialize().title(name)
                .message(message).clickaction("ChatActivity")
                .firebaseToken(token)
                .receiverFirebaseToken(otherFirebaseToken)
                .uid(userID).profilePic(profileImage).chatNode(chatNode)
                .send();
    }

    private void blockUserDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_chatblock);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_ok = dialog.findViewById(R.id.btn_for_ok);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        final TextView tv_for_text = dialog.findViewById(R.id.tv_for_text);

        final BlockUsers blockUsers = new BlockUsers();
        if (blockBy.equals("")) {
            blockUsers.blockedBy = session.getUserID();
            tv_for_text.setText("Do you want to block this user");
        } else if (blockBy.equals("Both")) {
            blockUsers.blockedBy = uID;
            tv_for_text.setText("Do you want to unblock this user");
        } else if (blockBy.equals(session.getUserID())) {
            blockUsers.blockedBy = "";
            tv_for_text.setText("Do you want to unblock this user");
        } else if (blockBy.equals(uID)) {
            blockUsers.blockedBy = "Both";
            tv_for_text.setText("Do you want to block this user");
        }

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.setValue(blockUsers);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void chatDeleteDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_chatblock);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_ok = dialog.findViewById(R.id.btn_for_ok);
        ImageView iv_for_cansel = dialog.findViewById(R.id.iv_for_cansel);
        TextView tv_for_text = dialog.findViewById(R.id.tv_for_text);
        tv_for_text.setText("Do you want to delete chat");

        iv_for_cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deteleMsg();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void deteleMsg() {

        if (keys != null && keys.size() != 0) {
            for (int i = 0; i < keys.size(); i++) {
                if (!chattings.get(i).deleteby.equals(session.getUserID())) {
                    if (chattings.get(i).deleteby.equals("")) {
                        chatRef.child(keys.get(i)).child("deleteby").setValue(session.getUserID());
                    } else {
                        chatRef.child(keys.get(i)).child("deleteby").setValue("Both");
                    }
                    iv_for_delete.setClickable(false);
                }
            }
            keys.clear();
        }
        FirebaseDatabase.getInstance().getReference().child("history").child(session.getUserID()).child(uID).child("deleteby").setValue(session.getUserID());

        chattings.clear();
        chatAdapter.notifyDataSetChanged();

    }

    private void getBlockList() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                blockBy = dataSnapshot.getValue(String.class);

                if (blockBy.equals("")) {
                    iv_for_block.setImageResource(R.drawable.ic_block_white);
                } else if (blockBy.equals(session.getUserID())) {
                    iv_for_block.setImageResource(R.drawable.ic_block_red);
                } else if (blockBy.equals("Both")) {
                    iv_for_block.setImageResource(R.drawable.ic_block_red);
                } else if (blockBy.equals(uID)) {
                    iv_for_block.setImageResource(R.drawable.ic_block_white);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                blockBy = dataSnapshot.getValue(String.class);

                if (blockBy.equals("")) {
                    iv_for_block.setImageResource(R.drawable.ic_block_white);
                } else if (blockBy.equals(session.getUserID())) {
                    iv_for_block.setImageResource(R.drawable.ic_block_red);
                } else if (blockBy.equals("Both")) {
                    iv_for_block.setImageResource(R.drawable.ic_block_red);
                } else if (blockBy.equals(uID)) {
                    iv_for_block.setImageResource(R.drawable.ic_block_white);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_for_send:
                if (imageUri != null) {
                    uploadImage();

                   /* if (blockBy.equals("")) {
                        uploadImage();
                        imageUri = null;
                        photoURI = null;
                    } else if (blockBy.equals(session.getUserID())) {
                        Toast.makeText(getContext(), "You blocked " + fullname + ". " + "Can't send any message", Toast.LENGTH_SHORT).show();
                    } else if (!blockBy.equals("")) {
                        Toast.makeText(getContext(), "You are blocked by " + fullname + ". " + "Can't send any message.", Toast.LENGTH_SHORT).show();
                    }*/

                } else {
                    String txt = et_for_sendTxt.getText().toString().trim();
                    if (!txt.equals("")) {

                        Chatting chatModel = new Chatting();
                        chatModel.message = txt;
                        chatModel.timeStamp = ServerValue.TIMESTAMP;
                        chatModel.uid = session.getUserID();
                        chatModel.firebaseToken = FirebaseInstanceId.getInstance().getToken();
                        chatModel.name = session.getFullName();
                        chatModel.deleteby = "";

                        writeToDBProfiles(chatModel);

                        et_for_sendTxt.setText("");

                       /* if (blockBy.equals("")) {
                            //writeToDBProfiles(chatModel, chatModel2, session.getUserID());
                            et_for_sendTxt.setText("");
                        } else if (blockBy.equals(session.getUserID())) {
                            Toast.makeText(getContext(), "You blocked " + fullname + ". " + "Can't send any message", Toast.LENGTH_SHORT).show();
                        } else if (!blockBy.equals("")) {
                            Toast.makeText(getContext(), "You are blocked by " + session.getFullName() + ". " + "Can't send any message.", Toast.LENGTH_SHORT).show();
                        }*/

                    } else {
                        Toast.makeText(getContext(), "Please enter text", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iv_for_pickImage:
                if (blockBy.equals("")) {
                    imageUri = null;
                    photoURI = null;
                    userImageClick();
                } else if (blockBy.equals(session.getUserID())) {
                    Toast.makeText(getContext(), "You blocked " + fullname + ". " + "Can't send any image", Toast.LENGTH_SHORT).show();
                } else if (!blockBy.equals("")) {
                    Toast.makeText(getContext(), "You are blocked by " + session.getFullName() + ". " + "Can't send any image.", Toast.LENGTH_SHORT).show();
                }
                break;
            /*case R.id.iv_for_delete:
                chatDeleteDialog();
                break;
            case R.id.iv_for_block:
                blockUserDialog();
                break;*/
        }
    }

    public void mySuffererAPI() {

        if (Utils.isNetworkAvailable(getContext())) {
            final Dialog pDialog = new Dialog(getContext());
            Constant.myDialog(getContext(), pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "mySufferer", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);
                    Log.e("Response", data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equalsIgnoreCase("success")) {
                            String mySufferer = jsonObject.getString("mySufferer");
                            MySuffererList mySuffererList = new Gson().fromJson(mySufferer.toString(), MySuffererList.class);
                            uID = mySuffererList.userId;


                            if (uID != null) {
                                if (Integer.parseInt(uID) > Integer.parseInt(session.getUserID())) {
                                    chatNode = session.getUserID() + "_" + uID;
                                } else {
                                    chatNode = uID + "_" + session.getUserID();
                                }
                            }

                            chatRef = FirebaseDatabase.getInstance().getReference().child("chat_rooms/" + chatNode);
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("BlockUsers/" + chatNode);

                            getMessageList(pDialog);
                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.i("Error", networkResponse + "");
                    Constant.errorHandle(error, getActivity());
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("authToken", session.getAuthToken());
                    return headers;
                }

            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
        } else {
            Constant.snackbar(iv_for_send, getResources().getString(R.string.check_net_connection));
        }
    }
}
