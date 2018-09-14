package tbi.org.chat.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ValueEventListener;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tbi.org.R;
import tbi.org.activity.main_activity.CaretakerHomeActivity;
import tbi.org.chat.CustomComprator_Chat_Histroy;
import tbi.org.chat.adapter.ChatAdapter;
import tbi.org.chat.model.BlockUsers;
import tbi.org.chat.model.Chatting;
import tbi.org.chat.model.MessageCount;
import tbi.org.fcm.FcmNotificationBuilder;
import tbi.org.helper.ImageRotator;
import tbi.org.helper.SendImageOnFirebase;
import tbi.org.model.MySuffererList;
import tbi.org.session.Session;
import tbi.org.util.Constant;
import tbi.org.util.Utils;
import tbi.org.vollyemultipart.VolleyMultipartRequest;
import tbi.org.vollyemultipart.VolleySingleton;

public class MessageCaretakerFragment extends Fragment implements View.OnClickListener {

    private ImageView iv_for_send, iv_for_pickImage, iv_for_deleteChat, iv_for_block;
    private EditText et_for_sendTxt;
    private TextView tv_for_noChat;
    private RecyclerView recycler_view;
    private Session session;
    private ArrayList<Chatting> chattings;
    private HashMap<String, Chatting> chat_map;
    private ChatAdapter chatAdapter;
    private String fullname, uID, chatNode, OtherFirebaseToken, blockBy = "";
    private DatabaseReference chatRef, databaseReference, msgCountRef, msgCountRefMy;
    private Uri imageUri, photoURI;
    private FirebaseStorage storage;
    private ArrayList<String> keys;
    private Dialog pDialog;
    private boolean isCamera;
    private CoordinatorLayout coordinateLay;
    private int test = 1;

    public MessageCaretakerFragment() {
        // Required empty public constructor
    }

    public static MessageCaretakerFragment newInstance() {
        return new MessageCaretakerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_message_caretaker, container, false);

        storage = FirebaseStorage.getInstance();
        keys = new ArrayList<>();
        session = new Session(getContext());

        initView(view);
        mySuffererAPI();

        iv_for_send.setOnClickListener(this);
        iv_for_pickImage.setOnClickListener(this);
        iv_for_deleteChat.setOnClickListener(this);
        iv_for_block.setOnClickListener(this);

        chattings = new ArrayList<>();
        chat_map = new HashMap<>();
        chatAdapter = new ChatAdapter(chattings, getContext());
        recycler_view.setAdapter(chatAdapter);

        if (keys.size() < 0) {
            iv_for_deleteChat.setClickable(true);
        } else {
            iv_for_deleteChat.setClickable(false);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getMessageList() {
        chattings.clear();
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chatting messageOutput = dataSnapshot.getValue(Chatting.class);

                if (messageOutput.deleteby.equals("") || messageOutput.deleteby.equals(uID)) {
                    pDialog.dismiss();
                    chat_map.put(dataSnapshot.getKey(), messageOutput);
                    chattings.add(messageOutput);
                    tv_for_noChat.setVisibility(View.GONE);
                    keys.add(dataSnapshot.getKey());
                    iv_for_deleteChat.setClickable(true);
                }
                recycler_view.scrollToPosition(chattings.size() - 1);
                Collections.sort(chattings, new CustomComprator_Chat_Histroy());
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Chatting messageOutput = dataSnapshot.getValue(Chatting.class);
                chat_map.remove(dataSnapshot.getKey());
                if (messageOutput.deleteby.equals("") || messageOutput.deleteby.equals(uID)) {
                    chat_map.put(dataSnapshot.getKey(), messageOutput);
                    chattings.clear();
                    chattings.addAll(chat_map.values());
                }
                Collections.sort(chattings, new CustomComprator_Chat_Histroy());
                chatAdapter.notifyDataSetChanged();
                recycler_view.scrollToPosition(chattings.size() - 1);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                chat_map.remove(dataSnapshot.getKey());
                chattings.clear();
                chattings.addAll(chat_map.values());
                Collections.sort(chattings, new CustomComprator_Chat_Histroy());
                chatAdapter.notifyDataSetChanged();
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
        tv_for_noChat = view.findViewById(R.id.tv_for_noChat);

        iv_for_block = getActivity().getWindow().getDecorView().findViewById(R.id.iv_for_block);
        iv_for_deleteChat = getActivity().getWindow().getDecorView().findViewById(R.id.iv_for_deleteChat);
        coordinateLay = getActivity().getWindow().getDecorView().findViewById(R.id.coordinateLay);
    }

    private void writeToDBProfiles(Chatting chatModel) {

        chatRef.push().setValue(chatModel);
        String message;
        if (chatModel.message.contains("tbicaretaker-e76f6.appspot.com")) {
            message = "Image";
        } else {
            message = chatModel.message;
        }
        sendPushNotificationToReceiver(message, session.getUserID(), OtherFirebaseToken);
    }

    private void userImageClick() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_take_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout layout_for_camera = dialog.findViewById(R.id.layout_for_camera);
        LinearLayout layout_for_gallery = dialog.findViewById(R.id.layout_for_gallery);
        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        EnableRuntimePermission();

        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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

    private void picImage(Dialog dialog) {
        if (!SendImageOnFirebase.appManifestContainsPermission(getContext(), Manifest.permission.CAMERA) || SendImageOnFirebase.hasCameraAccess(getContext())) {
            Intent takePhotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra("return-data", true);
            Uri uri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName()
                    + ".fileprovider", SendImageOnFirebase.getTemporalFile(getContext()));
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            getActivity().startActivityForResult(takePhotoIntent, Constant.CAMERA);
            isCamera = true;
            dialog.dismiss();
        }
    }


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

