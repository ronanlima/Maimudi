package br.com.maimudi.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.maimudi.R;
import br.com.maimudi.model.Comment;
import br.com.maimudi.model.Timeline;

/**
 * Created by Admin on 23/09/2016.
 */
public class UtilFirebaseServices {

    @NonNull
    public static Timeline recebePostFirebase(Context context, DataSnapshot data) {
        Timeline t = new Timeline();
        t.setId(data.getKey());
        t.setUser(data.child(context.getResources().getString(R.string.timeline_nickname)).getValue().toString());
        if (data.child(context.getResources().getString(R.string.timeline_action)).getValue() != null){
            t.setAction(data.child(context.getResources().getString(R.string.timeline_action)).getValue().toString());
        }
        if (data.child(context.getResources().getString(R.string.timeline_feeling)).getValue() != null){
            t.setFeeling(data.child(context.getResources().getString(R.string.timeline_feeling)).getValue().toString());
        }
        t.setLyric(data.child(context.getResources().getString(R.string.timeline_lyric)).getValue().toString());
        Long time = Long.parseLong(data.child(context.getResources().getString(R.string.timeline_timestamp)).getValue().toString());
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
        String horaFormatada = sdf.format(date);
        t.setTimestamp(time);
        t.setTimestamp_formatado(horaFormatada);
        t.setSing(R.drawable.microphone_black_icon);
        t.setImgComment(R.drawable.comment);
        if (data.child(context.getResources().getString(R.string.timeline_post_img)).getValue() != null){
            t.setPost_img(data.child(context.getResources().getString(R.string.timeline_post_img)).getValue().toString());
        }
        t.setPreview(data.child(context.getResources().getString(R.string.timeline_preview)).getValue().toString());
        if (data.child(context.getResources().getString(R.string.timeline_user_img)).getValue() != null){
            t.setUser_img(data.child(context.getResources().getString(R.string.timeline_user_img)).getValue().toString());
        }
        t.setComments(new ArrayList<Comment>());
        return t;
    }

    public static void recebeCommentOfPost(Timeline t, Context context, DataSnapshot commentEspecific) {
        Comment c = new Comment();
        c.setIdComment(commentEspecific.getKey());
        c.setComment(commentEspecific.child(context.getResources().getString(R.string.comment)).getValue().toString());
        c.setNickname(commentEspecific.child(context.getResources().getString(R.string.comment_nickname)).getValue().toString());
        if (commentEspecific.child(context.getResources().getString(R.string.comment_user_img)).getValue() != null){
            c.setUser_img(commentEspecific.child(context.getResources().getString(R.string.comment_user_img)).getValue().toString());
        }
        c.setUser(commentEspecific.child(context.getResources().getString(R.string.comment_user)).getValue().toString());
        c.setTimestamp((Long) commentEspecific.child(context.getResources().getString(R.string.comment_timestamp)).getValue());
        t.getComments().add(c);
    }
}
