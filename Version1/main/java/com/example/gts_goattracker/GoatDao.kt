import androidx.room.*
import com.example.gts_goattracker.Goat

@Dao
interface GoatDao {

    @Insert
    suspend fun insert(goat: Goat)

    @Query("SELECT * FROM Goat WHERE id = :id")
    suspend fun getById(id: Int): Goat?

    @Query("SELECT * FROM Goat")
    suspend fun getAll(): List<Goat>

    @Delete
    suspend fun delete(goat: Goat)

    @Update
    suspend fun update(goat: Goat)
}
