package t4hash;

/*
 * Denna klass ska förberedas för att kunna användas som nyckel i en hashtabell.
 * Du får göra nödvändiga ändringar även i klasserna MyString och ISBN10.
 *
 * Hashkoden ska räknas ut på ett effektivt sätt och följa de regler och
 * rekommendationer som finns för hur en hashkod ska konstrueras. Notera i en
 * kommentar i koden hur du har tänkt när du konstruerat din hashkod.
 */
public class Book {
    private MyString title;
    private MyString author;
    private ISBN10 isbn;
    private MyString content;
    private int price;

    public Book(String title, String author, String isbn, String content, int price) {
        this.title = new MyString(title);
        this.author = new MyString(author);
        this.isbn = new ISBN10(isbn);
        this.content = new MyString(content);
    }

    public MyString getTitle() {
        return title;
    }

    public MyString getAuthor() {
        return author;
    }

    public ISBN10 getIsbn() {
        return isbn;
    }

    public MyString getContent() {
        return content;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    /* The equals method of the class book uses both the title of the book as well as the ISBN.
     * ISBNs tries to be unique for a specific book but sometimes there are collisions, however
     * the chance of a book with both colliding ISBN as well as book title is so small and that's
     * why I went with this approach.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Book))
            return false;
        Book other = (Book) o;
        return isbn.equals(other.isbn) && title.equals(other.title);
    }

    /* This hashing method simply takes the hashvalues of the identifiers of the book
     * that makes it unique and combines them in a way that aims for low collision rate
     * and hopefully good distribution.
     */
    public int hashCode() {
        int hashVal = 0;
        hashVal += isbn.hashCode() * title.hashCode();

        return hashVal;
    }

    @Override
    public String toString() {
        return String.format("\"%s\" by %s Price: %d ISBN: %s lenght: %s", title, author, price, isbn, content.length());
    }

}
