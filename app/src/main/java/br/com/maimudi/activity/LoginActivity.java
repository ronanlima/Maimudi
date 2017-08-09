package br.com.maimudi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.model.User;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 11/03/16.
 */
public class LoginActivity extends BaseActivity{

    private static final String TAG = LoginActivity.class.getCanonicalName().toUpperCase();

    private LinearLayout ll_login,ll_choose_login, ll_choose_newUser,ll_forgot_password,ll_email_recovery;
    private Button btnCriarConta,btnEntrarLogin,btnForgotPassword,btnEmailRecovery;
    private EditText senha,email,usuarioLogin,senhaLogin,emailForgotPassword,oldSenhaForgotPassword,newSenhaForgotPassword,emailRecovery;
    private TextView btnViewLogar,btnViewNewUser, esqueceuSenhaTelaLogin;
    private LoginButton btnLoginFacebook;
    private CallbackManager callbackManager;
    private DatabaseReference ref;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private boolean entrarAutomaticamente = true;
    private Intent intent;
    private User loginUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ChildEventListener childEventListener;
    private User userMudi;
    private Context context = this;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        try {
            PackageInfo info = getPackageManager().getPackageInfo("br.com.maimudi", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Keyhash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e){

        } catch (NoSuchAlgorithmException ex){

        }
        mAuth = FirebaseAuth.getInstance();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    if (loginUser != null){
                        loginUser.setUid(user.getUid());
                        MaiMudiApplication.getInstance().setLoggedUser(loginUser);
                        intent = new Intent(LoginActivity.this, MainTab.class);
                        intent.putExtra("isRecuperarImagemStorage", true);
                        startActivity(intent);
                        finish();
                    } else {
                        if(userMudi == null){
                            userMudi = new User();
                            userMudi.setEmail(user.getEmail().toString());
                            MaiMudiApplication.getInstance().setLoggedUser(userMudi);
                        }

                        if(entrarAutomaticamente == false){
                            intent = new Intent(LoginActivity.this, EditProfileActivity.class);
                            startActivity(intent);

                            if(getSimpleArcDialog() != null) {
                                getSimpleArcDialog().dismiss();
                            }
                            if(childEventListener != null) {
                                ref.removeEventListener(childEventListener);
                            }
                        }else{
                            intent = new Intent(LoginActivity.this, MainTab.class);
                            intent.putExtra("isRecuperarImagemStorage",true);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        ll_login = (LinearLayout) findViewById(R.id.login);
        ll_choose_login = (LinearLayout) findViewById(R.id.chooseLogin);
        ll_choose_newUser = (LinearLayout) findViewById(R.id.cadastro);
        ll_forgot_password = (LinearLayout) findViewById(R.id.forgot);
        ll_email_recovery = (LinearLayout) findViewById(R.id.emailRecovery);

        configLoginFB();
        configLoginUserCadastrado();
        configNewUser();
        btnsAuxTelaPrincipal();
    }

    private void btnsAuxTelaPrincipal() {
        btnViewLogar = (TextView) findViewById(R.id.sign_in);
        btnViewLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_login.setVisibility(View.VISIBLE);
                ll_choose_login.setVisibility(View.GONE);
                ll_choose_newUser.setVisibility(View.GONE);
                ll_forgot_password.setVisibility(View.GONE);
                ll_email_recovery.setVisibility(View.GONE);
            }
        });

