package cz.muni.fi.pa165;

import cz.muni.fi.pa165.entity.Car;
import cz.muni.fi.pa165.entity.CarReservationRequest;
import cz.muni.fi.pa165.entity.RegionalBranch;
import cz.muni.fi.pa165.entity.User;
import cz.muni.fi.pa165.enums.CarReservationRequestState;
import cz.muni.fi.pa165.enums.UserType;
import cz.muni.fi.pa165.service.CarReservationRequestService;
import cz.muni.fi.pa165.service.CarService;
import cz.muni.fi.pa165.service.RegionalBranchService;
import cz.muni.fi.pa165.service.UserService;
import org.dom4j.Branch;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class InitializerImpl implements Initializer {

    private LocalDateTime currentTime = LocalDateTime.now().plus(1, ChronoUnit.HOURS);

    @Inject
    private CarService carService;

    @Inject
    private RegionalBranchService branchService;

    @Inject
    private UserService userService;

    @Inject
    private CarReservationRequestService reservationsService;

    @Override
    public void loadData() {
        //create Cars
        Car audi = this.createCar("Audi A3");
        Car bmw = this.createCar("BMW 3");
        Car volvo = this.createCar("Volvo XC90");
        Car skoda = this.createCar("Skoda Fabia");
        Car ford = this.createCar("Ford Focus");
        Car mazda = this.createCar("Mazda 3");

        //create Users
        User admin = this.createUser("admin", "admin", UserType.ADMIN);
        User mato = this.createUser("Mato","testTest", UserType.BRANCH_MANAGER);
        User standa = this.createUser("Standa", "testTest", UserType.BRANCH_MANAGER);
        User denis = this.createUser("Denis", "testTest", UserType.USER);
        User tomas = this.createUser("Tomas", "testTest", UserType.USER);
        User pepa = this.createUser("Pepa", "testTest", UserType.USER);
        User franta = this.createUser("Franta", "testTest", UserType.USER);
        User maria = this.createUser("Maria", "testTest", UserType.USER);

        //create Branches
        List<Car> headquarterCars = new ArrayList<>();
        headquarterCars.add(mazda);

        List<User> headquartersEmployees = new ArrayList<>();
        headquartersEmployees.add(admin);
        headquartersEmployees.add(standa);
        headquartersEmployees.add(pepa);

        List<Car> brnoCars = new ArrayList<>();
        brnoCars.add(skoda);
        brnoCars.add(ford);
        brnoCars.add(audi);
        brnoCars.add(bmw);
        brnoCars.add(volvo);

        List<User> brnoEmployees = new ArrayList<>();
        brnoEmployees.add(tomas);
        brnoEmployees.add(denis);
        brnoEmployees.add(mato);
        brnoEmployees.add(franta);

        RegionalBranch headquarters = this.createRegionalBranch("Headquarters",headquartersEmployees, headquarterCars, null, null );
        RegionalBranch brno = this.createRegionalBranch("Brno", brnoEmployees, brnoCars,null, headquarters);
        headquarters.addChild(brno);

        //create Reservations
        CarReservationRequest first = this.createReservationRequest(audi, denis, currentTime, currentTime.plus(1, ChronoUnit.DAYS), CarReservationRequestState.CREATED);
        CarReservationRequest second = this.createReservationRequest(ford, mato, currentTime, currentTime.plus(1, ChronoUnit.DAYS), CarReservationRequestState.APPROVED);
        CarReservationRequest third = this.createReservationRequest(volvo, franta, currentTime, currentTime.plus(1, ChronoUnit.DAYS), CarReservationRequestState.CREATED);

        this.createReservationRequest(mazda, standa, currentTime, currentTime.plus(1, ChronoUnit.DAYS), CarReservationRequestState.APPROVED);
    }

    private Car createCar(String name) {
        Car c = new Car();
        c.setName(name);
        c.setCreationDate(currentTime);
        c.setModificationDate(currentTime);
        carService.createCar(c);
        return c;
    }

    private User createUser(String username, String password, UserType type) {
        User u = new User();
        u.setUserName(username);
        u.setPassword(password);
        u.setCreationDate(currentTime);
        u.setModificationDate(currentTime);
        u.setType(type);
        userService.create(u, password);
        return u;
    }

    private RegionalBranch createRegionalBranch(String name, List<User> employees, List<Car> cars, List<RegionalBranch> children, RegionalBranch parent) {
        RegionalBranch b = new RegionalBranch();
        b.setName(name);
        b.setParent(parent);
        b.setCreationDate(currentTime);
        b.setModificationDate(currentTime);
        if (employees != null) {
            for (User employee : employees) {
                b.addEmployee(employee);
            }
        }
        if (cars != null) {
            for (Car car : cars) {
                b.addCar(car);
            }
        }
        if (children != null) {
            for (RegionalBranch child : children) {
                b.addChild(child);
            }
        }

        branchService.create(b);
        return b;
    }

    private CarReservationRequest createReservationRequest(Car car, User user, LocalDateTime reservationStartDate, LocalDateTime reservationEndDate, CarReservationRequestState state) {
        CarReservationRequest reservationRequest = new CarReservationRequest();
        reservationRequest.setCar(car);
        reservationRequest.setUser(user);
        reservationRequest.setReservationStartDate(reservationStartDate);
        reservationRequest.setReservationEndDate(reservationEndDate);
        reservationRequest.setCreationDate(currentTime);
        reservationRequest.setModificationDate(currentTime);
        reservationsService.create(reservationRequest);
        if(CarReservationRequestState.APPROVED.equals(state) || CarReservationRequestState.DENIED.equals(state)){
            reservationRequest.setState(state);
            reservationsService.update(reservationRequest); //because after create all reservations are set to created
        }
        return reservationRequest;
    }
}
