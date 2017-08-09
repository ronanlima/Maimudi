package br.com.maimudi.services;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import java.io.Serializable;

import br.com.maimudi.R;

/**
 * Created by Ronan.lima on 04/10/16.
 */
public class UtilConnectivityService {
    private static final String TAG = UtilConnectivityService.class.getCanonicalName().toUpperCase();

    private static IDisableConnection listener;

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static void createNetErrorDialog(final Context context, Bundle bundle, String msg){
        listener = (IDisableConnection) bundle.getSerializable("LISTENER_CANCELLED");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(msg)
                .setTitle(context.getResources().getString(R.string.title_settings))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.positive_button_settings),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                context.startActivity(intent);
                            }
                        })
                .setNegativeButton(context.getResources().getString(R.string.negative_button_settings),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                listener.onCancelledNetwork(context);
                            }
                        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public interface IDisableConnection extends Serializable{
        void onCancelledNetwork(Context context);
    }
}