        btnViewNewUser = (TextView) findViewById(R.id.btn_cadastrar);
        btnViewNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                ll_login.setVisibility(View.GONE);
                ll_choose_login.setVisibility(View.GONE);
                ll_choose_newUser.setVisibility(View.VISIBLE);
                ll_forgot_password.setVisibility(View.GONE);
                ll_email_recovery.setVisibility(View.GONE);
            }
        });

        //ESQUECEU SENHA
        emailRecovery = (EditText) ll_email_recovery.findViewById(R.id.email_recuperacao_senha);
        btnEmailRecovery = (Button) ll_email_recovery.findViewById(R.id.btnFowardEmailRecovery);
        btnEmailRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog(context, context.getResources().getString(R.string.label_env_email));
                getSimpleArcDialog().show();
                sendEmailRecovery();
            }
        });

        newSenhaForgotPassword = (EditText) ll_forgot_password.findViewById(R.id.nova_senha_recuperacao_senha);
        oldSenhaForgotPassword = (EditText) ll_forgot_password.findViewById(R.id.old_senha_recuperacao_senha);
        btnForgotPassword = (Button) ll_forgot_password.findViewById(R.id.btn_redefinir_senha);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog(context, context.getResources().getString(R.string.label_redef_senha));
                getSimpleArcDialog().show();
                forgotPassword();
            }
        });
    }

    private void configNewUser() {
        email = (EditText) ll_choose_newUser.findViewById(R.id.email);
        senha = (EditText) ll_choose_newUser.findViewById(R.id.senha);

        btnCriarConta = (Button) ll_choose_newUser.findViewById(R.id.btn_criar_conta);
        btnCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog(context, context.getResources().getString(R.string.label_criando_mudi));
                getSimpleArcDialog().show();
                entrarAutomaticamente = false;
                createUser();
            }
        });
    }

    private void configLoginUserCadastrado() {
        usuarioLogin = (EditText) ll_login.findViewById(R.id.nick);
        senhaLogin = (EditText) ll_login.findViewById(R.id.senha);
        btnEntrarLogin = (Button) ll_login.findViewById(R.id.btn_logar_tela_login);
        btnEntrarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog(context, context.getResources().getString(R.string.label_login_mudi));
                getSimpleArcDialog().show();
                entrarAutomaticamente = false;
                findUserAndLogin(usuarioLogin.getText().toString());
            }
        });
        esqueceuSenhaTelaLogin = (TextView) ll_login.findViewById(R.id.esqueci_senha_tela_login);
        esqueceuSenhaTelaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_login.setVisibility(View.GONE);
                ll_choose_login.setVisibility(View.GONE);
                ll_choose_newUser.setVisibility(View.GONE);
                ll_forgot_password.setVisibility(View.GONE);
                ll_email_recovery.setVisibility(View.VISIBLE);
            }
        });
    }

    private void configLoginFB() {
        callbackManager = CallbackManager.Factory.create();
        btnLoginFacebook = (LoginButton) findViewById(R.id.login_button_facebook);
        btnLoginFacebook.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                entrarAutomaticamente = false;
                setupDialog(context, context.getResources().getString(R.string.label_login_mudi));
                getSimpleArcDialog().show();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            String name = object.getString("name");
                            String picture = object.getJSONObject("picture").getJSONObject("data").getString("url");

                            userMudi = new User(email,name,picture);
                            MaiMudiApplication.getInstance().setLoggedUser(userMudi);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name,gender,last_name,birthday,location,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if (ll_choose_login.getVisibility() == View.VISIBLE){
            finish();
        } else {
            ll_login.setVisibility(View.GONE);
            ll_choose_login.setVisibility(View.VISIBLE);
            ll_choose_newUser.setVisibility(View.GONE);
            ll_forgot_password.setVisibility(View.GONE);
            ll_email_recovery.setVisibility(View.GONE);
        }
    }

    private void createUser(){
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.e(TAG, task.getException().getMessage());
                            getSimpleArcDialog().dismiss();
                            setupSnackBar(context.getResources().getString(R.string.msg_erro_criar_mudi));
                            snackbar.show();
                        }
                    }
        });
    }

    private void forgotPassword(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(newSenhaForgotPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });

      /*  ref = FirebaseDatabase.getInstance().getReference();
        ref.changePassword(emailForgotPassword.getText().toString(), oldSenhaForgotPassword.getText().toString(), newSenhaForgotPassword.getText().toString(), new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                getSimpleArcDialog().dismiss();
                Log.i("Firebase", "Nova senha cadastrado com sucesso");
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                getSimpleArcDialog().dismiss();
                Log.e("Firebase", firebaseError.getMessage());
                setupSnackBar("Não foi possível redefinir sua senha , tente novamente mais tarde!");
                snackbar.show();
            }
        });*/
    }

    private void sendEmailRecovery(){
        mAuth.sendPasswordResetEmail(emailRecovery.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            getSimpleArcDialog().dismiss();
                            ll_login.setVisibility(View.GONE);
                            ll_choose_login.setVisibility(View.GONE);
                            ll_choose_newUser.setVisibility(View.GONE);
                            ll_forgot_password.setVisibility(View.VISIBLE);
                            ll_email_recovery.setVisibility(View.GONE);
                        }else{
                            getSimpleArcDialog().dismiss();
                            Log.e("Firebase", task.getException().getMessage());
                            setupSnackBar(context.getResources().getString(R.string.label_duvida_email_muder));
                            snackbar.show();
                        }
                    }
                });
    }

    private void setupSnackBar(String text){
        snackbar = Snackbar
                .make(coordinatorLayout, text, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.white));
    }

    private void clear(){
        email.setText("");
        senha.setText("");
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.e(TAG, task.getException().getMessage());
                            setupSnackBar(context.getResources().getString(R.string.msg_falha_login_facebook));
                            snackbar.show();
                        }
                    }
                });
    }

    private void findUserAndLogin(final String usuario) {
        ref = FirebaseDatabase.getInstance().getReferenceFromUrl(getResources().getString(R.string.firebase_url)).child(getResources().getString(R.string.ref_firebase_users));
        ref.orderByChild("nickname").equalTo(usuario).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    loginUser = postSnapshot.getValue(User.class);
                }

                if (loginUser != null) {
                    mAuth.signInWithEmailAndPassword(loginUser.getEmail().toString(), senhaLogin.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    if (!task.isSuccessful()) {
                                        getSimpleArcDialog().dismiss();
                                        Log.e(TAG, task.getException().getMessage());
                                        setupSnackBar(context.getResources().getString(R.string.msg_falha_login_mudi));
                                        snackbar.show();
                                    } else {
                                        entrarAutomaticamente = true;
                                    }
                                }
                            });
                }else{
                    getSimpleArcDialog().dismiss();
                    setupSnackBar(context.getResources().getString(R.string.msg_muder_nao_encontrado));
                    snackbar.show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
