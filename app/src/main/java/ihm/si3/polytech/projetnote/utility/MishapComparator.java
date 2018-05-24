package ihm.si3.polytech.projetnote.utility;

import java.util.Comparator;

public class MishapComparator implements Comparator<Mishap> {

    @Override
    public int compare(Mishap mishap, Mishap t1) {
        return mishap.getPriority().compareTo(t1.getPriority());
    }
}
