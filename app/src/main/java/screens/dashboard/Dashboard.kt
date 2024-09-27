package screens.dashboard

import adapter.ItemsAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.identity.trace.R
import models.ItemsModel

class DashboardActivity : ComponentActivity()  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAdapter: ItemsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        val items = listOf(
            ItemsModel("Item 1", "Description 1", R.drawable.baseline_lock_24),
            ItemsModel("Item 1", "Description 1", R.drawable.bell),
            ItemsModel("Item 1", "Description 1", R.drawable.baseline_lock_24),
            ItemsModel("Item 1", "Description 1", R.drawable.),


        )

        recyclerView = findViewById(R.id.rvMissingPersons)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemsAdapter = ItemsAdapter(items)
        recyclerView.adapter = itemsAdapter
    }
}