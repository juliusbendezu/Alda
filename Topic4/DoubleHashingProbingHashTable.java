package t4hash;

public class DoubleHashingProbingHashTable<T> extends ProbingHashTable<T> {

    /*
     * Denna metod ska skrivas klart. Den ska använda bokens förslag på andra
     * hashalgoritm.
     *
     * TODO: algoritmen måste finnas med här för dem som inte har boken.
     */
    @Override
    protected int findPos(T x) {
        int r = smallerPrimeThanCapacity();
        int currentPos = myhash(x);
        int interval = r - (currentPos % r);

        while (continueProbing(currentPos, x)) {
		currentPos += interval;
		if(currentPos >= capacity())
		    currentPos -= capacity();
        }

        return currentPos;
    }

    /*
     * Denna metod ger ett primtal mindre än tabellens storlek. Detta primtal ska
     * användas i metoden ovan.
     */
    protected int smallerPrimeThanCapacity() {
        int n = capacity() - 2;
        while (!isPrime(n)) {
            n -= 2;
        }
        return n;
    }

}
