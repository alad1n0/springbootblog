package edu.itstep.final_project_v1.domain.repositories;

import edu.itstep.final_project_v1.domain.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String>
{

}
