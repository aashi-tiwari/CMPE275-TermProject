package edu.sjsu.cmpe275.repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import edu.sjsu.cmpe275.model.Ticket;
import edu.sjsu.cmpe275.model.TrainCapacity;

@Transactional(Transactional.TxType.REQUIRED)
public interface  TicketRepository extends CrudRepository<Ticket, String>{
	//START: Snehal
	@Query(value="SELECT * FROM ticket WHERE train_number=?1 AND travel_date=?2",nativeQuery=true)
	List<Ticket> searchTicketsByTrainAndDate(String trainNumber,String date);
	
	//@Query(value="SELECT * FROM ticket WHERE transaction_id in (\"abc\",\"cde\") AND status!=\"Inactive\"",nativeQuery=true)
//	@Query(value="SELECT * FROM ticket WHERE transaction_id in ?1 AND status!=\"Inactive\"",nativeQuery=true)
//	List<Ticket> searchTicketsByTransactionId(List<String> transactionIds);
	//List<Ticket> searchTicketsByTransactionId();
	
	//END: Snehal
}

