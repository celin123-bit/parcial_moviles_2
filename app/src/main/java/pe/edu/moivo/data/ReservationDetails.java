package pe.edu.moivo.data;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ReservationDetails {
    @Embedded public Reservation reservation;
    @Relation(parentColumn = "clientId", entityColumn = "id") public Client client;
    @Relation(parentColumn = "packageId", entityColumn = "id") public TourPackage tour;
}
