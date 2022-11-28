package at.kaasbrot.yugicalc.dialog;

import com.kaasbrot.boehlersyugiohapp.R;
import at.kaasbrot.yugicalc.history.Dice;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//retired :')
public class ImgPathsIterator implements Iterator<Integer> {
    private int i = 0;
    private boolean random;
    private final List<Integer> imgPaths;

    public ImgPathsIterator(boolean random, Integer... paths) {
        this.imgPaths = Arrays.asList(paths);
        this.random = random;
        if(random) {
            Collections.shuffle(this.imgPaths);
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        i += 1;
        if(i >= imgPaths.size()) {
            i = 0;
            if(random)
                Collections.shuffle(imgPaths);
        }
        return imgPaths.get(i);
    }

    public Integer current() {
        return imgPaths.get(i);
    }

    public Dice generateDiceValue() {
        int r = current();
        if(r == R.drawable.d1) return new Dice(1);
        else if(r == R.drawable.d2) return new Dice(2);
        else if(r == R.drawable.d3) return new Dice(3);
        else if(r == R.drawable.d4) return new Dice(4);
        else if(r == R.drawable.d5) return new Dice(5);
        else if(r == R.drawable.d6) return new Dice(6);
        else throw new RuntimeException("Bizarre dice value");

    }
}