package com.example.flightsearchapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.room.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// --- Entities ---
@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "iata_code") val iataCode: String,
    val name: String,
    val passengers: Int
)

@Entity(tableName = "favourite")
data class Favourite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "departure_code") val departureCode: String,
    @ColumnInfo(name = "destination_code") val destinationCode: String
)

// --- Flight Data Class ---
data class Flight(
    val departureCode: String,
    val departureName: String,
    val destinationCode: String,
    val destinationName: String
)

// --- DAO ---
@Dao
interface FlightDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE :query OR name LIKE :query ORDER BY passengers DESC")
    suspend fun searchAirports(query: String): List<Airport>

    @Query("SELECT * FROM airport WHERE iata_code != :excludeCode ORDER BY passengers DESC")
    suspend fun getAllAirportsExcept(excludeCode: String): List<Airport>

    @Query("SELECT * FROM airport WHERE iata_code = :code")
    suspend fun getAirportByCode(code: String): Airport?

    @Query("SELECT * FROM favourite")
    suspend fun getAllFavourites(): List<Favourite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(favourite: Favourite)
}

// --- Database ---
@Database(entities = [Airport::class, Favourite::class], version = 1)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile private var INSTANCE: FlightDatabase? = null

        fun getDatabase(context: Application): FlightDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    FlightDatabase::class.java,
                    "flight_search.db"
                )
                    // Uncomment below if you are using a pre-populated asset
                    // .createFromAsset("flight_search.db")
                    .build().also { INSTANCE = it }
            }
        }
    }
}

// --- ViewModel ---
class FlightViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = FlightDatabase.getDatabase(application).flightDao()

    val searchResults = mutableStateOf<List<Airport>>(emptyList())
    val flights = mutableStateOf<List<Flight>>(emptyList())
    val favourites = mutableStateOf<List<Favourite>>(emptyList())
    val selectedAirport = mutableStateOf<Airport?>(null)
    val showingFlights = mutableStateOf(false)

    fun searchAirports(query: String) {
        viewModelScope.launch {
            if (query.isNotBlank()) {
                searchResults.value = dao.searchAirports("%$query%")
                showingFlights.value = false
            } else {
                searchResults.value = emptyList()
                flights.value = emptyList()
                selectedAirport.value = null
                showingFlights.value = false
            }
        }
    }

    fun selectAirport(airport: Airport) {
        selectedAirport.value = airport
        showingFlights.value = true
        searchResults.value = emptyList()

        viewModelScope.launch {
            val destinations = dao.getAllAirportsExcept(airport.iataCode)
            flights.value = destinations.map { dest ->
                Flight(
                    departureCode = airport.iataCode,
                    departureName = airport.name,
                    destinationCode = dest.iataCode,
                    destinationName = dest.name
                )
            }
        }
    }

    fun addToFavourites(flight: Flight) {
        viewModelScope.launch {
            val favourite = Favourite(
                departureCode = flight.departureCode,
                destinationCode = flight.destinationCode
            )
            dao.addFavourite(favourite)
            loadFavourites()
        }
    }

    fun loadFavourites() {
        viewModelScope.launch {
            favourites.value = dao.getAllFavourites()
        }
    }

    fun clearSearch() {
        searchResults.value = emptyList()
        flights.value = emptyList()
        selectedAirport.value = null
        showingFlights.value = false
    }
}

// --- ViewModel Factory ---
class FlightViewModelFactory(private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// --- Main UI ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(viewModel: FlightViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults
    val flights by viewModel.flights
    val favourites by viewModel.favourites
    val selectedAirport by viewModel.selectedAirport
    val showingFlights by viewModel.showingFlights

    LaunchedEffect(Unit) {
        viewModel.loadFavourites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                viewModel.searchAirports(query)
            },
            label = { Text("Search airports") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        viewModel.clearSearch()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            searchQuery.isNotEmpty() && searchResults.isNotEmpty() -> {
                Text(
                    "Select an airport:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(searchResults) { airport ->
                        AirportSuggestionCard(
                            airport = airport,
                            onClick = {
                                searchQuery = ""
                                viewModel.selectAirport(airport)
                            }
                        )
                    }
                }
            }

            showingFlights && selectedAirport != null -> {
                Text(
                    "Flights from ${selectedAirport?.iataCode} - ${selectedAirport?.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(flights) { flight ->
                        FlightCard(
                            flight = flight,
                            onSaveFavourite = { viewModel.addToFavourites(flight) }
                        )
                    }
                }
            }

            else -> {
                Text(
                    "Favourite Destinations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (favourites.isEmpty()) {
                    Text(
                        "No favourite destinations saved yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn {
                        items(favourites) { favourite ->
                            FavouriteCard(favourite = favourite)
                        }
                    }
                }
            }
        }
    }
}


// --- UI Components ---
@Composable
fun AirportSuggestionCard(airport: Airport, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "${airport.iataCode} - ${airport.name}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                "${NumberFormat.getNumberInstance(Locale.US).format(airport.passengers)} passengers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FlightCard(flight: Flight, onSaveFavourite: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${flight.departureCode} → ${flight.destinationCode}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${flight.departureName} to ${flight.destinationName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onSaveFavourite,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun FavouriteCard(favourite: Favourite) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            "${favourite.departureCode} → ${favourite.destinationCode}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

// --- MainActivity ---
class MainActivity : ComponentActivity() {
    private val viewModel: FlightViewModel by viewModels {
        FlightViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlightSearchScreen(viewModel)
                }
            }
        }
    }
}
