package com.gfa.urlaliaser.repositories;

import com.gfa.urlaliaser.models.LinkEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkEntryRepository extends CrudRepository<LinkEntry, Long> {
    LinkEntry findByAlias(String alias);
}
