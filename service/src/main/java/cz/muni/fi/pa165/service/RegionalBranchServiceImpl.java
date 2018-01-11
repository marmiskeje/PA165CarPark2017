package cz.muni.fi.pa165.service;

import cz.muni.fi.pa165.dao.CarDAO;
import cz.muni.fi.pa165.dao.RegionalBranchDAO;
import cz.muni.fi.pa165.dao.UserDAO;
import cz.muni.fi.pa165.entity.Car;
import cz.muni.fi.pa165.entity.RegionalBranch;
import cz.muni.fi.pa165.entity.User;
import cz.muni.fi.pa165.service.enums.RegionalBranchOperationErrorCode;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Martin Miskeje, Matej Kralik
 */
@Service
public class RegionalBranchServiceImpl implements RegionalBranchService {

    private final Logger log = LoggerFactory.getLogger(RegionalBranchServiceImpl.class);

    @Inject
    private RegionalBranchDAO regionalBranchDao;
    
    @Inject
    private CarDAO carDao;
    
    @Inject
    private UserDAO userDao;
    
    @Inject
    private TimeService timeService;
    
    @Override
    public Set<RegionalBranchOperationErrorCode> create(RegionalBranch regionalBranch) {
        log.info("Regional branch will be created: " + regionalBranch);
        if (regionalBranch == null) {
            log.error("regionalBranch argument is null");
            throw new IllegalArgumentException("regionalBranch argument is null");
        }
        Set<RegionalBranchOperationErrorCode> errors = new HashSet<>();
        regionalBranch.setCreationDate(timeService.getCurrentTime());
        regionalBranch.setModificationDate(timeService.getCurrentTime());
        try{
            regionalBranchDao.save(regionalBranch);
        }catch (DataAccessException ex) {
            errors.add(RegionalBranchOperationErrorCode.DATABASE_ERROR);
            log.error(ex.toString());
        }
        return errors;
    }

    @Override
    public Set<RegionalBranchOperationErrorCode> update(RegionalBranch regionalBranch) {
        log.info("Regional branch will be updated: " + regionalBranch);
        if (regionalBranch == null) {
            log.error("regionalBranch argument is null");
            throw new IllegalArgumentException("regionalBranch argument is null");
        }
    	RegionalBranch existingBranch = regionalBranchDao.findOne(regionalBranch.getId());
        if (existingBranch == null) {
            log.error("regionalBranch doesn't exist");
            throw new IllegalArgumentException("branch doesn't exist");
        }
        Set<RegionalBranchOperationErrorCode> errors = new HashSet<>();
        try{
            existingBranch.setName(regionalBranch.getName());
            existingBranch.setParent(regionalBranch.getParent());

            existingBranch.removeAllCars();
            regionalBranch.getCars().forEach(item ->{
                Car foundCar = carDao.findOne(item.getId());
                if(foundCar.getRegionalBranch().getId()==null || foundCar.getRegionalBranch().getId() == existingBranch.getId()){
                    existingBranch.addCar(foundCar);
                }else{
                    RegionalBranch old = foundCar.getRegionalBranch();
                    old.removeCar(foundCar);
                    regionalBranchDao.save(old);
                    existingBranch.addCar(foundCar);
                }
            });

            existingBranch.removeAllEmployees();
            regionalBranch.getEmployees().forEach(item -> {
                User foundEmployee = userDao.findOne(item.getId());
                if(foundEmployee.getRegionalBranch().getId()==null || foundEmployee.getRegionalBranch().getId() == existingBranch.getId()){
                    existingBranch.addEmployee(foundEmployee);
                }else{
                    RegionalBranch old = foundEmployee.getRegionalBranch();
                    old.removeEmployee(foundEmployee);
                    regionalBranchDao.save(old);
                    existingBranch.addEmployee(foundEmployee);
                }
            })
            ;

            List<RegionalBranch> oldChildren = existingBranch.getChildren();
            regionalBranch.getChildren().stream()
                    .filter(item -> !oldChildren.contains(item))
                    .forEach(item -> existingBranch.addChild(item))
            ;

            existingBranch.setModificationDate(timeService.getCurrentTime());
            regionalBranchDao.save(existingBranch);
        }catch (DataAccessException ex) {
            errors.add(RegionalBranchOperationErrorCode.DATABASE_ERROR);
            log.error(ex.toString());
        }
        return errors;
    }

