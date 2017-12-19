package edu.sjsu.cmpe275.repository;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import edu.sjsu.cmpe275.model.TrainCapacity;

@Transactional(Transactional.TxType.REQUIRED)
public interface TrainCapacityRepository extends CrudRepository<TrainCapacity, String>{
	String q="";
	//@Query(value="SELECT * FROM TrainCapacity INNER JOIN Train ON  TrainCapacity.trainNumber=Train.trainNumber WHERE Train.direction=\"NB\" AND Train.status=\"Scheduled\"",nativeQuery=true)
	@Query(value="SELECT * FROM train_capacity INNER JOIN Train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train.status=\"Scheduled\"",nativeQuery=true)
	List<TrainCapacity> searchTrains(String direction,String date);
	
	@Query(value="SELECT * FROM train_capacity INNER JOIN Train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train.status=\"Scheduled\"",nativeQuery=true)
	List<TrainCapacity> searchTrainsByExactTime(String direction,String date,int exactTime,int currentTime);
}
