package api.desafio.codificar.Backend.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class AbstractService {

    protected Sort buildSort(String sort, String direction) {
        return Sort.by(Sort.Direction.fromString(direction), sort);
    }

    protected Pageable buildPageable(Integer page, Integer size, Sort sort) {
        return (page != -1 && size != -1) ? PageRequest.of(page, size, sort) : PageRequest.of(0, Integer.MAX_VALUE, sort);
    }
}
