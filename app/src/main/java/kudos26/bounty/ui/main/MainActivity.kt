package kudos26.bounty.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kudos26.bounty.R
import kudos26.bounty.core.Activity
import kudos26.bounty.utils.Constants.Companion.TOOLBAR_MARGIN_HORIZONTAL
import kudos26.bounty.utils.Constants.Companion.TOOLBAR_MARGIN_VERTICAL

class MainActivity : Activity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initNavigation()
    }

    private fun initToolbar() {
        (toolbarWrapper?.layoutParams as CoordinatorLayout.LayoutParams).apply {
            setMargins(TOOLBAR_MARGIN_HORIZONTAL,
                    TOOLBAR_MARGIN_VERTICAL + statusBarHeight,
                    TOOLBAR_MARGIN_HORIZONTAL, TOOLBAR_MARGIN_VERTICAL)
        }
        setSupportActionBar(toolbar)
    }

    private fun initNavigation() {
        val navController = (supportFragmentManager.findFragmentById(R.id.app_navigation_fragment) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.home), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setPadding(0, statusBarHeight, 0, 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        return when (item.itemId) {
            else -> onOptionsItemSelected(item)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.onNavDestinationSelected(findNavController(R.id.app_navigation_fragment))
        when (item.itemId) {
            R.id.switch_account -> switchAccount()
            R.id.dark_mode -> switchTheme()
            else -> return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.app_navigation_fragment).navigateUp(appBarConfiguration)
    }

}
