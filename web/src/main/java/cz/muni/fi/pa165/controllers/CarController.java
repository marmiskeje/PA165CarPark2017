package cz.muni.fi.pa165.controllers;

import cz.muni.fi.pa165.config.ApiDefinition;
import cz.muni.fi.pa165.dto.CarDTO;
import cz.muni.fi.pa165.dto.results.CarOperationResult;
import cz.muni.fi.pa165.exceptions.ResourceNotFound;
import cz.muni.fi.pa165.exceptions.ResourceNotValid;
import cz.muni.fi.pa165.facade.CarFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiDefinition.Car.BASE)
public class CarController {

    private final static Logger LOG = LoggerFactory.getLogger(CarController.class);

    @Inject
    private CarFacade carFacade;

    /**
     * /car select all cars
     * /car?branchId=1 select all cars for branch 1
     * /car?activated=false select all deactivated car
     * /car?activated=false&branchId=1 select all deactivated car in branch 1
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final List<CarDTO> getAllCars(@RequestParam(value = "activated",required = false, defaultValue = "true")
                                                     boolean activated,
                                         @RequestParam(value = "branchId",required = false, defaultValue = "-1")
                                                 long branchId){
        List<CarDTO> result = null;
        if(activated){
            result = carFacade.findAllActivatedCars();
        }else{
            result = carFacade.findAllDeactivatedCars();
        }
        if(result == null) {
            result = Collections.emptyList();
            return result;
        }
        if(branchId>=0){
            return result.stream().filter(x -> {
            	if(x.getRegionalBranch()==null) {
            		return false;
            	}else {
                	return x.getRegionalBranch().getId()==branchId;
            	}
            	})
            		.collect(Collectors.toList());
        }else{
            return result;
        }
    }

    @RequestMapping(value = ApiDefinition.Car.ID , method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public final CarDTO getCarById(@PathVariable(ApiDefinition.Car.PATH_ID) long id) {
        CarDTO result = carFacade.findCarById(id);
        if (result != null) {
            return result;
        } else {
            throw new ResourceNotFound();
        }
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public final CarOperationResult createCar(@Valid @RequestBody CarDTO car,
                                              BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new ResourceNotValid();
        }
        LOG.debug("REST create car: ", car);

        return carFacade.createCar(car);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public final CarOperationResult updateCar(@Valid @RequestBody CarDTO car,
                                              BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            throw new ResourceNotValid();
        }
        LOG.debug("REST update car: ", car);

        return carFacade.updateCar(car);
    }

    @RequestMapping(value = ApiDefinition.Car.ID, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public final CarOperationResult deleteCar(@PathVariable(ApiDefinition.Car.PATH_ID) long id){
        LOG.debug("REST delete car with id: ", id);
        CarDTO result = carFacade.findCarById(id);
        if (result != null) {
            result.setDeactivated(true);
            return carFacade.updateCar(result);
        } else {
            throw new ResourceNotFound();
        }
    }
}
