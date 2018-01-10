package cz.muni.fi.pa165.service;

import cz.muni.fi.pa165.dao.CarDAO;
import cz.muni.fi.pa165.dao.CarReservationRequestDAO;
import cz.muni.fi.pa165.dao.UserDAO;
import cz.muni.fi.pa165.entity.Car;
import cz.muni.fi.pa165.entity.CarReservationRequest;
import cz.muni.fi.pa165.entity.User;
import cz.muni.fi.pa165.enums.CarReservationRequestState;
import cz.muni.fi.pa165.service.enums.CarReservationRequestOperationErrorCode;
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
 * @author Martin Miskeje
 */
@Service
public class CarReservationRequestServiceImpl implements CarReservationRequestService {

    private final Logger log = LoggerFactory.getLogger(CarReservationRequestServiceImpl.class);


    @Inject
    private CarReservationRequestDAO requestsDao;
    @Inject
    private TimeService timeService;

    @Override
    public Set<CarReservationRequestOperationErrorCode> create(CarReservationRequest request) {
        log.info("arReservation will be created: " + request);
        if (request == null) {
            log.error("request argument is null");
            throw new IllegalArgumentException("request argument is null");
        }
        Set<CarReservationRequestOperationErrorCode> errors = new HashSet<>();
        errors.addAll(validateInput(request, null));
        if (errors.isEmpty()) {
            try {
                request.setState(CarReservationRequestState.CREATED);
                request.setCreationDate(timeService.getCurrentTime());
                request.setModificationDate(timeService.getCurrentTime());
                requestsDao.save(request);
            }
            catch (DataAccessException ex) {
                errors.add(CarReservationRequestOperationErrorCode.DATABASE_ERROR);
                log.error(ex.toString());
            }
        }
        return errors;
    }
    
    @Override
    public Set<CarReservationRequestOperationErrorCode> update(CarReservationRequest request) {
        log.info("CarReservation will be updated: " + request);
        if (request == null) {
            log.error("request argument is null");
            throw new IllegalArgumentException("request argument is null");
        }
        // get from db for change safety
        CarReservationRequest existing = requestsDao.findOne(request.getId());
        if (existing == null) {
            log.error("request not exists");
            throw new IllegalArgumentException("request argument not exists");
        }
        Set<CarReservationRequestOperationErrorCode> errors = new HashSet<>();
        errors.addAll(validateInput(request, existing));
        if (errors.isEmpty()) {
            try {
                //change is permitted only for this properties:
                existing.setUser(request.getUser());
                existing.setCar(request.getCar());
                existing.setReservationStartDate(request.getReservationStartDate());
                existing.setReservationEndDate(request.getReservationEndDate());
                existing.setState(request.getState());
                existing.setModificationDate(timeService.getCurrentTime());
                requestsDao.save(existing);
            }
            catch (DataAccessException ex) {
                errors.add(CarReservationRequestOperationErrorCode.DATABASE_ERROR);
                log.error(ex.toString());
            }
        }
        return errors;
    }
    
    @Override
    public CarReservationRequest delete(long reservationRequestId) {
        log.info("CarReservationRequest with id " + reservationRequestId + "  will be deleted");
        CarReservationRequest user = requestsDao.findOne(reservationRequestId);
        if (user != null) {
            requestsDao.delete(user);
        }else{
            log.debug("Car Reservation is not exist!");
        }
        return user;
    }
    
    @Override
    public List<CarReservationRequest> getAllForRegionalBranch(long regionalBranchId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return requestsDao.getAllForRegionalBranch(regionalBranchId, dateFrom, dateTo);
    }

    @Override
    public List<CarReservationRequest> getAllForRegionalBranchAndChildren(long regionalBranchId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        return requestsDao.getAllForRegionalBranchAndChildren(regionalBranchId, dateFrom, dateTo);
    }

    @Override
    public List<CarReservationRequest> findAll() {
        return requestsDao.findAll();
    }

    @Override
    public CarReservationRequest findOne(long id) {
        return requestsDao.findOne(id);
    }

    private Set<CarReservationRequestOperationErrorCode> validateInput(CarReservationRequest newRequest, CarReservationRequest existingRequest){
        Set<CarReservationRequestOperationErrorCode> errors = new HashSet<>();
        if (newRequest.getCar() == null) {
            errors.add(CarReservationRequestOperationErrorCode.CAR_REQUIRED);
        }
        else if (newRequest.getUser() == null) {
            errors.add(CarReservationRequestOperationErrorCode.USER_REQUIRED);
        }
        else if (newRequest.getReservationStartDate() == null) {
            errors.add(CarReservationRequestOperationErrorCode.START_DATE_REQUIRED);
        }
        else if (newRequest.getReservationEndDate() == null) {
            errors.add(CarReservationRequestOperationErrorCode.END_DATE_REQUIRED);
        }
        else if (newRequest.getReservationEndDate().isBefore(newRequest.getReservationStartDate())){
            errors.add(CarReservationRequestOperationErrorCode.END_DATE_BEFORE_START_DATE);
        }
        else if (newRequest.getReservationStartDate().isBefore(timeService.getCurrentTime())) {
            errors.add(CarReservationRequestOperationErrorCode.RESERVATION_IN_THE_PAST);
        }
        if (errors.isEmpty()) {
            List<CarReservationRequest> overlappedReservations = requestsDao.findAllOverlappedReservations(
                    newRequest.getReservationStartDate(),
                    newRequest.getReservationEndDate(),
                    newRequest.getCar().getId());
            // remove denied reservations, they are harmless
            overlappedReservations.removeIf(x -> x.getState() == CarReservationRequestState.DENIED);
            if (existingRequest != null){
                // remove existing reservation - it will be updated
                overlappedReservations.removeIf(x -> x.getId().equals(existingRequest.getId()));
            }
            if (!overlappedReservations.isEmpty()) {
                errors.add(CarReservationRequestOperationErrorCode.CAR_NOT_AVAILABLE);
            }
        }
        return errors;
    }

}
