package edu.sjsu.cmpe275.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import edu.sjsu.cmpe275.model.Train;

@Transactional(Transactional.TxType.REQUIRED)
public interface TrainRepository extends CrudRepository<Train, String>{

}
