package cz.muni.fi.pa165.service;

import cz.muni.fi.pa165.dao.CarDAO;
import cz.muni.fi.pa165.entity.Car;
import cz.muni.fi.pa165.service.enums.CarOperationErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CarServiceImpl implements CarService{

    private final Logger log = LoggerFactory.getLogger(CarServiceImpl.class);

    @Inject
    private CarDAO carDAO;

    @Inject
    private TimeService timeService;

    @Override
    public Set<CarOperationErrorCode> createCar(Car car) {
        log.info("Car will be created: " + car);
        if (car == null) {
            log.error("car argument is null");
            throw new IllegalArgumentException("car argument is null");
        }
        Set<CarOperationErrorCode> errors = new HashSet<>();
        car.setActivationDate(timeService.getCurrentTime());
        car.setCreationDate(timeService.getCurrentTime());
        car.setModificationDate(timeService.getCurrentTime());
        try{
            carDAO.save(car);
        }catch (DataAccessException ex) {
            errors.add(CarOperationErrorCode.DATABASE_ERROR);
            log.error(ex.toString());
        }
        return errors;
    }

    @Override
    public Set<CarOperationErrorCode> updateCar(Car car) {
        log.info("Car will be created: " + car);
        if (car == null) {
            log.error("car argument is null");
            throw new IllegalArgumentException("car argument is null");
        }
        Car existingCar = carDAO.findOne(car.getId());
        if (existingCar == null) {
            log.error("car doesn't exist");
            throw new IllegalArgumentException("car argument doesn't exist");
        }
        Set<CarOperationErrorCode> errors = new HashSet<>();
        try{
            //change is permitted only for this properties:
            existingCar.setName(car.getName());
            if(car.getActivationDate()!=null){
                existingCar.setActivationDate(car.getActivationDate());
            }
            existingCar.setModificationDate(timeService.getCurrentTime());
            existingCar.setDeactivated(car.isDeactivated());
            carDAO.save(existingCar);
        }catch (DataAccessException ex) {
            errors.add(CarOperationErrorCode.DATABASE_ERROR);
            log.error(ex.toString());
        }
        return errors;
    }

    @Override
    public Car deleteCar(long id) {
        log.info("Car with id " + id + "  will be deleted");
        Car car = carDAO.findOne(id);
        if (car != null) {
            carDAO.delete(car);
        }else{
            log.debug("Car is not exist!");
        }
        return car;
    }

    @Override
    public List<Car> findAllActivatedCars() {
        return carDAO.findByDeactivatedFalse();
    }

    @Override
    public List<Car> findAllDeactivatedCars() {
        return carDAO.findByDeactivatedTrue();
    }

    @Override
    public Car findCar(long id) {
        return carDAO.findOne(id);
    }
}
