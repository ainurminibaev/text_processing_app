package pack.repository;

import org.springframework.data.repository.CrudRepository;
import pack.model.Token;

public interface TokenRepository extends CrudRepository<Token, Long> {

}