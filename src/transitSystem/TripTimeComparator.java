package transitSystem;

import java.util.Comparator;
/** A comparator to compare each ContinuousTrip's start time. */
public class TripTimeComparator implements Comparator<ContinuousTrip> {

    /** To compare two ContinuousTrip */
    @Override
    public int compare(ContinuousTrip o1, ContinuousTrip o2) {
        return o1.getFront().getEnterTime().compareTo(o2.getFront().getEnterTime()) ;
    }
}
