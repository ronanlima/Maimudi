package br.com.maimudi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import br.com.maimudi.MaiMudiApplication;
import br.com.maimudi.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by brunolemgruber on 14/03/16.
 */
public class BaseActivity extends AppCompatActivity{
    private SimpleArcDialog simpleArcDialog;
    private ArcConfiguration arcConfiguration;
    private Toolbar toolbar;
    private TextView textViewNomeUsuario, tvLogout;
    private ProgressBar loadingImageProfile;
    private CircleImageView circleImageView;

    protected void setupToolBar(){
        if (toolbar == null){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            loadingImageProfile = (ProgressBar) findViewById(R.id.loading_image_profile);
            tvLogout = (TextView) findViewById(R.id.tv_logout);
            setCircleImageView((CircleImageView) findViewById(R.id.profile_image));
            tvLogout.setOnClickListener(onClickLogout());
            if(toolbar != null){
                setSupportActionBar(toolbar);
            }
        }
    }

    public View.OnClickListener onClickLogout(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logouApp();
            }
        };
    }

    public void logouApp() {
        if (FirebaseAuth.getInstance() != null){
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(BaseActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    public void updateImageProfile(boolean isImagemPersonalizada){
        loadingImageProfile.setVisibility(View.GONE);
        getCircleImageView().setVisibility(View.VISIBLE);
        if (isImagemPersonalizada){
            getCircleImageView().setImageDrawable(MaiMudiApplication.getInstance().getLoggedUser().getImageProfile().getDrawable());
        } else {
            getCircleImageView().setImageDrawable(this.getResources().getDrawable(R.drawable.user_mudi));
        }
    }

    public void updateNomeUsuario(View view){
        textViewNomeUsuario = (TextView) view.findViewById(R.id.nome_usuario);
        textViewNomeUsuario.setText(MaiMudiApplication.getInstance().getLoggedUser().getNome());
    }

    public void setupDialog(Context context, String text){
        int[] colors = {Color.parseColor("#00C64C")};

        setArcConfiguration(new ArcConfiguration(context));
        getArcConfiguration().setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
        getArcConfiguration().setColors(colors);
        getArcConfiguration().setText(text);
        setSimpleArcDialog(new SimpleArcDialog(context));
        getSimpleArcDialog().setConfiguration(getArcConfiguration());
    }

    public SimpleArcDialog getSimpleArcDialog() {
        return simpleArcDialog;
    }

    public void setSimpleArcDialog(SimpleArcDialog simpleArcDialog) {
        this.simpleArcDialog = simpleArcDialog;
    }

    public ArcConfiguration getArcConfiguration() {
        return arcConfiguration;
    }

    public void setArcConfiguration(ArcConfiguration arcConfiguration) {
        this.arcConfiguration = arcConfiguration;
    }

    public CircleImageView getCircleImageView() { return circleImageView; }

    public void setCircleImageView(CircleImageView circleImageView) { this.circleImageView = circleImageView; }
}
