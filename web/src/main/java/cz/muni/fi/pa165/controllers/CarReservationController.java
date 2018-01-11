package cz.muni.fi.pa165.controllers;

import cz.muni.fi.pa165.config.ApiDefinition;
import cz.muni.fi.pa165.dto.CarReservationRequestDTO;
import cz.muni.fi.pa165.dto.results.CarReservationRequestOperationResult;
import cz.muni.fi.pa165.enums.CarReservationRequestState;
import cz.muni.fi.pa165.exceptions.ResourceNotFound;
import cz.muni.fi.pa165.exceptions.ResourceNotValid;
import cz.muni.fi.pa165.facade.CarReservationRequestFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiDefinition.Reservation.BASE)
public class CarReservationController {

    private final static Logger LOG = LoggerFactory.getLogger(CarReservationController.class);

    @Inject
    private CarReservationRequestFacade reservationRequestFacade;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<CarReservationRequestDTO> getAllReservations(@RequestParam(value = "ignoreDenied",required = false, defaultValue = "true")
                                                                               boolean ignoreDenied){
        List<CarReservationRequestDTO> result = reservationRequestFacade.findAll();
        if(result == null) {
            result = Collections.emptyList();
        }
        if(ignoreDenied){
            result = result.stream().filter(item -> !CarReservationRequestState.DENIED.toString().equals(item.getState().toString())).collect(Collectors.toList());
        }
        return result;
    }

    @RequestMapping(value = ApiDefinition.Reservation.ID , method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final CarReservationRequestDTO getReservationById(@PathVariable(ApiDefinition.Reservation.PATH_ID) long id) {
        CarReservationRequestDTO result = reservationRequestFacade.findOne(id);
        if (result != null) {
            return result;
        } else {
            throw new ResourceNotFound();
        }
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public final CarReservationRequestOperationResult createReservation(@Valid
                                                                        @RequestBody CarReservationRequestDTO reservation,
                                                                        BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new ResourceNotValid();
        }
        LOG.debug("REST create reservation: ", reservation);

        return reservationRequestFacade.createCarReservationRequest(reservation);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public final CarReservationRequestOperationResult updateReservation(@Valid @RequestBody CarReservationRequestDTO reservation,
                                                                        BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new ResourceNotValid();
        }
        LOG.debug("REST update reservation: ", reservation);

        return reservationRequestFacade.updateCarReservationRequest(reservation);
    }

    @RequestMapping(value = ApiDefinition.Reservation.ID, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final void deleteReservation(@PathVariable(ApiDefinition.Reservation.PATH_ID) long id){
        LOG.debug("REST delete reservation with id: ", id);
        try {
            reservationRequestFacade.deleteCarReservationRequest(id);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            throw new ResourceNotFound();
        }
    }

    /**
     * @param children true for all children ../reservation/1?children=true , ?ignoreDenied=true select all reservations except denied
     */
    @RequestMapping(value = ApiDefinition.Reservation.ID, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<CarReservationRequestDTO> getReservation(@PathVariable(ApiDefinition.Reservation.PATH_ID) long id,
                                                                          @RequestParam(value = "children",required = false, defaultValue = "false")
                                                                                  boolean children,
                                                               @RequestParam(value = "ignoreDenied",required = false, defaultValue = "true")
                                                                       boolean ignoreDenied,
                                                                          @RequestBody Map<String, LocalDateTime> period){
        LocalDateTime start = period.get("start");
        LocalDateTime end = period.get("end");
        List<CarReservationRequestDTO> result = null;
        if(children){
            LOG.debug("get all reservations for branch ", id);
            result= reservationRequestFacade.getAllForRegionalBranchAndChildren(id,start,end);
        }else{
            LOG.debug("get all reservations for branch ", id);
            result = reservationRequestFacade.getAllForRegionalBranch(id,start,end);
        }
        if(result == null) {
            result = Collections.emptyList();
        }
        if(ignoreDenied){
            result = result.stream().filter(item -> !CarReservationRequestState.DENIED.toString().equals(item.getState().toString())).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * Get all reservation for particular user, ?ignoreDenied=true select all reservations except denied
     */
    @RequestMapping(value = ApiDefinition.Reservation.BY_USER_ID, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<CarReservationRequestDTO> getReservationForUser(@PathVariable(ApiDefinition.Reservation.PATH_ID) long id,
                                                                      @PathVariable(ApiDefinition.Reservation.USER_ID) long userId,
                                                                      @RequestParam(value = "ignoreDenied",required = false, defaultValue = "true")
                                                                                  boolean ignoreDenied,
                                                               @RequestBody Map<String, LocalDateTime> period){
        LOG.debug("get all reservations for user with id '" + userId + "' in the branch " + id);
        LocalDateTime start = period.get("start");
        LocalDateTime end = period.get("end");
        List<CarReservationRequestDTO> result = null;

        LOG.debug("get all reservations for branch ", id);
        result= reservationRequestFacade.getAllForRegionalBranch(id,start,end);
        if(result == null) {
            result = Collections.emptyList();
        }
        if(ignoreDenied){
            result = result.stream().filter(item -> !CarReservationRequestState.DENIED.toString().equals(item.getState().toString())).collect(Collectors.toList());
        }
        return result.stream().filter(x -> x.getUser().getId() == userId).collect(Collectors.toList());
    }
}
