package cz.muni.fi.pa165.service.tests;

import cz.muni.fi.pa165.dao.CarDAO;
import cz.muni.fi.pa165.dao.CarReservationRequestDAO;
import cz.muni.fi.pa165.dao.RegionalBranchDAO;
import cz.muni.fi.pa165.dao.UserDAO;
import cz.muni.fi.pa165.entity.Car;
import cz.muni.fi.pa165.entity.CarReservationRequest;
import cz.muni.fi.pa165.entity.RegionalBranch;
import cz.muni.fi.pa165.entity.User;
import cz.muni.fi.pa165.enums.CarReservationRequestState;
import cz.muni.fi.pa165.enums.UserType;
import cz.muni.fi.pa165.service.CarReservationRequestService;
import cz.muni.fi.pa165.service.CarReservationRequestServiceImpl;
import cz.muni.fi.pa165.service.TimeService;
import cz.muni.fi.pa165.tests.support.TestObjectFactory;
import org.hibernate.service.spi.ServiceException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class CarReservationRequestServiceTest extends BaseServiceTest {

    @Mock
    private CarReservationRequestDAO carReservationRequestDAO;

    @Mock
    private UserDAO userDao;

    @Mock
    private CarDAO carDao;

    @Mock
    private TimeService timeService;

    @Mock
    private RegionalBranchDAO regionalBranchDao;

    @InjectMocks
    private CarReservationRequestService carReservationRequestService = new CarReservationRequestServiceImpl();

    private final TestObjectFactory objectFactory = new TestObjectFactory();

    private LocalDateTime currentTime;
    private Car car1;
    private Car car2;
    private User user1;
    private User user2;
    private CarReservationRequest reservation1;
    private CarReservationRequest reservation2;

    @BeforeMethod
    public void setup() throws ServiceException {

        currentTime = LocalDateTime.now();

        user1 = objectFactory.createUser("firstUser", UserType.USER);
        user1.setId(Long.valueOf(1));
        user2 = objectFactory.createUser("secondUser", UserType.USER);
        user2.setId(Long.valueOf(2));

        userDao.save(user1);
        userDao.save(user2);

        car1 = objectFactory.createCar("sampleCar");
        car1.setId(Long.valueOf(1));
        car2 = objectFactory.createCar("sampleCar2");
        car2.setId(Long.valueOf(2));

        carDao.save(car1);
        carDao.save(car2);

        reservation1 = objectFactory.createReservationRequest(car1, user1, currentTime, currentTime.plus(4, ChronoUnit.DAYS), CarReservationRequestState.CREATED);
        reservation1.setId(Long.valueOf(1));
        reservation2 = objectFactory.createReservationRequest(car2, user2, currentTime, currentTime.plus(2, ChronoUnit.DAYS), CarReservationRequestState.CREATED);

        when(carReservationRequestDAO.findOne(Long.valueOf(1)))
                .thenReturn(reservation1);

        when(carReservationRequestDAO.findAll())
                .thenReturn(Arrays.asList(reservation1, reservation2));
        when(userDao.findOne(1L))
                .thenReturn(user1);
        when(userDao.findOne(2L))
                .thenReturn(user2);

        when(carDao.findOne(1L))
                .thenReturn(car1);
        when(carDao.findOne(2L))
                .thenReturn(car2);

        when(timeService.getCurrentTime())
                .thenReturn(currentTime);

        doAnswer((Answer<Object>) (InvocationOnMock invocation) -> {
            Object argument = invocation.getArguments()[0];
            if (argument == null) {
                throw new IllegalArgumentException();
            }

            CarReservationRequest carReservationRequest = (CarReservationRequest) argument;

            carReservationRequest.setId(Long.valueOf(1));
            return null;
        }).when(carReservationRequestDAO).save(any(CarReservationRequest.class));

        doAnswer((Answer<Void>) (InvocationOnMock invocation) -> {
            Object argument = invocation.getArguments()[0];

            long userId = (long) argument;
            if (userId < 0) {
                throw new IllegalArgumentException();
            }

            return null;
        }).when(carReservationRequestDAO).delete(any(long.class));
    }

    @BeforeClass
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void createReservation() throws IllegalArgumentException {
        carReservationRequestService.create(reservation2);
        assertThat(reservation2.getId()).isGreaterThan(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createNullReservation() throws IllegalArgumentException {
        carReservationRequestService.create(null);
    }

    @Test
    public void updateReservation() throws IllegalArgumentException {
        reservation1.setCar(car2);
        carReservationRequestService.update(reservation1);
        assertThat(reservation1.getCar()).isEqualTo(car2);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void updateNullReservation() throws IllegalArgumentException {
        carReservationRequestService.update(null);
    }

    @Test
    public void deleteNonExistingReservation() throws IllegalArgumentException {
        CarReservationRequest deletedReservationRequest = carReservationRequestService.delete(-1L);
        assertThat(deletedReservationRequest).isNull();
    }

    @Test
    public void deleteReservation() throws IllegalArgumentException {
        CarReservationRequest deletedReservation = carReservationRequestService.delete(reservation1.getId());
        Mockito.verify(carReservationRequestDAO, Mockito.times(1)).delete(reservation1);
        assertThat(deletedReservation).isEqualToComparingFieldByField(reservation1);
    }

    @Test
    public void getAllForRegionalBranchTest() {
        RegionalBranch regionalBranch = objectFactory.createRegionalBranch("testBranch");
        regionalBranch.addCar(car1);
        regionalBranchDao.save(regionalBranch);

        List<CarReservationRequest> foundReservations = carReservationRequestDAO.getAllForRegionalBranch(reservation1.getId(), currentTime, currentTime.plus(4, ChronoUnit.DAYS));

        assertThat(foundReservations.contains(reservation1));
    }
}
