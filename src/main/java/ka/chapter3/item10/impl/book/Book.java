package ka.chapter3.item10.impl.book;

import java.util.Objects;

public class Book {
    private String title;
    private int pageCount;

    public Book(String title, int pageCount) {
        this.title = title;
        this.pageCount = pageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book obj = (Book) o;
        return pageCount == obj.pageCount &&
                Objects.equals(title, obj.title);
    }
}
