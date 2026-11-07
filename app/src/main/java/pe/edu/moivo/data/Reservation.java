package pe.edu.moivo.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reservations", foreignKeys = {
    @ForeignKey(entity = Client.class, parentColumns = "id", childColumns = "clientId", onDelete = ForeignKey.RESTRICT),
    @ForeignKey(entity = TourPackage.class, parentColumns = "id", childColumns = "packageId", onDelete = ForeignKey.RESTRICT)
}, indices = {@Index("clientId"), @Index("packageId")})
public class Reservation {
    @PrimaryKey(autoGenerate = true) public long id;
    public long clientId;
    public long packageId;
    @NonNull public String date = "";
    public int people;
    // Copia del precio al reservar: los cambios futuros de tarifa no alteran la reserva.
    public long unitPriceCents;
    @NonNull public String status = "PENDIENTE";
    public long createdAt;
}
