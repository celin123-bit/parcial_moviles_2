package pe.edu.moivo.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "clients", indices = {@Index(value = "document", unique = true)})
public class Client {
    @PrimaryKey(autoGenerate = true) public long id;
    @NonNull public String name = "";
    @NonNull public String document = "";
    @NonNull public String email = "";
    @NonNull public String phone = "";
}
