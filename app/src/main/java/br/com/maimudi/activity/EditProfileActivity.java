package br.com.maimudi.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.onesignal.OneSignal;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by brunolemgruber on 13/05/16.
 */
@RuntimePermissions
public class EditProfileActivity extends Activity implements Validator.ValidationListener {

    private static final String TAG = EditProfileActivity.class.getCanonicalName().toUpperCase();
    public static final String PROFILE = "Profile";
    public static final String BACKGROUND = "Background";
    public static final String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Validator validator;
    private ImageView imageViewLogo, imageViewBackground;
    private CircleImageView circleImageView;
    private LinearLayout llImageProfileCrop, llImageBackGroundCrop;
    private String typeImage;
    private ImageView backGroundImageView;
    private User loggedUser;
    private EditText email;
    @NotEmpty(trim = true, messageResId = R.string.msg_validacao_nome)
    private EditText name;
    @NotEmpty(trim = true, messageResId = R.string.msg_validacao_nome_usuario)
    private EditText nickname;
    private TextView btnSave, txtImgBackground, txtImgProfile;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private ChildEventListener childEventListener;
    private SimpleArcDialog simpleArcDialog;
    private ArcConfiguration arcConfiguration;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Intent intentCropImgProfile, intentCropImgBackground;
    private OnProgressStorage listenerProgressStorage;
    private boolean isAtingiuPorcentagem = false, isEnviouImgProfile = false, isEnviouImgBackground = false;
    private String urlDownloadImgProfile;
    private String urlDownloadImgBackground;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_profile);
        validator = new Validator(this);
        validator.setValidationListener(this);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = firebaseStorage.getReferenceFromUrl(getResources().getString(R.string.storage_firebase_url)).child(getResources().getString(R.string.ref_firebase_users)).child(firebaseUser.getUid());
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        loggedUser = MaiMudiApplication.getInstance().getLoggedUser();

        btnSave = (TextView) findViewById(R.id.save);
        txtImgBackground = (TextView) findViewById(R.id.txt_choose_img_background);
        txtImgProfile = (TextView) findViewById(R.id.txt_choose_img_profile);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        email = (EditText) findViewById(R.id.email);
        email.setText(loggedUser.getEmail());
        nickname = (EditText) findViewById(R.id.user);
        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                nickname.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findUserValidate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        name = (EditText) findViewById(R.id.name);
        if (loggedUser.getNome() != null) {
            name.setText(loggedUser.getNome());
        }
        if (loggedUser.getNickname() != null) {
            nickname.setText(loggedUser.getNickname());
            nickname.setEnabled(false);
        }

        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        llImageProfileCrop = (LinearLayout) findViewById(R.id.ll_image_profile);
        backGroundImageView = (ImageView) findViewById(R.id.image_background);
        llImageBackGroundCrop = (LinearLayout) findViewById(R.id.ll_image_background);
        imageViewLogo = (ImageView) findViewById(R.id.logo);
        setClickListenerImages(imageViewLogo);
        imageViewBackground = (ImageView) findViewById(R.id.background);
        setClickListenerImages(imageViewBackground);

        if (loggedUser.getProfile_img() != null) {
            Glide.with(this).load(loggedUser.getProfile_img()).into(circleImageView);
            circleImageView.setVisibility(View.VISIBLE);
            imageViewLogo.setVisibility(View.GONE);
            txtImgProfile.setText(context.getResources().getString(R.string.msg_alterar_img_perfil));
        } else if (loggedUser.getImageProfile() != null) {
            circleImageView.setImageDrawable(loggedUser.getImageProfile().getDrawable());
            circleImageView.setVisibility(View.VISIBLE);
            setClickListenerImages(circleImageView);
            imageViewLogo.setVisibility(View.GONE);
            txtImgProfile.setText(context.getResources().getString(R.string.msg_alterar_img_perfil));
        }
        if (loggedUser.getImageBackground() != null) {
            backGroundImageView.setImageDrawable(loggedUser.getImageBackground().getDrawable());
            backGroundImageView.setVisibility(View.VISIBLE);
            setClickListenerImages(backGroundImageView);
            txtImgBackground.setText(context.getResources().getString(R.string.msg_alterar_img_fundo));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EditProfileActivityPermissionsDispatcher.onRequestPermissionsResult(EditProfileActivity.this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void getAllGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.label_selecione_imagem)), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.label_falha_selecione_imagem), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = typeImage + "_maimudi_image.png";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        UCrop.Options options = new UCrop.Options();

        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setToolbarColor(getResources().getColor(R.color.colorAccent));
        options.setToolbarTitle(getResources().getString(R.string.label_edit_foto_muder));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent));
        options.setDimmedLayerColor(getResources().getColor(R.color.colorPrimary));
        options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.start(EditProfileActivity.this);
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            if (typeImage.equalsIgnoreCase(PROFILE)) {
                intentCropImgProfile = result;
                circleImageView.setImageURI(UCrop.getOutput(result));
                circleImageView.setVisibility(View.VISIBLE);
                llImageProfileCrop.setVisibility(View.GONE);
                setClickListenerImages(circleImageView);
                enviaImagesFirebase(result, getResources().getString(R.string.ref_storage_profile_perfil));
            } else {
                intentCropImgBackground = result;
                backGroundImageView.setImageURI(UCrop.getOutput(result));
                llImageBackGroundCrop.setVisibility(View.GONE);
                setClickListenerImages(backGroundImageView);
                enviaImagesFirebase(result, getResources().getString(R.string.ref_storage_profile_background));
            }
        } else {
            if (typeImage.equalsIgnoreCase(PROFILE)) {
                intentCropImgProfile = null;
            } else {
                intentCropImgBackground = null;
            }
            Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.msg_falha_cortar_imagem), Toast.LENGTH_SHORT).show();
        }
    }

    private void setClickListenerImages(final ImageView view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeImage = (view.getId() == R.id.profile_image || view.getId() == R.id.logo) ? PROFILE : BACKGROUND;
                EditProfileActivityPermissionsDispatcher.getAllGalleryWithCheck(EditProfileActivity.this);
            }
        });
    }

    private void setupSnackBar(String text) {
        snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.white));
    }

    private void setupDialog(String text) {
        int[] colors = {Color.parseColor("#00C64C")};

        arcConfiguration = new ArcConfiguration(this);
        arcConfiguration.setLoaderStyle(SimpleArcLoader.STYLE.COMPLETE_ARC);
        arcConfiguration.setColors(colors);
        arcConfiguration.setText(text);
        simpleArcDialog = new SimpleArcDialog(this);
        simpleArcDialog.setConfiguration(arcConfiguration);
    }

    private void findUserValidate(final String user) {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                setupSnackBar(getResources().getString(R.string.msg_user_duplicado, user.toString().toUpperCase()));
                snackbar.show();
                EditProfileActivity.this.nickname.setTextColor(Color.RED);
                View view = EditProfileActivity.this.getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        };

        ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_firebase_nickname));
        ref.orderByKey().equalTo(user).addChildEventListener(childEventListener);
    }

    @Override
    public void onValidationSucceeded() {
        if (loggedUser.getNome() == null || loggedUser.getNickname() == null || loggedUser.getNome().isEmpty() || loggedUser.getNickname().isEmpty()) {
            loggedUser.setNome(name.getText().toString());
            loggedUser.setNickname(nickname.getText().toString());
            if (urlDownloadImgProfile != null && !urlDownloadImgProfile.isEmpty()) {
                loggedUser.setProfile_img(urlDownloadImgProfile);
            }
            if (urlDownloadImgProfile != null && !urlDownloadImgProfile.isEmpty()) {
                loggedUser.setBackground_img(urlDownloadImgBackground);
            }
            if(loggedUser.getOne_signal_id() == null || loggedUser.getOne_signal_id().isEmpty()){
                loggedUser.setOne_signal_id(getOneSignalId());
            }
            setupDialog(getResources().getString(R.string.label_login_succeded));
            simpleArcDialog.show();

            ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_firebase_users));
            ref.child(firebaseUser.getUid()).setValue(loggedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    simpleArcDialog.dismiss();
                    if (!task.isComplete()) {
                        setupDialog(context.getResources().getString(R.string.msg_falha_salvar_usuario));
                        simpleArcDialog.setCancelable(true);
                    } else {
                        redirecionaParaMainTab(urlDownloadImgProfile != null ? true : urlDownloadImgBackground != null ? true : false);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    setupDialog(context.getResources().getString(R.string.msg_falha_salvar_usuario));
                }
            });

            ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_firebase_nickname));
            ref.child(firebaseUser.getUid()).child(loggedUser.getNickname()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ref.removeEventListener(childEventListener);
                }
            });
        } else {
            if (!name.getText().toString().equalsIgnoreCase(loggedUser.getNome())) {
                loggedUser.setNome(name.getText().toString());
                if (urlDownloadImgProfile != null && !urlDownloadImgProfile.isEmpty()) {
                    loggedUser.setProfile_img(urlDownloadImgProfile);
                }
                setupDialog(context.getResources().getString(R.string.label_atualizando));
                simpleArcDialog.show();
                ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.ref_firebase_users));
                ref.child(firebaseUser.getUid()).child(context.getResources().getString(R.string.user_nome)).setValue(name.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        simpleArcDialog.dismiss();
                        if (!task.isComplete()) {
                            setupDialog(context.getResources().getString(R.string.msg_falha_salvar_usuario));
                            simpleArcDialog.setCancelable(true);
                        } else {
                            redirecionaParaMainTab(false);
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setupDialog(context.getResources().getString(R.string.msg_falha_salvar_usuario));
                    }
                });
            } else {
                redirecionaParaMainTab(false);
            }
        }
    }

    private void enviaImagesFirebase(Intent intent, String refStorage) {
        StorageReference imgProfileReference = storageReference.child(refStorage);
        InputStream stream;
        UploadTask uploadTask;
        listenerProgressStorage = callListener();
        try {
            stream = new FileInputStream(new File(UCrop.getOutput(intent).getPath()));

            uploadTask = imgProfileReference.putStream(stream);
            configDialog(context.getResources().getString(R.string.label_atualizando_imagem), false);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());
                    configDialog(context.getResources().getString(R.string.msg_falha_enviar_imagem), true);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    simpleArcDialog.dismiss();
                    if (typeImage.equals(PROFILE)) {
                        saveImgIntoFirebase(taskSnapshot, true, context.getResources().getString(R.string.user_profile_img));
                    } else {
                        saveImgIntoFirebase(taskSnapshot, false, context.getResources().getString(R.string.user_background_img));
                    }
                    isAtingiuPorcentagem = false;
                    exibeMsgSnackbar(coordinatorLayout, getResources().getString(R.string.msg_img_enviada_sucesso));
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Envio de imagem pausado devido a alguma interferencia do SO.\n" + taskSnapshot.getError());
                    simpleArcDialog.dismiss();
                    exibeMsgSnackbar(coordinatorLayout, getResources().getString(R.string.label_env_img_pausado));
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void exibeMsgSnackbar(CoordinatorLayout coordinatorLayout, String string) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, string, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        snackbar.show();
    }

    private void saveImgIntoFirebase(UploadTask.TaskSnapshot taskSnapshot, boolean isImgProfile, final String childFirebase) {
        if (isImgProfile) {
            urlDownloadImgProfile = taskSnapshot.getDownloadUrl().toString();
            isEnviouImgProfile = true;
            intentCropImgProfile = null;
        } else {
            urlDownloadImgBackground = taskSnapshot.getDownloadUrl().toString();
            isEnviouImgBackground = true;
            intentCropImgBackground = null;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getApplicationContext().getResources().getString(R.string.ref_firebase_users));
        ref.child(UID).child(childFirebase).setValue(taskSnapshot.getDownloadUrl().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, childFirebase + " gravada com sucesso para o usuário '" + UID + "'");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, childFirebase + " não gravada para o usuário '" + UID + "'. Erro = " + e.getMessage());
            }
        });
    }

    private void configDialog(String text, boolean isCancelable) {
        if (simpleArcDialog != null) {
            SimpleArcDialog oldInstance = simpleArcDialog;
            arcConfiguration.setText(text);
            simpleArcDialog = new SimpleArcDialog(this, arcConfiguration);
            oldInstance.dismiss();
        } else {
            setupDialog(text);
        }
        simpleArcDialog.setCancelable(isCancelable);
        simpleArcDialog.show();
    }

    private void redirecionaParaMainTab(boolean isRecuperarImagens) {
        if (urlDownloadImgProfile != null && !urlDownloadImgProfile.isEmpty()) {
            if (isEnviouImgProfile) {
                loggedUser.setImageProfile(circleImageView);
            }
        }
        if (urlDownloadImgBackground != null && !urlDownloadImgBackground.isEmpty()) {
            if (isEnviouImgBackground) {
                loggedUser.setImageBackground(backGroundImageView);
            }
        }
        Intent intent = new Intent(EditProfileActivity.this, MainTab.class);
        intent.putExtra("isRecuperarImagemStorage", isRecuperarImagens);
        startActivity(intent);
        finish();
    }

    private String getOneSignalId(){

        final String[] id = {null};

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug one signal",userId);
                if(userId != null || !userId.isEmpty())
                    id[0] = userId;
            }
        });

        return id[0];
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(view.getContext());

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
        }
    }

    public interface OnProgressStorage extends Serializable {
        void onProgressAtingePorcentagem(String texto, boolean isCancelable);
    }

    public OnProgressStorage callListener() {
        return new OnProgressStorage() {
            @Override
            public void onProgressAtingePorcentagem(String texto, boolean isCancelable) {
                if (!isAtingiuPorcentagem) {
                    configDialog(texto, isCancelable);
                    isAtingiuPorcentagem = true;
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (storageReference != null) {
            outState.putString("storageReference", storageReference.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final String ref = savedInstanceState.getString("storageReference");
        if (ref == null) {
            return;
        }
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(ref);
        List<UploadTask> tasks = storageReference.getActiveUploadTasks();
        if (tasks.size() > 0) {
            UploadTask uploadTask = tasks.get(0);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    configDialog(context.getResources().getString(R.string.msg_img_enviada_sucesso), true);
                }
            });
        }
    }
}
