package edu.sjsu.cmpe275.repository;

import java.sql.Date;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import edu.sjsu.cmpe275.model.TrainCapacity;

@Transactional(Transactional.TxType.REQUIRED)
public interface TrainCapacityRepository extends CrudRepository<TrainCapacity, String>,JpaSpecificationExecutor<TrainCapacity>{
	//@Query(value="SELECT * FROM TrainCapacity INNER JOIN Train ON  TrainCapacity.trainNumber=Train.trainNumber WHERE Train.direction=\"NB\" AND Train.status=\"Scheduled\"",nativeQuery=true)
	@Query(value="SELECT * FROM train_capacity INNER JOIN train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train_capacity.status=\"Scheduled\"",nativeQuery=true)
	List<TrainCapacity> searchTrains(String direction,String date);
	
	
	//Search train with exact time
	@Query(value="SELECT * FROM train_capacity INNER JOIN train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train_capacity.status=\"Scheduled\" AND train.start_time=?3",nativeQuery=true)
	List<TrainCapacity> searchTrainsByExactTime(String direction,String date,int exactTime);
	
	//Search train with train type
	@Query(value="SELECT * FROM train_capacity INNER JOIN train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train_capacity.status=\"Scheduled\" AND train.type=?3 ",nativeQuery=true)
	List<TrainCapacity> searchTrainsWithType(String direction,String date,String trainType);
	
	//Search train with exact time and train type
	@Query(value="SELECT * FROM train_capacity INNER JOIN train ON train_capacity.train_number=train.train_number WHERE train.direction=?1 AND train_capacity.travel_date=?2 AND train_capacity.status=\"Scheduled\" AND train.start_time=?3 AND train.type=?4 ",nativeQuery=true)
	List<TrainCapacity> searchTrains_ExactTime_Type(String direction,String date,int exactTime,String trainType);
	
	//START: Queries for Train Cancellation
	//Search train capacity for train status change
	@Query(value="SELECT * FROM train_capacity WHERE train_capacity.train_number=?1 AND train_capacity.travel_date=?2",nativeQuery=true)
	List<TrainCapacity> getTrainForStatusChange(String trainNumber,String date);
	//END: Queries for Train Cancellation
	
		@Query(value="SELECT * FROM train_capacity WHERE train_number=?1 AND travel_date=?2 ",nativeQuery=true)
		List<TrainCapacity> searchTrainsToUpdateCapacity(String trainNumber,Date date);
		
		// Aashi - Added new Query for updating capacity after system reset
		@Modifying
		@Query(value="UPDATE train_capacity SET "
				+ "a_capacity = ?1, b_capacity = ?1, c_capacity = ?1, c_capacity = ?1, d_capacity = ?1, "
				+ "e_capacity = ?1, f_capacity = ?1, g_capacity = ?1, h_capacity = ?1, i_capacity = ?1, "
				+ "j_capacity = ?1, k_capacity = ?1, l_capacity = ?1, m_capacity = ?1, n_capacity = ?1, "
				+ "o_capacity = ?1, p_capacity = ?1, q_capacity = ?1, r_capacity = ?1, s_capacity = ?1, "
				+ "t_capacity = ?1, u_capacity = ?1, v_capacity = ?1, w_capacity = ?1, x_capacity = ?1, "
				+ "y_capacity = ?1, z_capacity = ?1" , nativeQuery=true)
		void updateTrainCapacity(int default_capacity);
}