    @Override
    public RegionalBranch delete(long id) {
        log.info("Regional branch with id " + id + "  will be deleted");
        regionalBranchDao.delete(id);
        RegionalBranch branch = regionalBranchDao.findOne(id);
        if (branch != null) {
        	List<RegionalBranch> children = branch.getChildren();
        	if(children!=null){
        		RegionalBranch newParent = branch.getParent();
        		for(RegionalBranch child : children){
        			child.setParent(newParent);
        			regionalBranchDao.save(child);
        		}
        	}
        	regionalBranchDao.delete(branch);
        }else{
            log.debug("Regional branch is not exist!");
        }
        return branch;
    }

    @Override
    public List<RegionalBranch> findAll() {
    	return regionalBranchDao.findAll();
    }

    @Override
    public RegionalBranch findOne(long id) {
    	return regionalBranchDao.findOne(id);
    }

	@Override
	public Set<RegionalBranchOperationErrorCode> assignUser(long regionalBranchId, User user) {
        log.info("User " + user + " will be assign to the branch with id " + regionalBranchId);
        if (user == null) {
            log.error("user argument is null");
            throw new IllegalArgumentException("user argument is null");
        }
    	RegionalBranch existingBranch = regionalBranchDao.findOne(regionalBranchId);
        if (existingBranch == null) {
            log.error("regionalBranch doesn't exist");
            throw new IllegalArgumentException("branch doesn't exist");
        }
        Set<RegionalBranchOperationErrorCode> errors = new HashSet<>();
		User foundUser = userDao.findOne(user.getId());
		if(foundUser == null){
	        errors.add(RegionalBranchOperationErrorCode.USER_DOESNT_EXIST);
            log.debug("user doesn't exist! User: " + user);
		}else{
			if(foundUser.getRegionalBranch()!=null){ //remove from old branch
                log.debug("User must be reassigned");
                RegionalBranch old = foundUser.getRegionalBranch();
				old.removeEmployee(foundUser);
                errors.addAll(this.update(old));
			}
			existingBranch.addEmployee(foundUser);
			errors.addAll(this.update(existingBranch));
		}
		return errors;
	}

	@Override
	public Set<RegionalBranchOperationErrorCode> assignCar(long regionalBranchId, Car car) {
        log.info("Car " + car + " will be assign to the branch with id " + regionalBranchId);
        if (car == null) {
            log.error("car argument is null");
            throw new IllegalArgumentException("car argument is null");
        }
    	RegionalBranch existingBranch = regionalBranchDao.findOne(regionalBranchId);
        if (existingBranch == null) {
            log.error("regionalBranch doesn't exist");
            throw new IllegalArgumentException("branch doesn't exist");
        }
        Set<RegionalBranchOperationErrorCode> errors = new HashSet<>();
		Car foundCar = carDao.findOne(car.getId());
		if(foundCar == null){
                    errors.add(RegionalBranchOperationErrorCode.CAR_DOESNT_EXIST);
            log.debug("car doesn't exist! Car: " + car);
        }else{
			if(foundCar.getRegionalBranch()!=null){ //remove from old branch
                log.debug("Car must be reassigned");
                RegionalBranch old = foundCar.getRegionalBranch();
				old.removeCar(foundCar);
                errors.addAll(this.update(old));
			}
			existingBranch.addCar(foundCar);
			errors.addAll(this.update(existingBranch));
		}
		return errors;
	}

	@Override
	public List<Car> findAllAvailableCarsForBranch(RegionalBranch regionalBranch, LocalDateTime day) {
		return regionalBranchDao.findAllAvailableCarsForBranch(regionalBranch.getName(), day);
	}
}
