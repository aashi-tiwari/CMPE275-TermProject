package edu.sjsu.cmpe275.repository;

import java.sql.Date;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import edu.sjsu.cmpe275.model.TrainCapacity;

@Transactional(Transactional.TxType.REQUIRED)
public interface TrainCapacityRepository extends CrudRepository<TrainCapacity, String>,JpaSpecificationExecutor<TrainCapacity>{
	String q="";
	//@Query(value="SELECT * FROM TrainCapacity INNER JOIN Train ON  TrainCapacity.trainNumber=Train.trainNumber WHERE Train.direction=\"NB\" AND Train.status=\"Scheduled\"",nativeQuery=true)
	@Query(value="SELECT * FROM train_capacity INNER JOIN Train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train.status=\"Scheduled\"",nativeQuery=true)
	List<TrainCapacity> searchTrains(String direction,String date);
	
	
	//Search train with exact time
	@Query(value="SELECT * FROM train_capacity INNER JOIN Train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train.status=\"Scheduled\" AND train.start_time=?3",nativeQuery=true)
	List<TrainCapacity> searchTrainsByExactTime(String direction,String date,int exactTime);
	
	//Search train with train type
	@Query(value="SELECT * FROM train_capacity INNER JOIN Train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train.status=\"Scheduled\" AND train.type=?3 ",nativeQuery=true)
	List<TrainCapacity> searchTrainsWithType(String direction,String date,String trainType);
	
	//Search train with exact time and train type
	@Query(value="SELECT * FROM train_capacity INNER JOIN Train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train.status=\"Scheduled\" AND train.start_time=?3 AND train.type=?4 ",nativeQuery=true)
	List<TrainCapacity> searchTrains_ExactTime_Type(String direction,String date,int exactTime,String trainType);
	
	@Query(value="SELECT * FROM train_capacity WHERE train_number=?1 AND travel_date=?2 ",nativeQuery=true)
	List<TrainCapacity> searchTrainsToUpdateCapacity(String trainNumber,Date date);
	
}
