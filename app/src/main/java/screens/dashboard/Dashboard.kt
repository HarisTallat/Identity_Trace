package screens.dashboard

import adapters.CategoryAdapter
import adapters.MissingPersonAdapter
import adapters.SliderAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.identity.trace.R
import models.CategoryModel
import models.MissingPersonModel
import screens.SignIn
import screens.emailsupport.EmailSupportActivity
import screens.missingpersonforms.ConsentFormActivity
import screens.missingpersonforms.SearchMissingPersonActivity

class DashboardActivity : ComponentActivity() {

    private lateinit var rvMissingPerson: RecyclerView
    private lateinit var missingPersonAdapter: MissingPersonAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var viewPagerBanner: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        initializeViews()
        setupAdapters()
        setupRecyclerViews()
        bottomNavigationView.itemIconTintList = getColorStateList(R.color.white)
        bottomNavigationView.itemTextColor = getColorStateList(R.color.white)

        // Handle intent that comes from EmailSupportActivity
        val intent = intent
        if (intent.hasExtra("SELECT_TAB")) {
            val selectedTab = intent.getStringExtra("SELECT_TAB")
            if (selectedTab == "HOME") {
                bottomNavigationView.selectedItemId = R.id.nav_home
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate to Home Screen (you can use fragments or new activities)
                    // Example: Load HomeFragment or Activity
                    true
                }
                R.id.nav_support -> {
                    // Navigate to Support Screen
                    val intent = Intent(this, EmailSupportActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, SignIn::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun initializeViews() {
        rvMissingPerson = findViewById(R.id.rvMissingPersons)
        viewPagerBanner = findViewById(R.id.viewPagerBanner)
        recyclerViewCategory = findViewById(R.id.rvMissingPersonCategory)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }

    private fun setupAdapters() {
        sliderAdapter = SliderAdapter(getBannerList())

        // Initialize MissingPersonAdapter with an empty list
        missingPersonAdapter = MissingPersonAdapter(emptyList())

        // Set the adapter to RecyclerView
        rvMissingPerson.adapter = missingPersonAdapter

        // Fetch data asynchronously and update the adapter
        getMissingPersonItems { missingPersons ->
            missingPersonAdapter.updateData(missingPersons) // Update adapter with loaded data
        }

        categoryAdapter = CategoryAdapter(getCategories()) { category ->
            handleCategoryClick(category)
        }
    }


    private fun setupRecyclerViews() {
        recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvMissingPerson.layoutManager = GridLayoutManager(this, 2)
        recyclerViewCategory.adapter = categoryAdapter
        rvMissingPerson.adapter = missingPersonAdapter
        viewPagerBanner.adapter = sliderAdapter
    }

    private fun getBannerList(): List<Int> {
        return listOf(R.drawable.missing_person_banner, R.drawable.banner2)
    }

    private fun getMissingPersonItems(onDataLoaded: (List<MissingPersonModel>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val missingPersonsRef = database.child("users")

        missingPersonsRef.get().addOnSuccessListener { snapshot ->
            val missingPersons = mutableListOf<MissingPersonModel>()
            for (childSnapshot in snapshot.children) {
                val name = childSnapshot.child("name").value.toString()
                val lastKnownLocation = childSnapshot.child("lastKnownLocation").value.toString()
                val imageUrl = childSnapshot.child("imageUrl").value.toString()

                missingPersons.add(MissingPersonModel(name, lastKnownLocation, imageUrl))
            }
            // Call the callback with the loaded data
            onDataLoaded(missingPersons)
        }.addOnFailureListener {
            // Use proper context for the Toast
            Toast.makeText(this, "Failed to load data: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }




    private fun getCategories(): List<CategoryModel> {
        return listOf(
            CategoryModel("Add Missing Person", R.drawable.add_mp),
            CategoryModel("Search Missing Person", R.drawable.mp_search)
        )
    }

    private fun handleCategoryClick(category: CategoryModel) {
        when (category.title) {
            "Add Missing Person" -> {
                val intent = Intent(this, ConsentFormActivity::class.java)
                startActivity(intent)
            }
            "Search Missing Person" -> {
                val intent = Intent(this, SearchMissingPersonActivity::class.java)
                startActivity(intent)
            }
        }
    }
}