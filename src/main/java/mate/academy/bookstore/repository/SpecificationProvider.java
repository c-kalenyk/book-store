package mate.academy.bookstore.repository;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();

    default Specification<T> getSpecification(String param) {
        throw new UnsupportedOperationException("Can't get specification with such "
                + "input parameter. Method is not implemented");
    }

    default Specification<T> getSpecification(String[] params) {
        throw new UnsupportedOperationException("Can't get specification with such "
                + "input parameter. Method is not implemented");
    }

    default Specification<T> getSpecification(BigDecimal param) {
        throw new UnsupportedOperationException("Can't get specification with such "
                + "input parameter. Method is not implemented");
    }
}
