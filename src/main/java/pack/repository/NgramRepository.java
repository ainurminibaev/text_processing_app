package pack.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pack.model.Ngram;

import java.util.List;

public interface NgramRepository extends CrudRepository<Ngram, Long> {

    List<Ngram> findByNgramSize(int ngramSize);

    @Query(nativeQuery = true, value = "select * from ngram where isstart and ngramSize = ?1 order by RANDOM() limit 1")
    Ngram randomFirstNgramByNgramSize(int ngramSize);
}