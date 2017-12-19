package edu.sjsu.cmpe275.repository;

import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import edu.sjsu.cmpe275.model.User;

@Transactional(Transactional.TxType.REQUIRED)
public interface UserRepository extends CrudRepository<User, Long>{

}
