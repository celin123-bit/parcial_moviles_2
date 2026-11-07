package pe.edu.moivo.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "packages")
public class TourPackage {
    @PrimaryKey public long id;
    @NonNull public String name = "";
    @NonNull public String destination = "";
    @NonNull public String description = "";
    @NonNull public String includes = "";
    @NonNull public String duration = "";
    @NonNull public String meetingPoint = "";
    public long priceCents;
    public double latitude;
    public double longitude;
}
