package mate.academy.bookstore.repository.book;

import mate.academy.bookstore.model.Book;
import mate.academy.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String TITLE = "title";

    @Override
    public String getKey() {
        return TITLE;
    }

    @Override
    public Specification<Book> getSpecification(String titlePart) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(TITLE), "%" + titlePart + "%");
    }
}
