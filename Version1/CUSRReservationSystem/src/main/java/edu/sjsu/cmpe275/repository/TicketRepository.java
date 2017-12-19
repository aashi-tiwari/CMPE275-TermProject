package edu.sjsu.cmpe275.repository;

import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import edu.sjsu.cmpe275.model.Ticket;

@Transactional(Transactional.TxType.REQUIRED)
public interface  TicketRepository extends CrudRepository<Ticket, String>{
//	@Query(value="SELECT * FROM train",nativeQuery=true)
//	void findTrains(Character );
}

