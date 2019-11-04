package t3trees;

/**
 * Detta är den enda av de tre klasserna ni ska göra några ändringar i. (Om ni
 * inte vill lägga till fler testfall.) Det är också den enda av klasserna ni
 * ska lämna in. Glöm inte att namn och användarnamn ska stå i en kommentar
 * högst upp, och att paketdeklarationen måste plockas bort vid inlämningen för
 * att koden ska gå igenom de automatiska testerna.
 * <p>
 * De ändringar som är tillåtna är begränsade av följande:
 * <ul>
 * <li>Ni får INTE byta namn på klassen.
 * <li>Ni får INTE lägga till några fler instansvariabler.
 * <li>Ni får INTE lägga till några statiska variabler.
 * <li>Ni får INTE använda några loopar någonstans. Detta gäller också alterntiv
 * till loopar, så som strömmar.
 * <li>Ni FÅR lägga till fler metoder, dessa ska då vara privata.
 * <li>Ni får INTE låta NÅGON metod ta en parameter av typen
 * BinarySearchTreeNode. Enbart den generiska typen (T eller vad ni väljer att
 * kalla den), String, StringBuilder, StringBuffer, samt primitiva typer är
 * tillåtna.
 * </ul>
 *
 * @param <T>
 * @author henrikbe
 */

public class BinarySearchTreeNode<T extends Comparable<T>> {

    private T data;
    private BinarySearchTreeNode<T> left;
    private BinarySearchTreeNode<T> right;

    public BinarySearchTreeNode(T data) {
        this.data = data;
    }

    public boolean add(T data) {
        if (data == null)
            return false;

        int comparedResult = data.compareTo(this.data);

        if (comparedResult < 0) {
            if (left == null) {
                left = new BinarySearchTreeNode<>(data);
                return true;
            } else
                return left.add(data);
        } else if (comparedResult > 0) {
            if (right == null) {
                right = new BinarySearchTreeNode<>(data);
                return true;
            } else
                return right.add(data);
        } else
            return false;
    }

    private T findMin() {
        if (left == null)
            return data;
        else
            return left.findMin();
    }

    public BinarySearchTreeNode<T> remove(T data) {
        if (data == null)
            return null;


        if (data.compareTo(this.data) < 0) {
            if(left == null)
                return this;

            if (left.data == data) {
                if (left.left != null && left.right != null) {
                    T toReplace = left.right.findMin();
                    left.data = toReplace;
                    left.right = left.right.remove(toReplace);
                } else if (left.left != null)
                    left = left.left;
                else if (left.right != null)
                    left = left.right;
                else
                    left = null;

            } else
                left.remove(data);

        } else if (data.compareTo(this.data) > 0) {
            if(right == null)
                return this;

            if (right.data == data) {
                if (right.left != null && right.right != null) {
                    T toReplace = right.right.findMin();
                    right.data = toReplace;
                    right.right = right.right.remove(toReplace);
                } else if (right.left != null)
                    right = right.left;
                else if (right.right != null)
                    right = right.right;
                else
                    right = null;

            } else
                right.remove(data);

        } else { //self
            if (right != null) {
                T toReplace = right.findMin();
                this.data = toReplace;
                if(right.left == null && right.right == null)
                    right = null;
                else
                    right.remove(toReplace);

            } else if (left != null) {
                return left;

            } else
                return null;
        }

        return this;
    }

    public boolean contains(T data) {
        if (data == null)
            return false;

        int comparedResult = data.compareTo(this.data);

        if (comparedResult < 0)
            return (left != null) && left.contains(data);
        else if (comparedResult > 0)
            return (right != null) && right.contains(data);
        else
            return true;
    }

    public int size() {
        int count = 0;

        if (left != null)
            count += left.size();

        if (right != null)
            count += right.size();

        return ++count;
    }

    public int depth() {
        if (left == null && right == null)
            return 0;

        int leftDepth = 1 + ((left == null) ? 0 : left.depth());
        int rightDepth = 1 + ((right == null) ? 0 : right.depth());

        return Math.max(leftDepth, rightDepth);
    }

    public String toString() {
        String str = "";
        if (left != null)
            str += left.toString() + ", ";

        str += data.toString();

        if (right != null)
            str += ", " + right.toString();

        return str;
    }
}
