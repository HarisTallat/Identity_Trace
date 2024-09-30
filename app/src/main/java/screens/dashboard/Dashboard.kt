package screens.dashboard

import adapter.ItemsAdapter
import adapter.SliderAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.identity.trace.R
import models.ItemsModel

class DashboardActivity : ComponentActivity()  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var recyclerViewCategory: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        val bannerList = listOf(
            R.drawable.banner_mp,
            R.drawable.banner2
        )

        val items = listOf(
            ItemsModel("Joe Deline", "Description 1", R.drawable.baseline_lock_24),
            ItemsModel("Item 1", "Description 1", R.drawable.bell),
            ItemsModel("Item 1", "Description 1", R.drawable.baseline_lock_24),

        )

        recyclerView = findViewById(R.id.rvMissingPersons)
        viewPager2 = findViewById(R.id.viewPager2)
        recyclerViewCategory = findViewById(R.id.rvMissingPersons1);
        recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns
        itemsAdapter = ItemsAdapter(items)
        recyclerView.adapter = itemsAdapter
        recyclerViewCategory.adapter = itemsAdapter

        sliderAdapter = SliderAdapter(bannerList)
        viewPager2.adapter = sliderAdapter
    }
}