        if (requestCode == Constant.GALLERY && resultCode == Constant.RESULT_OK && null != data) {
            imageUri = data.getData();
            if (imageUri != null) {
                uploadImage();
            }
        } else if (requestCode == Constant.CAMERA && resultCode == Constant.RESULT_OK) {

            Bitmap bm;
            File imageFile = SendImageOnFirebase.getTemporalFile(getContext());
            photoURI = Uri.fromFile(imageFile);

            bm = SendImageOnFirebase.getImageResized(getContext(), photoURI);
            int rotation = ImageRotator.getRotation(getContext(), photoURI, isCamera);
            bm = ImageRotator.rotate(bm, rotation);

            File file = new File(getActivity().getExternalCacheDir(), UUID.randomUUID() + ".jpg");
            imageUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()
                    + ".fileprovider", file);

            if (file != null) {
                try {
                    OutputStream outStream;
                    outStream = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 80, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (imageUri != null) {
                uploadImage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    } // onActivityResult

    private void uploadImage() {

        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(getString(R.string.upload));
            progressDialog.show();

            StorageReference storageReference = storage.getReference();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Uri fireBaseUri = taskSnapshot.getDownloadUrl();

                            Chatting chatModel = new Chatting();
                            chatModel.message = fireBaseUri.toString();
                            chatModel.timeStamp = ServerValue.TIMESTAMP;
                            chatModel.uid = session.getUserID();
                            chatModel.firebaseToken = FirebaseInstanceId.getInstance().getToken();
                            chatModel.name = session.getFullName();
                            chatModel.deleteby = "";

                            writeToDBProfiles(chatModel);

                            imageUri = null;
                            photoURI = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
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

    private void sendPushNotificationToReceiver(String message, String userID, String otherFirebaseToken) {
        FcmNotificationBuilder.initialize().title(session.getFullName())
                .message(message).clickaction("ChatActivity").username(session.getFullName())
                .receiverFirebaseToken(otherFirebaseToken)
                .uid(userID).send();
    }

    private void blockUserDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dailog_delete_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_yes = dialog.findViewById(R.id.btn_for_yes);
        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        final TextView tv_for_txt = dialog.findViewById(R.id.tv_for_txt);
        TextView title = dialog.findViewById(R.id.title);
        title.setText(R.string.block);

        final BlockUsers blockUsers = new BlockUsers();
        if (blockBy.equals("")) {
            blockUsers.blockedBy = session.getUserID();
            tv_for_txt.setText(R.string.block_user);
        } else if (blockBy.equals("Both")) {
            blockUsers.blockedBy = uID;
            tv_for_txt.setText(R.string.unblock_user);
        } else if (blockBy.equals(session.getUserID())) {
            blockUsers.blockedBy = "";
            tv_for_txt.setText(R.string.unblock_user);
        } else if (blockBy.equals(uID)) {
            blockUsers.blockedBy = "Both";
            tv_for_txt.setText(R.string.block_user);
        }

        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_yes.setOnClickListener(new View.OnClickListener() {
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
        dialog.setContentView(R.layout.dailog_delete_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btn_for_yes = dialog.findViewById(R.id.btn_for_yes);
        ImageView layout_for_crossDailog = dialog.findViewById(R.id.layout_for_crossDailog);
        final TextView tv_for_txt = dialog.findViewById(R.id.tv_for_txt);
        tv_for_txt.setText(R.string.delete_chat);

        layout_for_crossDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_for_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deteleMsg();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deteleMsg() {
        if (keys != null && keys.size() != 0) {
            for (int i = 0; i < keys.size(); i++) {
                if (!chattings.get(i).deleteby.equals(session.getUserID())) {
                    if (chattings.get(i).deleteby.equals("")) {
                        chatRef.child(keys.get(i)).child("deleteby").setValue(session.getUserID());
                    } else {
                        chatRef.child(keys.get(i)).setValue(null);
                    }
                    iv_for_deleteChat.setClickable(false);
                }
            }
            keys.clear();
        }
        chattings.clear();
        chatAdapter.notifyDataSetChanged();
    }

    private void getBlockList() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                blockBy = dataSnapshot.getValue(String.class);

                if (blockBy.equals("")) {
                    iv_for_block.setImageResource(R.drawable.ic_blocked);
                } else if (blockBy.equals(session.getUserID())) {
                    iv_for_block.setImageResource(R.drawable.ic_blocked_red);
                } else if (blockBy.equals("Both")) {
                    iv_for_block.setImageResource(R.drawable.ic_blocked_red);
                } else if (blockBy.equals(uID)) {
                    iv_for_block.setImageResource(R.drawable.ic_blocked);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                blockBy = dataSnapshot.getValue(String.class);

                if (blockBy.equals("")) {
                    iv_for_block.setImageResource(R.drawable.ic_blocked);
                } else if (blockBy.equals(session.getUserID())) {
                    iv_for_block.setImageResource(R.drawable.ic_blocked_red);
                } else if (blockBy.equals("Both")) {
                    iv_for_block.setImageResource(R.drawable.ic_blocked_red);
                } else if (blockBy.equals(uID)) {
                    iv_for_block.setImageResource(R.drawable.ic_blocked);
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

    private void messageCount() {
        msgCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MessageCount messageCount = dataSnapshot.getValue(MessageCount.class);
                try {
                    if (test != 1) {
                        if (messageCount == null) messageCount = new MessageCount().setValue(0);
                        int value = (Integer.parseInt(messageCount.count) + 1);
                        msgCountRef.setValue(messageCount.setValue(value));
                    }
                    test = 2;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    msgCountRefMy.setValue(new MessageCount().setValue(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                String txt = et_for_sendTxt.getText().toString().trim();
                if (!txt.equals("")) {

                    Chatting chatModel = new Chatting();
                    chatModel.message = txt;
                    chatModel.timeStamp = ServerValue.TIMESTAMP;
                    chatModel.uid = session.getUserID();
                    chatModel.firebaseToken = FirebaseInstanceId.getInstance().getToken();
                    chatModel.name = session.getFullName();
                    chatModel.deleteby = "";

                    if (blockBy.equals("")) {
                        messageCount();
                        writeToDBProfiles(chatModel);
                        et_for_sendTxt.setText("");
                    } else if (blockBy.equals(session.getUserID())) {
                        Constant.snackbarTop(coordinateLay, "You blocked " + fullname + ". " + "Can't send any message.");
                    } else if (!blockBy.equals("")) {
                        Constant.snackbarTop(coordinateLay, "You are blocked by " + session.getFullName() + ". " + "Can't send any message.");
                    }

                } else {
                    Constant.snackbarTop(coordinateLay, getResources().getString(R.string.enter_text));
                }
                break;
            case R.id.iv_for_pickImage:
                if (blockBy.equals("")) {
                    imageUri = null;
                    photoURI = null;
                    userImageClick();
                } else if (blockBy.equals(session.getUserID())) {
                    Constant.snackbarTop(coordinateLay, "You blocked " + fullname + ". " + "Can't send any image.");
                } else if (!blockBy.equals("")) {
                    Constant.snackbarTop(coordinateLay, "You are blocked by " + session.getFullName() + ". " + "Can't send any image.");
                }
                break;
            case R.id.iv_for_deleteChat:
                chatDeleteDialog();
                break;
            case R.id.iv_for_block:
                blockUserDialog();
                break;
        }
    }

    public void mySuffererAPI() {

        if (Utils.isNetworkAvailable(getContext())) {
            pDialog = new Dialog(getContext());
            Constant.myDialog(getContext(), pDialog);
            pDialog.show();

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.GET, Constant.URL_WITH_LOGIN + "mySufferer", new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String data = new String(response.data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("success")) {
                            String mySufferer = jsonObject.getString("mySufferer");
                            MySuffererList mySuffererList = new Gson().fromJson(mySufferer, MySuffererList.class);
                            uID = mySuffererList.userId;
                            fullname = mySuffererList.name;

                            if (uID != null) {
                                if (Integer.parseInt(uID) > Integer.parseInt(session.getUserID())) {
                                    chatNode = session.getUserID() + "_" + uID;
                                } else {
                                    chatNode = uID + "_" + session.getUserID();
                                }
                            }

                            chatRef = FirebaseDatabase.getInstance().getReference().child("chat_rooms/" + chatNode);
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("block_users/" + chatNode);
                            msgCountRef = FirebaseDatabase.getInstance().getReference().child("massage_count/" + uID);
                            msgCountRefMy = FirebaseDatabase.getInstance().getReference().child("massage_count/" + session.getUserID());
                            msgCountRefMy.setValue(new MessageCount().setValue(0));
                            ((CaretakerHomeActivity) getContext()).navigationAdapter.notifyDataSetChanged();

                            getBlockList();
                            getMessageList();

                            FirebaseDatabase.getInstance().getReference().child("users").child(uID).child("firebaseToken").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
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

                            messageCount();
                        } else {
                            Constant.snackbar(recycler_view, getString(R.string.no_sufferer));
                            pDialog.dismiss();
                        }

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Constant.errorHandle(error, getActivity());
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
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
