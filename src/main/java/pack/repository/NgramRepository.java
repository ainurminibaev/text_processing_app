package pack.repository;

import org.springframework.data.repository.CrudRepository;
import pack.model.Ngram;

import java.util.List;

public interface NgramRepository extends CrudRepository<Ngram, Long> {

    List<Ngram> findByNgramSize(int ngramSize);
}