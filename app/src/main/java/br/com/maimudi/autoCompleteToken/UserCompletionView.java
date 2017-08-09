package br.com.maimudi.autoCompleteToken;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import br.com.maimudi.R;
import br.com.maimudi.model.User;

/**
 * Created by brunolemgruber on 23/03/16.
 */
public class UserCompletionView extends TokenCompleteTextView<User> {

    public UserCompletionView(Context context) {
        super(context);
    }

    public UserCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(User user) {

        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.user_token, (ViewGroup)UserCompletionView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.nome)).setText(user.getNome());

        return view;
    }

    @Override
    protected User defaultObject(String completionText) {
        //return new User(R.drawable.roster1,completionText,completionText);
        return null;
    }
}
