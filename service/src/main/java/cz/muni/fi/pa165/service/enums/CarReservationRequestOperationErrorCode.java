package cz.muni.fi.pa165.service.enums;

/**
 *
 * @author Martin Miskeje
 */
public enum CarReservationRequestOperationErrorCode {
    UNKNOWN_ERROR, DATABASE_ERROR, CAR_REQUIRED, USER_REQUIRED, START_DATE_REQUIRED, END_DATE_REQUIRED, CAR_NOT_AVAILABLE, END_DATE_BEFORE_START_DATE, RESERVATION_IN_THE_PAST
}
