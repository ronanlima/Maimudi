package br.com.maimudi.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import br.com.maimudi.R;
import br.com.maimudi.model.Mood;

/**
 * Created by brunolemgruber on 16/03/16.
 */
public class MoodsService {

    public static List<Mood> getMoods(Context context) {

        List<Mood> moods = new ArrayList<>();
        int[] mImg = new int[] {R.drawable.angry,R.drawable.cool,R.drawable.crying,R.drawable.evil,R.drawable.happy,R.drawable.love, +
                R.drawable.sad};

        String[] mNome = new String[] {"com raiva","thug life","chorando","pensando besteira", "feliz","apaixonado","triste"};

        for (int i = 0; i <= 6; i++) {
            Mood m = new Mood();
            m.setImg(mImg[i]);
            m.setNome(mNome[i]);
            moods.add(m);
        }

        return moods;

    }
}
