package screens.dashboard

import adapters.CategoryAdapter
import adapters.MissingPersonAdapter
import adapters.SliderAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.identity.trace.R
import models.CategoryModel
import models.MissingPersonModel
import screens.missingpersonforms.AddMissingPersonActivity
import screens.missingpersonforms.SearchMissingPersonActivity

class DashboardActivity : ComponentActivity() {

    private lateinit var rvMissingPerson: RecyclerView
    private lateinit var missingPersonAdapter: MissingPersonAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var viewPagerBanner: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        initializeViews()
        setupAdapters()
        setupRecyclerViews()
    }

    private fun initializeViews() {
        rvMissingPerson = findViewById(R.id.rvMissingPersons)
        viewPagerBanner = findViewById(R.id.viewPagerBanner)
        recyclerViewCategory = findViewById(R.id.rvMissingPersonCategory)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }

    private fun setupAdapters() {
        sliderAdapter = SliderAdapter(getBannerList())
        missingPersonAdapter = MissingPersonAdapter(getMissingPersonItems())
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
        return listOf(R.drawable.banner_mp, R.drawable.banner2)
    }

    private fun getMissingPersonItems(): List<MissingPersonModel> {
        return listOf(
            MissingPersonModel("Joe Deline", "Lahore", R.drawable.junaid),
            MissingPersonModel("Item 1", "Description 1", R.drawable.junaid),
            MissingPersonModel("Item 2", "Description 2", R.drawable.junaid),
            MissingPersonModel("Item 3", "Description 3", R.drawable.junaid)
        )
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
                val intent = Intent(this, AddMissingPersonActivity::class.java)
                startActivity(intent)
            }
            "Search Missing Person" -> {
                val intent = Intent(this, SearchMissingPersonActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
