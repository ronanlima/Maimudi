package br.com.maimudi.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import br.com.maimudi.adapter.TabAdapter;
import br.com.maimudi.model.Friend;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by brunolemgruber on 04/12/15.
 */
public class MainTab extends BaseActivity implements TabLayout.OnTabSelectedListener {

    private static final String TAG = MainTab.class.getCanonicalName().toUpperCase();
    public static final String STORAGE_REFERENCE = "storageReference";
    private FrameLayout frameLayout;
    public ViewPager mViewPager;
    private TabLayout tabLayout;
    private DatabaseReference ref;
    private StorageReference refStorage;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);
        setupToolBar();

        intent = getIntent();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        frameLayout = (FrameLayout) findViewById(R.id.layout_main_tab);
        setmViewPager((ViewPager) findViewById(R.id.viewpager));
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);

        if (intent != null && intent.getExtras() != null && intent.getExtras().getBoolean("isRecuperarImagemStorage")){
            recuperarInfosUsuarioFirebaseDatabase();
        } else {
            updateNomeUsuario(frameLayout);
            continuaCarregamentoTela(!(MaiMudiApplication.getInstance().getLoggedUser().getProfile_img() != null));
        }
    }

    private void continuaCarregamentoTela(boolean isDeveBuscarImgStorage) {
        getmViewPager().setAdapter(new TabAdapter(getSupportFragmentManager()));

        if (isDeveBuscarImgStorage){
            recuperaImagemPerfilUsuario(FirebaseAuth.getInstance().getCurrentUser());
        } else {
            Glide.with(getApplicationContext()).load(MaiMudiApplication.getInstance().getLoggedUser().getProfile_img()).into(getCircleImageView());
            MaiMudiApplication.getInstance().getLoggedUser().setImageProfile(getCircleImageView());
            updateImageProfile(!isDeveBuscarImgStorage);
        }

        int cor = getResources().getColor(R.color.white);

        tabLayout.setTabTextColors(cor, cor);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.timeline));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.friends));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.find_user));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.statics));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.profile));

        tabLayout.setOnTabSelectedListener(this);

        getmViewPager().addOnPageChangeListener(new MyTablayoutOnPageChangeListener(tabLayout));

        getmViewPager().setCurrentItem(0);

        Friend f = new Friend();
        f.setUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ((TabAdapter)getmViewPager().getAdapter()).setFriend(f);
    }

    private class MyTablayoutOnPageChangeListener extends TabLayout.TabLayoutOnPageChangeListener{

        public MyTablayoutOnPageChangeListener(TabLayout tabLayout) {
            super(tabLayout);
        }

        @Override
        public void onPageSelected(int position) {
            TabAdapter tabAdapter = (TabAdapter) getmViewPager().getAdapter();
            tabAdapter.getMediaPlayer().stop();
            tabAdapter.getMediaPlayer().reset();
            //FIXME ronan: existe possibilidade de o id mudar ? esse id corresponde ao viewpager
            if (position < 2){
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if (fm.findFragmentByTag("android:switcher:2131493128:3") != null){
                    ft.detach(fm.findFragmentByTag("android:switcher:2131493128:3"));
                    ft.remove(fm.findFragmentByTag("android:switcher:2131493128:3")).commit();
                }
            }
            super.onPageSelected(position);
        }
    }

    private void recuperarInfosUsuarioFirebaseDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.ref_firebase_users));
        ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(getResources().getString(R.string.ref_firebase_nome)).getValue() == null ||
                        dataSnapshot.child(getResources().getString(R.string.ref_firebase_nickname)).getValue() == null ||
                        dataSnapshot.child(getResources().getString(R.string.ref_firebase_email)).getValue() == null) {
                    Log.d(TAG, "Campos nulos para o Userid = "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainTab.this).setMessage("Houve uma falha ao iniciar o aplicativo. Por favor, faça login novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logouApp();
                        }
                    }).setCancelable(false).setTitle("Erro").setIcon(R.mipmap.ic_alert);
                    AlertDialog alert = dialog.create();
                    alert.show();
                } else {
                    MaiMudiApplication.getInstance().getLoggedUser().setOne_signal_id(dataSnapshot.child(getResources().getString(R.string.ref_firebase_one_signal_id)).getValue().toString());
                    MaiMudiApplication.getInstance().getLoggedUser().setNome(dataSnapshot.child(getResources().getString(R.string.ref_firebase_nome)).getValue().toString());
                    MaiMudiApplication.getInstance().getLoggedUser().setNickname(dataSnapshot.child(getResources().getString(R.string.ref_firebase_nickname)).getValue().toString());
                    if (dataSnapshot.child(getResources().getString(R.string.ref_firebase_url_img_profile)).getValue() != null){
                        MaiMudiApplication.getInstance().getLoggedUser().setProfile_img(dataSnapshot.child(getResources().getString(R.string.ref_firebase_url_img_profile)).getValue().toString());
                    }
                    if (dataSnapshot.child(getResources().getString(R.string.ref_firebase_url_img_background)).getValue() != null){
                        MaiMudiApplication.getInstance().getLoggedUser().setBackground_img(dataSnapshot.child(getResources().getString(R.string.ref_firebase_url_img_background)).getValue().toString());
                    }
                    updateNomeUsuario(frameLayout);
                    continuaCarregamentoTela(!(dataSnapshot.child(getResources().getString(R.string.ref_firebase_url_img_profile)).getValue() != null));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperaImagemPerfilUsuario(FirebaseUser user) {
        refStorage = FirebaseStorage.getInstance().getReferenceFromUrl(getResources().getString(R.string.storage_firebase_url)).child(getResources().getString(R.string.ref_firebase_users)).child(user.getUid()).child(getResources().getString(R.string.ref_storage_profile_perfil));
        final long THRE_MEGABYTE = 1024 * 3072;
        refStorage.getBytes(THRE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                setImageProfile(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar snackbar = Snackbar.make(frameLayout, getBaseContext().getResources().getString(R.string.msg_falha_download_image_profile), Snackbar.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) snackbar.getView();
                group.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                snackbar.show();
                updateImageProfile(false);
            }
        });
    }

    private void setImageProfile(Bitmap bitmap) {
        ImageView imgView = new ImageView(getApplicationContext());
        imgView.setImageBitmap(bitmap);
        MaiMudiApplication.getInstance().getLoggedUser().setImageProfile(imgView);
        updateImageProfile(true);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        getmViewPager().setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (refStorage != null){
            outState.putString(STORAGE_REFERENCE, refStorage.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final String ref = savedInstanceState.getString(STORAGE_REFERENCE);
        if (ref == null){
            return;
        }

        refStorage = FirebaseStorage.getInstance().getReferenceFromUrl(ref);
        List tasks = refStorage.getActiveDownloadTasks();
        if (tasks.size() > 0){
            final StreamDownloadTask task = (StreamDownloadTask) tasks.get(0);
            task.addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufferSize = 1024 * 1024 * 3;
                    byte[] buffer = new byte[bufferSize];

                    int length;
                    try {
                        while ((length = taskSnapshot.getStream().read(buffer)) != -1){
                            baos.write(buffer, 0, length);
                        }
                        setImageProfile(BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length));
                    } catch (IOException e) {
                        Log.e(TAG, getBaseContext().getResources().getString(R.string.msg_exception_download_image_profile
                                , MaiMudiApplication.getInstance().getLoggedUser().getNickname(), e.getMessage()));
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    public void setmViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }
}

